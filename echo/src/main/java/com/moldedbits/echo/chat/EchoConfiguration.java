package com.moldedbits.echo.chat;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;

import com.moldedbits.echo.chat.core.model.EchoMessage;
import com.moldedbits.echo.chat.database.DatabaseExtension;

import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttPingSender;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.Locale;

/**
 * Author viveksingh
 * Date 16/06/17.
 */

public class EchoConfiguration {
    private static EchoConfiguration config;

    /**
     * Identifier of each MQTT client connecting to a MQTT broker
     */

    private String clientId;

    /**
     * Indicates the broker that the client wants to establish a persistent session or not.
     * A persistent session (CleanSession is false) means,
     * that the broker will store all subscriptions for the client and also all missed messages,
     * when subscribing with QoS 1 or 2.If clean session is set to true,
     * the broker won’t store anything for the client and will also remove all information from a
     * previous persistent session
     */

    private boolean cleanSession;

    /**
     * User name for authentication. This is an optional field
     */

    private String userName;

    /**
     * Password for authentication. This is an optional field
     */
    private String password;
    private String lastWillTopic;
    private int lastWillQos;

    /**
     * It allows to notify other clients, when a client disconnects ungracefully
     */
    private String lastWillMessage;

    /**
     * time interval, the clients sends regular PING Request messages to the broker.
     * The broker response with PING Response and this mechanism will allow both sides to determine
     * if the other one is still alive and reachable.
     */
    private int keepAlive = 120;
    private String serverUri;
    private MqttClientPersistence clientPersistence;
    private MqttPingSender pingSender;
    private DatabaseExtension<EchoMessage> extension;
    private int timeOut;
    private String cacheDirectoryName;

    private EchoConfiguration() {

    }

    public static EchoConfiguration getInstance() {
        if (config == null) {
            synchronized (EchoConfiguration.class) {
                if(config == null) {
                    config = new EchoConfiguration();
                }
            }
        }
        return config;
    }

    public DatabaseExtension<EchoMessage> getExtension() {
        return extension;
    }

    public void setExtension(DatabaseExtension<EchoMessage> extension) {
        this.extension = extension;
    }

    public String getClientId() {
        return clientId;
    }

    private void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    private void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public String getUserName() {
        return userName;
    }

    private void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String getLastWillTopic() {
        return lastWillTopic;
    }

    private void setLastWillTopic(String lastWillTopic) {
        this.lastWillTopic = lastWillTopic;
    }

    public int getLastWillQos() {
        return lastWillQos;
    }

    private void setLastWillQos(int lastWillQos) {
        this.lastWillQos = lastWillQos;
    }

    public String getLastWillMessage() {
        return lastWillMessage;
    }

