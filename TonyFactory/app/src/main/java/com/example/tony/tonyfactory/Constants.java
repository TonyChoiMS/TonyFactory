package com.example.tony.tonyfactory;

/**
 * Created by Administrator on 2016-11-21.
 */

public class Constants {
    /**
     * Firebase Messaging 관련 전역변수
     */
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "notice";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String ALERT_KILLED = "alertKilled";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    /**
     * Youtube Api 관련 전역 변수
     */
    public static final String DeveloperKey = "AIzaSyB4F7ueZFx2-rQY2zJjtpUia8NV1ESCqzU";

    /**
     * Instagram Api 관련 전역 변수
     */
    public static final String CLIENT_ID = "3f2142907dc6487faa58ae1049e43360";
    public static final String CLIENT_SECRET = "aa1ccd3b48584cc1816ebfa04a63f22c";
    public static final String CALLBACK_URL = "http://localhost/gitInstagram/success.php";
}
