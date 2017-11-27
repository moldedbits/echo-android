package com.moldedbits.echo.chat.commands;

import android.content.Context;
import android.util.Log;

import com.moldedbits.echo.chat.EchoConfiguration;
import com.moldedbits.echo.chat.core.MessageType;
import com.moldedbits.echo.chat.core.MqttSession;
import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.database.DatabaseExtension;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by viveksingh
 * on 10/09/17.
 */

abstract class AbstractCommand {
    private Context context;
    protected final List<Disposable> disposableBucket = new ArrayList<>();

    abstract void execute();

    abstract void onSuccess();

    abstract void onFailure();

    public AbstractCommand(Context context) {
        this.context = context;
    }

    protected void send(final EchoMessage echoMessage, MqttMessage message, String topic) {
        try {
            // Sending message
            MqttSession.getInstance().publish(message, topic, context, new IMqttActionListener() {
                @Override
                public void onSuccess(final IMqttToken asyncActionToken) {
                    Log.i(getClass().toString(), "Sent Message success");
                    saveMessage(echoMessage);
                    // TODO: 14/06/17 check for msg non delivery, some sort of push based
                    // keep alive message
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.e(getClass().toString(), "Sent Message error", exception);
                    exception.printStackTrace();
                    AbstractCommand.this.onFailure();
                }
            });
        } catch (Exception e) {
            Log.e(getClass().toString(), e.getMessage());
        }

    }

    private void saveMessage(final EchoMessage message) {
        DatabaseExtension<EchoMessage> ext = EchoConfiguration.getInstance().getExtension();
        ext.storeMessage(message).subscribe(new SingleObserver<EchoMessage>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposableBucket.add(d);
            }

            @Override
            public void onSuccess(EchoMessage message) {
                AbstractCommand.this.onSuccess();
            }

            @Override
            public void onError(Throwable e) {
                AbstractCommand.this.onFailure();
                e.printStackTrace();
            }
        });
    }
}
