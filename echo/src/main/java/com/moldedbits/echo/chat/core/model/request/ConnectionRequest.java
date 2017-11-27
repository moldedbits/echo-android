package com.moldedbits.echo.chat.core.model.request;

import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttPingSender;

/**
 * It contains all the necessary data required to connect a client to broker
 */
public class ConnectionRequest {
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

    private int[] qos;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setLastWillQos(int lastWillQos) {
        this.lastWillQos = lastWillQos;
    }

    public String getLastWillMessage() {
        return lastWillMessage;
    }

    public void setLastWillMessage(String lastWillMessage) {
        this.lastWillMessage = lastWillMessage;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getServerUri() {
        return serverUri;
    }

    public void setServerUri(String serverUri) {
        this.serverUri = serverUri;
    }

    public MqttClientPersistence getClientPersistence() {
        return clientPersistence;
    }

    public void setClientPersistence(MqttClientPersistence clientPersistence) {
        this.clientPersistence = clientPersistence;
    }

    public MqttPingSender getPingSender() {
        return pingSender;
    }

    public void setPingSender(MqttPingSender pingSender) {
        this.pingSender = pingSender;
    }

    public int[] getQos() {
        return qos;
    }

    public void setQos(int[] qos) {
        this.qos = qos;
    }
}