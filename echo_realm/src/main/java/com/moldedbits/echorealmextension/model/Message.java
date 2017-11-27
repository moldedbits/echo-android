package com.moldedbits.echorealmextension.model;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Author viveksingh
 * Date 15/06/17.
 */

public class Message extends RealmObject {
    @Index
    @PrimaryKey
    private String messageId;

    private String message;

    private String senderClientId;

    @Index
    private String topic;

    private int messageType;

    private long messageSentDuration;

    private boolean incoming;

    private String extras;

    private String attachment;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderClientId() {
        return senderClientId;
    }

    public void setSenderClientId(String senderClientId) {
        this.senderClientId = senderClientId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public long getMessageSentDuration() {
        return messageSentDuration;
    }

    public void setMessageSentDuration(long messageSentDuration) {
        this.messageSentDuration = messageSentDuration;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public enum ColumnName {

        MESSAGE_ID("messageId"),

        MESSAGE("message"),

        SENDER_CLIENT_ID("senderClientId"),

        TOPIC("topic"),

        MESSAGE_TYPE("messageType"),

        MESSAGE_SENT_DURATION("messageSentDuration"),

        Extras("extras"),

        INCOMING("incoming"),

        ATTACHMENT("attachment");

        String val;

        ColumnName(String name) {
            val = name;
        }

        public String getValue() {
            return val;
        }
    }
}
