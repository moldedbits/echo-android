package com.moldedbits.echo.chat.core;

import android.util.Base64;

import com.moldedbits.echo.chat.EchoConfiguration;
import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.utils.FileUtil;

import java.io.File;
import java.io.IOException;

public class EchoMessageFactory {

    public static EchoMessage generateChatOnlyMessage(String topic, String message) {
        return new EchoMessage.Builder()
                .setMessageType(MessageType.P2P_CHAT)
                .setMessage(message)
                .setTopic(topic)
                .setMessageSentDuration(System.currentTimeMillis())
                .setMessageId(ChatUtils.generateMessageId(EchoConfiguration.getInstance()
                        .getClientId()))
                .setSenderClientId(EchoConfiguration.getInstance().getClientId())
                .build();
    }

    public static EchoMessage generateImageOnlyMessage(String topic, File file, String message)
            throws IOException {

        return new EchoMessage.Builder()
                .setMessageType(MessageType.P2P_ATTACHMENT_IMAGE)
                .setMessage(message)
                .setTopic(topic)
                .setSenderClientId(EchoConfiguration.getInstance().getClientId())
                .setMessageId(ChatUtils.generateMessageId(EchoConfiguration.getInstance()
                        .getClientId()))
                .setMessageSentDuration(System.currentTimeMillis())
                .setAttachment(Base64.encodeToString(FileUtil.fileToByte(file),Base64.DEFAULT))
                .setExtras(file.getName())
                .build();
    }

    public static EchoMessage generateSystemAck(String topic, String message) {
        return new EchoMessage.Builder()
                .setMessageType(MessageType.SYS_ACK)
                .setMessage(message)
                .setTopic(topic)
                .setSenderClientId(EchoConfiguration.getInstance().getClientId())
                .setMessageId(ChatUtils.generateMessageId(EchoConfiguration.getInstance()
                        .getClientId()))
                .setMessageSentDuration(System.currentTimeMillis())
                .build();
    }

    public static EchoMessage generateFirstMessage(String topic, String message) {
        return new EchoMessage.Builder()
                .setMessageType(MessageType.P2P_FIRST_MSG)
                .setMessage(message)
                .setTopic(topic)
                .setSenderClientId(EchoConfiguration.getInstance().getClientId())
                .setMessageId(ChatUtils.generateMessageId(EchoConfiguration.getInstance()
                        .getClientId()))
                .setMessageSentDuration(System.currentTimeMillis())
                .build();
    }

}
