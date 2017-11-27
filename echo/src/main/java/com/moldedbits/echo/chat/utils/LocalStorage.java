package com.moldedbits.echo.chat.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LocalStorage {

    private static final String PREF_NAME = "pregacare.nextcure.com.nextcures";
    private static final String PROFESSION_TYPE_ID = "profession_type_id";
    private static final String USER_MOBILE = "user_mobile";
    private static final String USER_MAIL = "user_mail";
    private static final String USER_PROFILE_IMAGE = "profile_image";
    private static final String LATTITUDE = "lat";
    private static final String LONDITUDE = "lng";
    private static final String LAST_QUERY = "last_query";
    private static final String END_CONSULT_MESSAGES = "end_consult_messages";
    public static final String REFERRAL_TOKEN = "referral_token";
    private static final String TOPICS = "topics";
    private static final String BROADCAST_MESSAGES = "broadcast_message";
    private static final String SELECTED_FILTER = "filter";
    private static final String SELECTED_RINGTONE = "ringtone";
    private static final String RINGTONE_ENABLED = "ringtone_enabled";
    private static final String VIBRATION_ENABLED = "vibration_enabled";
    private static LocalStorage sInstance;
    private SharedPreferences mPref;
    private SharedPreferences mPasscodePref;

    private static final String EASY_CONSULT_CHAT_CONTROL = "easy_consult_chat_control";
    private static final String IN_CLINIC_CHAT_CONTROL = "in_clinic_chat_control";

    private static final String PASSCODE = "passcode";

    public static final String AUTH_TOKEN = "auth_token";
    public static final String LOGGED_IN = "logged_in";
    public static final String FIRST_TIME_USER = "first_time_user";
    public static final String GCM_TOKEN = "gcm_token";
    public static final String UPLOADS = "uploads";
    public static final String EMQTTD_CLIENT_ID = "emqttd_client_id";
    public static final String USER_NAME = "user_name";
    public static final String ADD_NEW_PATIENT = "add_new_patient";
    public static final String PATIENT_DETAILS = "patient_details";
    public static final String PROFESSIONAL_DETAILS = "professional_details";

    public static LocalStorage getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new LocalStorage(context);
        }
        return sInstance;
    }

    private LocalStorage(Context context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setAuthToken(String token) {
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(AUTH_TOKEN, token);
        editor.apply();
    }

    public String getAuthToken() {
        return mPref.getString(AUTH_TOKEN, null);
    }

    public void setLoggedIn(boolean value) {
        mPref.edit().putBoolean(LOGGED_IN, value).apply();
    }

    public boolean isLoggedIn() {
        return mPref.getBoolean(LOGGED_IN, false);
    }


    public void clear() {
        mPref.edit().clear().apply();
    }

    public void setEasyCunsultChatControl(boolean val) {
        mPref.edit().putBoolean(EASY_CONSULT_CHAT_CONTROL, val).apply();
    }

    public boolean getEasyCunsultChatControl() {
        return mPref.getBoolean(EASY_CONSULT_CHAT_CONTROL, true);
    }

    public void setInClinicChatControl(boolean val) {
        mPref.edit().putBoolean(IN_CLINIC_CHAT_CONTROL, val).apply();
    }

    public boolean getInClinicChatControl() {
        return mPref.getBoolean(IN_CLINIC_CHAT_CONTROL, true);
    }

    public String getPasscode() {
        return mPasscodePref.getString(PASSCODE, null);
    }

    public void setPasscode(String value) {
        mPasscodePref.edit().putString(PASSCODE, value).apply();
    }

    public void storeGcmToken(String val) {
        mPref.edit().putString(GCM_TOKEN, val).apply();
    }

    public String getGcmToken() {
        return mPref.getString(GCM_TOKEN, null);
    }

    public String getUploadedFileList() {
        return mPref.getString(UPLOADS, null);
    }

    public void setUploadedFileList(String value) {
        mPref.edit().putString(UPLOADS, value).apply();
    }

    public String getMqttClientId() {
        return mPref.getString(EMQTTD_CLIENT_ID, null);
    }

    public void setMqttClientId(String value) {
        mPref.edit().putString(EMQTTD_CLIENT_ID, value).apply();
    }

    public String getUserName() {
        return mPref.getString(USER_NAME, null);
    }

    public void setUserName(String value) {
        mPref.edit().putString(USER_NAME, value).apply();
    }

    public String getMobileNumber() {
        return mPref.getString(USER_MOBILE, null);
    }

    public void setMobileNumber(String value) {
        mPref.edit().putString(USER_MOBILE, value).apply();
    }

    public String getEmail() {
        return mPref.getString(USER_MAIL, null);
    }

    public void setLastSearchQuery(String value) {
        mPref.edit().putString(LAST_QUERY, value).apply();
    }

    public String getLastSearchQuery() {
        return mPref.getString(LAST_QUERY, null);
    }

    public void setMail(String value) {
        mPref.edit().putString(USER_MAIL, value).apply();
    }

    public void setProfileImageUrl(String value) {
        mPref.edit().putString(USER_PROFILE_IMAGE, value).apply();
    }

    public Set<String> getEndConsultMessages() {
        return mPref.getStringSet(END_CONSULT_MESSAGES, null);
    }

    public void setEndConsultMessages(String value) {
        Set<String> previousMessages = getEndConsultMessages();
        if (previousMessages == null) {
            previousMessages = new HashSet<>();
        }
        previousMessages.add(value);
        mPref.edit().putStringSet(END_CONSULT_MESSAGES, previousMessages).apply();
    }

    public String getProfileImageUrl() {
        return mPref.getString(USER_PROFILE_IMAGE, null);
    }

    public void setCurrentLat(float value) {
        mPref.edit().putFloat(LATTITUDE, value).apply();
    }

    public double getCurrentLat() {
        return mPref.getFloat(LATTITUDE, 0F);
    }

    public void setCurrentLng(float value) {
        mPref.edit().putFloat(LONDITUDE, value).apply();
    }

    public double getCurrentLng() {
        return mPref.getFloat(LONDITUDE, 0F);
    }

    public void setReferralToken(String referralToken) {
        mPref.edit().putString(REFERRAL_TOKEN, referralToken).apply();
    }

    public String getReferralToken() {
        return mPref.getString(REFERRAL_TOKEN, "");
    }

    public void clearReferralToken() {
        mPref.edit().remove(REFERRAL_TOKEN).apply();
    }

    public void setTopics(String[] topics) {
        Set<String> mySet = new HashSet<>(Arrays.asList(topics));
        mPref.edit().putStringSet(TOPICS, mySet).apply();
    }

    public String[] getTopics() {
        Set<String> set = mPref.getStringSet(TOPICS, null);
        return set.toArray(new String[set.size()]);
    }

    public Set<String> getBroadcastMessages() {
        return mPref.getStringSet(BROADCAST_MESSAGES, null);
    }

    public void setBroadcastMessages(String value) {
        Set<String> previousMessages = getEndConsultMessages();
        if (previousMessages == null) {
            previousMessages = new HashSet<>();
        }
        previousMessages.add(value);
        mPref.edit().putStringSet(BROADCAST_MESSAGES, previousMessages).apply();
    }

    public void clearBroadcastMessage(String value) {
        String data = null;
        Set<String> endConsult = getBroadcastMessages();
        for (String s : endConsult) {
            if (value.contentEquals(s)) {
                data = s;
            }
        }
        endConsult.remove(data);
        mPref.edit().putStringSet(BROADCAST_MESSAGES, endConsult).apply();
    }

    public void setSelectedFilter(String filter) {
        mPref.edit().putString(SELECTED_FILTER, filter).apply();
    }

    public String getSelectedFilter() {
        return mPref.getString(SELECTED_FILTER, null);
    }

    public void setSelectedRingtone(String uri) {
        mPref.edit().putString(SELECTED_RINGTONE, uri).apply();
    }

    public String getSelectedRingtone() {
        return mPref.getString(SELECTED_RINGTONE, null);
    }

    public static LocalStorage getInstance() {
        if(sInstance == null) {
            throw new IllegalStateException("Not initialized");
        }
        return sInstance;
    }

    public void enableRingtone(boolean val) {
        mPref.edit().putBoolean(RINGTONE_ENABLED, val).apply();
    }

    public boolean isRingtoneEnabled() {
        return mPref.getBoolean(RINGTONE_ENABLED, true);
    }

    public void enableVibration(boolean val) {
        mPref.edit().putBoolean(VIBRATION_ENABLED, val).apply();
    }

    public boolean isVibrationEnabled() {
        return mPref.getBoolean(VIBRATION_ENABLED, false);
    }

}