    private void setLastWillMessage(String lastWillMessage) {
        this.lastWillMessage = lastWillMessage;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    private void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getServerUri() {
        return serverUri;
    }

    private void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public MqttClientPersistence getClientPersistence() {
        return clientPersistence;
    }

    private void setClientPersistence(MqttClientPersistence clientPersistence) {
        this.clientPersistence = clientPersistence;
    }

    public MqttPingSender getPingSender() {
        return pingSender;
    }

    private void setPingSender(MqttPingSender pingSender) {
        this.pingSender = pingSender;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public String getCacheDirectoryName() {
        return cacheDirectoryName;
    }

    public void setCacheDirectoryName(String cacheDirectoryName) {
        this.cacheDirectoryName = cacheDirectoryName;
    }

    public static class Builder {
        /**
         * Identifier of each MQTT client connecting to a MQTT broker
         */

        private String clientId;

        /**
         * Indicates the broker that the client wants to establish a persistent session or not.
         * A persistent session (CleanSession is false) means,
         * that the broker will store all subscriptions for the client and also all missed messages,
         * when subscribing with QoS 1 or 2.If clean session is set to true,
         * the broker won’t store anything for the client and will also remove all information
         * from a
         * previous persistent session
         */

        private boolean cleanSession;

        /**
         * User name for authentication. This is an optional field
         */

        private String userName;

        /**
         * Password for authentication. This is an optional field
         */

        private String password;

        private String lastWillTopic;

        private int lastWillQos = -1;

        /**
         * It allows to notify other clients, when a client disconnects ungracefully
         */

        private String lastWillMessage;

        /**
         * time interval, the clients sends regular PING Request messages to the broker.
         * The broker response with PING Response and this mechanism will allow both sides to
         * determine
         * if the other one is still alive and reachable.
         */

        private int keepAlive = 120;

        private String serverUri;

        private MqttClientPersistence clientPersistence;

        private MqttPingSender pingSender;

        private DatabaseExtension<EchoMessage> extension;

        private int timeOut;
        private Context context;

        private String cacheDirectoryName;

        public Builder(Context context) {
            this.context = context;
        }


        public String getClientId() {
            return clientId;
        }

        public Builder setClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public boolean isCleanSession() {
            return cleanSession;
        }

        public Builder setCleanSession(boolean cleanSession) {
            this.cleanSession = cleanSession;
            return this;
        }

        public String getUserName() {
            return userName;
        }

        public Builder setUserName(String userName) {
            this.userName = userName;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public String getLastWillTopic() {
            return lastWillTopic;
        }

        public void setLastWillTopic(String lastWillTopic) {
            this.lastWillTopic = lastWillTopic;
        }

        public int getLastWillQos() {
            return lastWillQos;
        }

        public Builder setLastWillQos(int lastWillQos) {
            this.lastWillQos = lastWillQos;
            return this;
        }

        public String getLastWillMessage() {
            return lastWillMessage;
        }

        public Builder setLastWillMessage(String lastWillMessage) {
            this.lastWillMessage = lastWillMessage;
            return this;
        }

        public int getKeepAlive() {
            return keepAlive;
        }

        public Builder setKeepAlive(int keepAlive) {
            this.keepAlive = keepAlive;
            return this;
        }

        public String getServerUri() {
            return serverUri;
        }

        public Builder setServerUri(String serverUri) {
            this.serverUri = serverUri;
            return this;
        }

        public MqttClientPersistence getClientPersistence() {
            return clientPersistence;
        }

        public Builder setClientPersistence(MqttClientPersistence clientPersistence) {
            this.clientPersistence = clientPersistence;
            return this;
        }

        public MqttPingSender getPingSender() {
            return pingSender;
        }

        public Builder setPingSender(MqttPingSender pingSender) {
            this.pingSender = pingSender;
            return this;
        }

        public DatabaseExtension<EchoMessage> getExtension() {
            return extension;
        }

        public Builder setExtension(DatabaseExtension<EchoMessage> extension) {
            this.extension = extension;
            return this;
        }

        public String getCacheDirectoryName() {
            return cacheDirectoryName;
        }

        public Builder setCacheDirectoryName(String cacheDirectoryName) {
            this.cacheDirectoryName = cacheDirectoryName;
            return this;
        }

        public int getTimeOut() {
            return timeOut;
        }

        public void setTimeOut(int timeOut) {
            this.timeOut = timeOut;
        }

        public EchoConfiguration build() {
            EchoConfiguration configuration = EchoConfiguration.getInstance();

            if (!TextUtils.isEmpty(this.getClientId())) {
                configuration.setClientId(this.getClientId());
            } else {
                throw new MendatoryFieldException();
            }

            if (TextUtils.isEmpty(this.getServerUri())) {
                throw new MendatoryFieldException();
            } else {
                configuration.setServerUri(this.getServerUri());
            }

            if (this.getExtension() != null) {
                configuration.setExtension(this.getExtension());
            } else {
                throw new MendatoryFieldException();
            }

            if (this.getKeepAlive() > 0) {
                configuration.setKeepAlive(this.getKeepAlive());
            } else {
                configuration.setKeepAlive(10000);
            }

            if (TextUtils.isEmpty(this.getLastWillMessage())) {
                configuration.setLastWillMessage(
                        String.format(Locale.getDefault(), "%1s is disconnected",
                                this.getClientId()));
            } else {
                configuration.setLastWillMessage(this.getLastWillMessage());
            }

            if (this.getLastWillQos() < 0 || this.getLastWillQos() > 2) {
                configuration.setLastWillQos(0);
            } else {
                configuration.setLastWillQos(this.getLastWillQos());
            }

            if (!TextUtils.isEmpty(this.getLastWillTopic())) {
                configuration.setLastWillTopic(this.getLastWillTopic());
            } else {
                configuration.setLastWillTopic(
                        String.format(Locale.getDefault(), "%1s//%2s//%3s", "lastWill", "topic",
                                this.getClientId()));
            }

            if (this.getTimeOut() <= 0) {
                configuration.setTimeOut(40);
            } else {
                configuration.setTimeOut(this.getTimeOut());
            }

            if (this.getClientPersistence() != null) {
                configuration.setClientPersistence(this.getClientPersistence());
            } else {
                configuration.setClientPersistence(new MemoryPersistence());
            }

            if (TextUtils.isEmpty(cacheDirectoryName)) {
                // getting the application name
                ApplicationInfo applicationInfo = context.getApplicationInfo();
                int stringId = applicationInfo.labelRes;
                String name = stringId == 0
                        ? applicationInfo.nonLocalizedLabel.toString()
                        : context.getString(stringId);

                if (TextUtils.isEmpty(name)) {
                    name = "Echo";
                }

                configuration.setCacheDirectoryName(name);
            } else {
                configuration.setCacheDirectoryName(cacheDirectoryName);
            }

            configuration.setUserName(this.getUserName());
            configuration.setPassword(this.getPassword());
            configuration.setPingSender(this.getPingSender());
            configuration.setCleanSession(this.isCleanSession());

            return configuration;
        }
    }
}