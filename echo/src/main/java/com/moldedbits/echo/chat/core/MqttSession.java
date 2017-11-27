package com.moldedbits.echo.chat.core;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.core.model.MqttAck;
import com.moldedbits.echo.chat.core.model.request.ConnectionRequest;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttSession {

    private static final int CONNECTION_TIMEOUT = 40;
    private static MqttSession sInstance;
    private ConnectionRequest mConnection;
    private MqttAsyncClient mClient;
    private IMqttToken mToken;
    private MqttConnectOptions mConnectOptions;
    private MqttCallback mCallback;

    public ConnectionStates getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ConnectionStates currentState) {
        this.currentState = currentState;
    }

    private ConnectionStates currentState;

    private MqttSession() {
        currentState = ConnectionStates.DISCONNECTED;
    }

    public static MqttSession getInstance() {
        if (sInstance == null) {
            sInstance = new MqttSession();
        }
        return sInstance;
    }

    public void setCallback(MqttCallback callback) {
        mCallback = callback;
    }

    public MqttAsyncClient getClient() {
        return mClient;
    }

    public boolean isSessionValid() {
        return mClient != null && mClient.isConnected();
    }

    public IMqttToken createSession(Context context, ConnectionRequest request,
                                    IMqttActionListener listener) throws MqttException {
        if (isSessionValid()) {
            disconnect();
        }
        Log.e("mqtt",
                "client_id-->" + request.getClientId() + " Server Url-->" + request.getServerUri() +
                        " clean session-->" + request.isCleanSession() + " connection timeout-->" +
                        CONNECTION_TIMEOUT + " Keep alive interval-->" + request.getKeepAlive() +
                        " Last Will Message-->" + request.getLastWillMessage() +
                        " Last will qos-->" +
                        request.getLastWillQos());
        mConnection = request;
        currentState = ConnectionStates.CONNECTING;
        if (!isSessionValid()) {
            mClient = new MqttAsyncClient(mConnection.getServerUri(), mConnection.getClientId(),
                    mConnection.getClientPersistence());

            mConnectOptions = new MqttConnectOptions();
            mConnectOptions.setCleanSession(mConnection.isCleanSession());
            mConnectOptions.setConnectionTimeout(CONNECTION_TIMEOUT);
            mConnectOptions.setKeepAliveInterval(mConnection.getKeepAlive());

            EchoMessage echoMessage = new EchoMessage();
            echoMessage.setSenderClientId(request.getClientId());
            echoMessage.setTopic(MqttService.LAST_WILL_TOPIC);
            echoMessage.setMessage(request.getLastWillMessage());
            echoMessage.setIncoming(false);
            echoMessage.setMessageType(MessageType.LWTD);

            mClient.setCallback(mCallback);
            mToken = mClient.connect(mConnectOptions, context, listener);

            currentState =
                    mToken.isComplete() ? ConnectionStates.CONNECTED :
                            ConnectionStates.DISCONNECTED;
        }
        return mToken;
    }

    public IMqttToken subscribeToTopic(String[] topic, Context context,
                                       IMqttActionListener listener) throws MqttException {
        if (isSessionValid()) {
            int[] qos = new int[topic.length];
            // setting qos for each topic
            for (int i = 0; i < topic.length; i++) {
                qos[i] = 2;
            }

            IMqttToken token = mClient.subscribe(topic, qos, context, listener);
            return token;
        }
        return null;
    }

    public IMqttToken subscribeToTopic(String topic, Context context,
                                       IMqttActionListener listener) throws MqttException {
        if (isSessionValid()) {
            IMqttToken token = mClient.subscribe(topic, 2, context, listener);
            return token;
        }
        return null;
    }

    public IMqttDeliveryToken publish(EchoMessage echoMessage, Context invocationContext,
                                      IMqttActionListener listener) throws MqttException {
        MqttMessage mqttMessage;
        if (isSessionValid()) {
            mqttMessage = generateMqttPayload(echoMessage);
            IMqttDeliveryToken deliveryToken =
                    mClient.publish(echoMessage.getTopic(), mqttMessage, invocationContext,
                            listener);
            return deliveryToken;
        }
        return null;
    }

    public IMqttDeliveryToken publish(MqttMessage message,
                                      String topic,
                                      Context invocationContext,
                                      IMqttActionListener listener) throws MqttException {
        if (isSessionValid()) {
            IMqttDeliveryToken deliveryToken =
                    mClient.publish(topic, message, invocationContext, listener);
            return deliveryToken;
        }
        return null;
    }

    public static MqttMessage generateMqttPayload(EchoMessage echoMessage) {
        Gson gson = new Gson();
        MqttMessage mqttMessage = new MqttMessage();
        if (echoMessage.getMessageType() == MessageType.P2P_FIRST_MSG) {
            mqttMessage.setRetained(true);
        }
        mqttMessage.setPayload(gson.toJson(echoMessage).getBytes());
        return mqttMessage;
    }


    private synchronized IMqttDeliveryToken publish(String topic,
                                                    EchoMessage echoMessage,
                                                    Context invocationContext,
                                                    IMqttActionListener listener) throws MqttException {
        Gson gson = new Gson();
        MqttMessage mqttMessage = new MqttMessage();
        if (echoMessage.getMessageType() == MessageType.P2P_FIRST_MSG) {
            mqttMessage.setRetained(true);
        }
        mqttMessage.setPayload(gson.toJson(echoMessage).getBytes());
        IMqttDeliveryToken deliveryToken =
                mClient.publish(topic, mqttMessage, invocationContext, listener);

        return deliveryToken;
    }

    public IMqttDeliveryToken publishAck(String topic, MqttAck mqttAck, Context invocationContext,
                                         IMqttActionListener listener) throws MqttException {
        try {
            if (isSessionValid()) {
                EchoMessage echoMessage = new EchoMessage();
                echoMessage.setSenderClientId(mClient.getClientId());
                echoMessage.setTopic(topic);
                echoMessage.setMessage(new Gson().toJson(mqttAck));
                echoMessage.setIncoming(false);
                echoMessage.setMessageType(MessageType.SYS_ACK);

                return publish(topic, echoMessage, invocationContext, listener);
            }
        } catch (Exception e) {
            Log.e("Exceptio", e.getMessage(), e);
        }
        return null;
    }


    public void disconnect() {
        try {
            if (mClient != null) {
                mClient.disconnect();
                Log.e("mqtt", "disconnected");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}