package com.moldedbits.echo.chat.core.model.response;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.util.Debug;

/**
 * Created by viveksingh
 * on 12/04/16.
 */
public class ConnectionResponse {

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getServerUi() {
        return serverUi;
    }

    public void setServerUi(String serverUi) {
        this.serverUi = serverUi;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Debug getDebug() {
        return debug;
    }

    public void setDebug(Debug debug) {
        this.debug = debug;
    }

    public MqttException getException() {
        return exception;
    }

    public void setException(MqttException exception) {
        this.exception = exception;
    }

    public MqttClient getClient() {
        return client;
    }

    public void setClient(MqttClient client) {
        this.client = client;
    }

    private boolean isConnected;

    private String topic;

    private String serverUi;

    private String clientId;

    private Debug debug;

    private MqttException exception;

    private MqttClient client;
}
