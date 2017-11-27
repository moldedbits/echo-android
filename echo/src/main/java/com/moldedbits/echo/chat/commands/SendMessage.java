package com.moldedbits.echo.chat.commands;

import android.content.Context;
import android.util.Log;

import com.moldedbits.echo.chat.core.MessageType;
import com.moldedbits.echo.chat.core.MqttSession;
import com.moldedbits.echo.chat.core.model.EchoMessage;

import org.eclipse.paho.client.mqttv3.MqttMessage;

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

public abstract class SendMessage extends AbstractCommand {

    private EchoMessage message;

    public SendMessage(Context context, EchoMessage message) {
        super(context);
        this.message = message;
    }

    @Override
    void execute() {
        if (message.getMessageType() == MessageType.P2P_ATTACHMENT_DOC
                || message.getMessageType() == MessageType.P2P_ATTACHMENT_IMAGE) {
            // TODO:: show loading dialog
        }
        Single.create(new SingleOnSubscribe<MqttMessage>() {
            @Override
            public void subscribe(SingleEmitter<MqttMessage> e) throws Exception {
                e.onSuccess(MqttSession.generateMqttPayload(message));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<MqttMessage>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(MqttMessage mqttMessage) {
                        send(message, mqttMessage, message.getTopic());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), e.getMessage());
                        e.printStackTrace();
                    }
                });
    }
}
