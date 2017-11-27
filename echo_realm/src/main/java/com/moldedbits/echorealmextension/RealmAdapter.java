package com.moldedbits.echorealmextension;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.moldedbits.echo.chat.core.MessageType;
import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.database.BaseDatabaseAdapter;
import com.moldedbits.echorealmextension.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Author viveksingh
 * Date 15/06/17.
 */

public class RealmAdapter implements BaseDatabaseAdapter<Message> {

    /**
     * Can not create realm object without any transaction
     */
    @Override
    public Message fromEchoMessage(@NonNull final EchoMessage echoMessage) {
        return null;
    }

    /**
     * Convert Incoming message to Realm message. Please call this method inside a realm transaction
     *
     * @param echoMessage message to be converted from
     * @param message     empty realm message
     * @return realm message values set from incoming message
     */
    public Message fromEchoMessage(@NonNull final EchoMessage echoMessage,
            @NonNull Message message) {
        message.setIncoming(echoMessage.isIncoming());
        message.setMessage(echoMessage.getMessage());
        message.setMessageSentDuration(echoMessage.getMessageSentDuration());
        message.setMessageType(echoMessage.getMessageType().getValue());
        message.setSenderClientId(echoMessage.getSenderClientId());
        message.setTopic(echoMessage.getTopic());
        message.setExtras(echoMessage.getExtras());
        message.setAttachment(echoMessage.getAttachment());

        if (TextUtils.isEmpty(message.getMessageId())) {
            message.setMessageId(echoMessage.getMessageId());
        }
        return message;
    }

    /**
     * Can not create realm object without any transaction
     */
    @Override
    public List<Message> fromEchoMessages(@NonNull final List<EchoMessage> message) {
        return null;
    }

    /**
     * Convert Incoming message to Realm message. Please call this method inside a realm transaction
     *
     * @param echoMessages List of messages to be converted from
     * @param messages     empty list of realm message
     * @return List of realm message values set from incoming message
     */
    public List<Message> fromEchoMessages(@NonNull final List<EchoMessage> echoMessages,
            @NonNull final List<Message> messages) {
        final int size = echoMessages.size();
        for (final EchoMessage echoMessage : echoMessages) {
            messages.add(fromEchoMessage(echoMessage));
        }
        return messages;
    }

    @Override
    public EchoMessage toEchoMessage(@NonNull final Message message) {
        final EchoMessage echoMessage = new EchoMessage();

        echoMessage.setIncoming(message.isIncoming());
        echoMessage.setMessage(message.getMessage());
        echoMessage.setMessageId(message.getMessageId());
        echoMessage.setMessageSentDuration(message.getMessageSentDuration());
        echoMessage.setMessageType(MessageType.valueOf(message.getMessageType()));
        echoMessage.setSenderClientId(message.getSenderClientId());
        echoMessage.setTopic(message.getTopic());
        echoMessage.setExtras(message.getExtras());
        echoMessage.setAttachment(message.getAttachment());

        return echoMessage;
    }

    @Override
    public List<EchoMessage> toEchoMessages(@NonNull final List<Message> messages) {
        final List<EchoMessage> echoMessages = new ArrayList<>(messages.size());
        for (final Message message : messages) {
            echoMessages.add(toEchoMessage(message));
        }
        return echoMessages;
    }

}
