package com.moldedbits.echo.chat.core.model;

import org.eclipse.paho.client.mqttv3.IMqttToken;

/**
 * Used in a edge case where receiver has network connection but is not receiving messages
 * because chat server connection is broken.
 * We send a push to receiver after 30 seconds. When receiver taps on that push, connection is
 * re-established
 */
public class QueuedMessage {

    public IMqttToken getToken() {
        return token;
    }

    public void setToken(IMqttToken token) {
        this.token = token;
    }
    
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    private IMqttToken token;
    private String messageId;
}
