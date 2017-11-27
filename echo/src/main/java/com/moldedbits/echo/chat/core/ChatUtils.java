package com.moldedbits.echo.chat.core;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.moldedbits.echo.chat.utils.Constants;
import com.google.gson.Gson;

import com.moldedbits.echo.chat.utils.LocalStorage;


public class ChatUtils {
    /**
     * Create unique id using client id and timestamp
     * @param clientId
     * @return
     */
    public static String generateMessageId(String clientId) {
        return String.format("%1s_%2s", clientId, System.currentTimeMillis());
    }

    public static <T> T parseMessage(String message, Class<T> className) {
        Gson gson = new Gson();
        return gson.fromJson(message, className);
    }

    /**
     * reconnect chat server by restarting the MqttService
     * @param context context require to start service
     */
    public static void refreshChatServer(Context context) {
        if (context != null) {
            Log.i("mqtt", "refreshing chat server ");
            Intent intent = new Intent(context, MqttService.class);
            intent.putExtra(Constants.KEY_SERVER_URI, Constants.CHAT_SERVER_URL);
            intent.putExtra(Constants.KEY_CLIENT_ID, LocalStorage.getInstance().getMqttClientId());
            intent.putExtra(Constants.KEY_TOPIC, LocalStorage.getInstance().getTopics());
            intent.putExtra(Constants.KEY_NAME, LocalStorage.getInstance().getUserName());
            if (MqttSession.getInstance().isSessionValid()) {
                context.stopService(intent);
                MqttSession.getInstance().disconnect();
            }
            context.startService(intent);
        }
    }
}
