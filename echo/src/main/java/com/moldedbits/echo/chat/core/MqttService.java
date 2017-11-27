package com.moldedbits.echo.chat.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.moldedbits.echo.chat.EchoConfiguration;
import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.core.model.MqttAck;
import com.moldedbits.echo.chat.core.model.request.ConnectionRequest;
import com.moldedbits.echo.chat.utils.Constants;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

public class MqttService extends Service
        implements MqttCallback, IMqttActionListener {
    static final String LAST_WILL = "%1s IS DISCONNECTED";
    static final String LAST_WILL_TOPIC = "WILL/TOPIC";
    public static final String CHAT_MESSAGE_KEY = "chat_message_key";
    public static int count = 1;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class ChatServiceBinder extends Binder {
        MqttService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MqttService.this;
        }
    }

    private String[] topics = {"world"};

    public MqttService() {
        super();
    }

    /**
     * When binding to the service, we return an interface to our messenger
     * for sending messages to the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    /**
     * Keeps track of all current registered clients.
     */
    List<Messenger> mClients = new ArrayList<>();
    /**
     * Holds last value set by a client.
     */
    int mValue = 0;

    /**
     * Command to the service to register a client, receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client where callbacks should be sent.
     */
    public static final int MSG_REGISTER_CLIENT = 1;

    /**
     * Command to the service to unregister a client, ot stop receiving callbacks
     * from the service.  The Message's replyTo field must be a Messenger of
     * the client as previously given with MSG_REGISTER_CLIENT.
     */
    public static final int MSG_UNREGISTER_CLIENT = 2;

    /**
     * Command to service to set a new value.  This can be sent to the
     * service to supply a new value, and will be sent by the service to
     * any registered clients with the new value.
     */
    public static final int CHAT_MSG_RECEIVE = 3;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClients.add(msg.replyTo);
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClients.remove(msg.replyTo);
                    break;
                //                case CHAT_MSG_RECEIVE:
                //                    mValue = msg.arg1;
                //                    for (int i = mClients.size() - 1; i >= 0; i--) {
                //                        try {
                //                            mClients.get(i).send(Message.obtain(null,
                //                                    CHAT_MSG_RECEIVE, mValue, 0));
                //                        } catch (RemoteException e) {
                //                            // The client is dead.  Remove it from the list;
                //                            // we are going through the list from back to front
                //                            // so this is safe to do inside the loop.
                //                            mClients.remove(i);
                //                        }
                //                        break;
                //                    }
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new IncomingHandler());

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //        clientId = intent.getExtras().getString(Constants.KEY_CLIENT_ID);
        //        serverUrl = intent.getExtras().getString(Constants.KEY_SERVER_URI);
        //        topics = intent.getExtras().getStringArray(Constants.KEY_TOPIC);
        MqttSession.getInstance().setCurrentState(ConnectionStates.NONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                connectMqttClient();
            }
        }).start();
        //        PollingJob.scheduleJob();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (MqttSession.getInstance().isSessionValid()) {
            MqttSession.getInstance().disconnect();
        }
        //        PollingJob.cancelJob();
        Log.e("mqtt", "mqtt service destroyed");

    }

    private void connectMqttClient() {
        Log.e("mqtt", "Initiating server connection");
        MqttSession mqttSession = MqttSession.getInstance();
        mqttSession.setCallback(this);
        ConnectionRequest request = new ConnectionRequest();
        mqttSession.setCurrentState(ConnectionStates.CONNECTING);
        if (!mqttSession.isSessionValid()) {
            EchoConfiguration config = EchoConfiguration.getInstance();
            request.setClientId(config.getClientId());

            int[] qos = new int[topics.length];
            for (int i = 0; i < topics.length; i++) {
                qos[i] = 0;
            }

            request.setQos(qos);
            request.setCleanSession(config.isCleanSession());
            request.setServerUri(config.getServerUri());
            request.setLastWillMessage(config.getLastWillMessage());
            request.setLastWillQos(config.getLastWillQos());
            request.setLastWillTopic(config.getLastWillTopic());
            request.setClientPersistence(new MemoryPersistence());
            request.setKeepAlive(10000);
            try {
                mqttSession.createSession(this, request, this);
            } catch (Exception e) {
                mqttSession.setCurrentState(ConnectionStates.DISCONNECTED);
            }
        } else {
            Log.e(getClass().toString(), "Already connected");
            mqttSession.setCurrentState(ConnectionStates.CONNECTED);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        cause.printStackTrace();
        Log.e("mqtt", "Connection Lost -->" + cause.getMessage());
        MqttSession.getInstance().setCurrentState(ConnectionStates.DISCONNECTED);
        stopSelf();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.i("mqtt", "message arrived-->" + message.toString());

        final EchoMessage echoMessage = ChatUtils.parseMessage(message.toString(),
                EchoMessage.class);
        if (echoMessage.getSenderClientId() != null && !echoMessage.getSenderClientId()
                .contentEquals(EchoConfiguration.getInstance().getClientId())) {
            echoMessage.setIncoming(true);

            EchoConfiguration.getInstance().getExtension().storeMessage(echoMessage).subscribe(
                    new SingleObserver<EchoMessage>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(EchoMessage message) {
                            notifyActivity(message);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }
                    });
        }
    }

    private void notifyActivity(EchoMessage echoMessage) {
        for (int i = mClients.size() - 1; i >= 0; i--) {
            try {
                Message message1 = Message.obtain(null,
                        CHAT_MSG_RECEIVE, mValue, 0);
                Bundle bundle = new Bundle();
                bundle.putParcelable(CHAT_MESSAGE_KEY, echoMessage);
                message1.setData(bundle);
                mClients.get(i).send(message1);
            } catch (RemoteException e) {
                // The client is dead.  Remove it from the list;
                // we are going through the list from back to front
                // so this is safe to do inside the loop.
                mClients.remove(i);
            }
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        if (asyncActionToken.isComplete()) {
            Log.i("mqtt", "Successfully Connected to mqtt server");
            MqttSession.getInstance().setCurrentState(ConnectionStates.CONNECTED);

            try {
                Log.d("mqtt_benchmark1", "" + System.currentTimeMillis());
                MqttSession.getInstance()
                        .subscribeToTopic(topics, this, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                Log.i("mqtt", "Successfully subscribe to topic " +
                                        asyncActionToken.getTopics().toString());
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                    Throwable exception) {
                                exception.printStackTrace();
                                Log.e("mqtt", "Could not subscribe to topic " +
                                        asyncActionToken.getTopics()[0] + "because of" +
                                        exception.getMessage());
                            }
                        });
            } catch (MqttException e) {
                e.printStackTrace();
                MqttSession.getInstance().setCurrentState(ConnectionStates.DISCONNECTED);

            }
        }
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        Log.e("mqtt", "Could Not Connect to mqtt server  Cause-->" + exception.getMessage());

        exception.printStackTrace();
        MqttSession.getInstance().setCurrentState(ConnectionStates.DISCONNECTED);

    }

    private void sendAck(String topic, MqttAck mqttAck) {
        try {
            MqttSession.getInstance().publishAck(topic, mqttAck, this, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.e("mqtt", "Ack Sent");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e("mqtt", "Ack Sent failed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        EchoConfiguration config = EchoConfiguration.getInstance();
        restartServiceIntent.putExtra(Constants.KEY_CLIENT_ID, config.getClientId());
        restartServiceIntent.putExtra(Constants.KEY_SERVER_URI, config.getServerUri());
        restartServiceIntent.putExtra(Constants.KEY_TOPIC, topics);
        PendingIntent
                restartServicePendingIntent = PendingIntent
                .getService(getApplicationContext(), 1, restartServiceIntent,
                        PendingIntent.FLAG_ONE_SHOT);
        AlarmManager
                alarmService = (AlarmManager) getApplicationContext().getSystemService(
                Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }
}