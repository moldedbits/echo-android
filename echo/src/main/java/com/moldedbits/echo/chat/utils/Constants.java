package com.moldedbits.echo.chat.utils;

public class Constants {
    public static final int HEIGHT_MIN_FEET = 1;
    public static final int HEIGHT_MAX_FEET = 7;
    public static final int HEIGHT_DEFAULT = 5;
    public static final int WEIGHT_MIN = 2;
    public static final int WEIGHT_MAX = 200;
    public static final int WEIGHT_DEFAULT = 60;
    public static final int DEFAULT_UPLOAD_IMAGE_WIDTH = 1024;
    public static final int DEFAULT_PROFILE_IMAGE_WIDTH = 120;

    // Content for Notification
    public static final String SENDER_ID = ""; // TODO Load from environment
    public static final String SENDER_CLIENT_ID = "";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_NOTIFICATION_TYPE_ID = "notification_type_id";
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_CHANNEL_NAME = "channel_name";

    // NOTIFICATION TYPE ID
    public static final String MESSAGE_PROFESSIONAL_TO_PATIENT = "message_professional_to_patient";
    public static final String CHAT_PROFESSIONAL_TO_PATIENT = "chat_professional_to_patient";
    public static final String MESSAGE_PATIENT_TO_PROFESSIONAL = "message_patient_to_professional";
    public static final String MESSAGE_BROADCAST = "broadcast_message";
    public static final String MESSAGE_EASY_CONSULT_ADD = "easy_consult_add";
    public static final String MESSAGE_END_CONSULTATION = "end_consultation";
    public static final String MESSAGE_START_CONSULTATION = "start_easy_consult";
    public static final String MESSAGE_SATISFIED_FEEDBACK_PUSH = "satisfied_feedback_push";
    public static final String MESSAGE_LOCAL_CHAT_NOTIFICATION = "message_local_chat_notification";
    public static final String MESSAGE_IN_CLINIC_ADD_PATIENT = "in_clinic_add_patient";
    public static final String MESSAGE_MARK_SATISFIED_REMINDER = "mark_satisfied_reminder";

    public static final String EXTRA_CHAT_MESSAGE = "last_message";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_USER_ID = "user_uid";
    public static final String EXTRA_SENDER_ID = "sender_uid";

    public final static String CHAT_SERVER_URL = "tcp://192.168.1.25:1883";

    public static final String KEY_CLIENT_ID = "client_id";
    public static final String KEY_SERVER_URI = "server_uri";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PROFILE_PIC = "profile_pic";
    public static final String KEY_TOPIC = "topic";
    public static final String KEY_NOTES = "notes";
    public static final String KEY_IS_CONNECTED = "is_connected";
    public static final String KEY_NOTIFICATION_TYPE_ID = "notification_type_id";
    public static final String KEY_RECEIVER_ID = "receiver_id";
    public static final String KEY_SENDER_CLASS = "sender_class";

    public static final String IDENTITY_POOL_ID = "nextcures_dev";
    public static final String BUCKET_ID = "nextcures";
    public static final String UPLOAD_DIRECTORY_IMAGE = "/Nextcures/Upload/Image";
    public static final String UPLOAD_DIRECTORY_DOCUMENT = "/Nextcures/Upload/Document";
    public static final String DOWNLOAD_DIRECTORY_IMAGE = "/Nextcures/Download/Image";
    public static final String DOWNLOAD_DIRECTORY_DOCUMENT = "/Nextcures/Download/Document";
    public static final String PROFILE_PIC_DIRECTORY = "/Nextcures/Profile";
    public static final String AWS_ACCESS_KEY_ID = ""; // TODO Load from environment
    public static final String AWS_SECRET_KEY = ""; // TODO Load from environment
    public static final String KEY_DATA = "data";
    public static final String RESPONSE_TIME_DATA_INTENT = "RESPONSE_TIME";
    public static final String KEY_IS_ACTIVE = "is_active";
    public static final java.lang.String KEY_RELATIONSHIP_ID = "relationship_id";

    public static final java.lang.String EXTRA_PROFESSION_TYPE_ID = "profession_type_id";
    public static final java.lang.String EXTRA_PROFESSION_ID = "profession_id";
    public static final String KEY_LOCAL_PROFILE_PIC = "local_url";
    public static final java.lang.String EXTRA_TOPIC = "topic";

    // Important Links
    public static final String TERMS_OF_USE = "http://www.nextcures.com/terms-of-use";
    public static final String PRIVACY_POLICY = "http://www.nextcures.com/terms-of-use#privacy";
    public static final String FAQ_FOR_PATIENTS = "http://www.nextcures.com/faq-for-patients";
    public static final String FAQ_FOR_BUSINESSES = "http://www.nextcures.com/faq-for-hcp";
    public static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";
    public static final String KEY_NAME = "name";
    public static final String KEY_PATH = "path";
    public static final String KEY_BITMAP = "bitmap";
    public static final String KEY_URI = "uri";
    public static final String KEY_DOC = "doc";
    public static final String KEY_TIME = "time";
    public static final String KEY_IMAGE_UPLOADED = "image_uploaded";
    public static final String KEY_REFRESH = "refresh";
    public static final String UTM_SOURCE = "utm_source";
    public static final String DOCUMENT = "document";

    // Messages Sent Statuses
    public static final int STATUS_SENT = 1;
    public static final int STATUS_RECEIVED = 2;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_UPLOADING = 4;
    public static final int STATUS_UPLOADED = 5;
    public static final String EXTRA_IMAGE = "image";
    public static final String EXTRA_CITY = "city";
    public static final String EXTRA_FEE = "fee";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_FILTER = "filter";
}
