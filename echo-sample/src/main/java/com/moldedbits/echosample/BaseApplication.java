package com.moldedbits.echosample;

import android.app.Application;
import android.content.Intent;
import android.os.Build;

import com.moldedbits.echo.chat.EchoConfiguration;
import com.moldedbits.echo.chat.core.MqttService;
import com.moldedbits.echo.chat.utils.LocalStorage;
import com.moldedbits.echorealmextension.RealmExtension;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LocalStorage.getInstance(this);

        // TODO: 26/06/17  This code is for testing purpose
        String clientId = "CLIENT_ID_1";
        if (isEmulator()) {
            clientId = "CLIENT_ID_2";
        }

        // setting up Echo configuration
        EchoConfiguration.Builder builder = new EchoConfiguration.Builder(this);
        builder.setServerUri("tcp://192.168.1.74:1883")
                .setClientId(clientId)
                .setCleanSession(false)
                .setExtension(RealmExtension.getInstance(this))
                .build();

        startService(new Intent(this, MqttService.class));
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
