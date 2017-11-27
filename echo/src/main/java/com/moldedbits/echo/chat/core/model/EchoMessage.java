package com.moldedbits.echo.chat.core.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.moldedbits.echo.chat.core.MessageType;

public class EchoMessage implements Parcelable {

    /**
     * unique Id for a message
     */
    private String messageId;

    /**
     * Message data
     */
    private String message;

    /**
     * Client Id of the sender of this message
     */
    private String senderClientId;

    /**
     * Topic on which this message is sent
     */
    private String topic;

    /**
     * Type of the message whether it is image , document or text message ect
     */
    private MessageType messageType;

    /**
     * Time stamp of the sent message
     */
    private long messageSentDuration;

    /**
     * If this message is incoming or not
     * it is not to be used while sending a message but It is required when message is received
     */
    private boolean incoming;

    /**
     * In this field one can send all the extra data that is not provided in this EchoApi and can
     * be used to customize the chat according to user needs
     */
    private String extras;

    /**
     * Any attachment whether its a Image, Document or Video. Convert the file to byte array before sending
     */
    private String attachment;

    public EchoMessage() {
    }

    protected EchoMessage(Parcel in) {
        messageId = in.readString();
        message = in.readString();
        senderClientId = in.readString();
        topic = in.readString();
        messageSentDuration = in.readLong();
        incoming = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(messageId);
        dest.writeString(message);
        dest.writeString(senderClientId);
        dest.writeString(topic);
        dest.writeLong(messageSentDuration);
        dest.writeByte((byte) (incoming ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EchoMessage> CREATOR = new Creator<EchoMessage>() {
        @Override
        public EchoMessage createFromParcel(Parcel in) {
            return new EchoMessage(in);
        }

        @Override
        public EchoMessage[] newArray(int size) {
            return new EchoMessage[size];
        }
    };

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

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public long getMessageSentDuration() {
        return messageSentDuration;
    }

    public void setMessageSentDuration(long messageSentDuration) {
        this.messageSentDuration = messageSentDuration;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public boolean isIncoming() {
        return incoming;
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

    public static class Builder {

        private String messageId;
        private String message;
        private String senderClientId;
        private String topic;
        private MessageType messageType;
        private long messageSentDuration;
        private String extras;
        private String attachment;

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder setTopic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder setMessageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder setSenderClientId(String id) {
            senderClientId = id;
            return this;
        }

        public Builder setMessageSentDuration(long duration) {
            messageSentDuration = duration;
            return this;
        }



        /**
         * Serialize all the objects that are needed to be used to customize the chat according
         * to client requirement
         *
         * @param extras serialized string value for extra objects that are not provided
         * @return builder
         */
        public Builder setExtras(String extras) {
            this.extras = extras;
            return this;
        }

        public Builder setAttachment(String attachment) {
            this.attachment = attachment;
            return this;
        }

        public EchoMessage build() {
            EchoMessage echoMessage = new EchoMessage();

            // modified data
            echoMessage.setMessage(this.message);
            echoMessage.setMessageId(this.messageId);
            echoMessage.setTopic(this.topic);
            echoMessage.setMessageType(this.messageType);
            echoMessage.setMessageSentDuration(this.messageSentDuration);
            echoMessage.setSenderClientId(this.senderClientId);
            echoMessage.setExtras(this.extras);
            echoMessage.setAttachment(this.attachment);

            return echoMessage;
        }
    }
}
