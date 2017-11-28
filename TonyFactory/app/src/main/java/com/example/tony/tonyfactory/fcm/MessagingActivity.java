package com.example.tony.tonyfactory.fcm;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tony.tonyfactory.Constants;
import com.example.tony.tonyfactory.R;
import com.example.tony.tonyfactory.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2016-11-28.
 */

public class MessagingActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView txtRegId, txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new send().execute();
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);

        txtRegId = (TextView) findViewById(R.id.txt_reg_id);
        txtMessage = (TextView) findViewById(R.id.txt_push_message);

//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // checking for type intent filter
//                if (intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
//                    // gcm successfully registered
//                    // now subscribe to `global` topic to receive app wide notifications
//                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.TOPIC_GLOBAL);
//
//                    displayFirebaseRegId();
//
//                } else if (intent.getAction().equals(Constants.PUSH_NOTIFICATION)) {
//                    // new push notification is received
//
//                    String message = intent.getStringExtra("message");
//
//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
//
//                    txtMessage.setText(message);
//                }
//            }
//        };

        displayFirebaseRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Constants.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            txtRegId.setText("Firebase Reg Id: " + regId);
        else
            txtRegId.setText("Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public class send extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String url = "https://fcm.googleapis.com/fcm/send";

            try {
                String parameters = "{" +
                        "\"data\": {" +
                        "\"message\": {" +
                        "\"title\": \"test\"," +
                        "\"content\": \"test content\"," +
                        "\"imgUrl\": \"\"," +
                        "\"link\": \"\"," +
                        "}" +
                        "}," +
                        "\"to\": \"/topics/notice\"" +
                        "}";
                String result = sendPost(url, parameters);
                Log.i("result", result);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public String sendPost(String url, String parameters) throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", "key=AAAA2fYSXq4:APA91bFgiLjF2WFZ5C8TLgRdYFJhdQU-TVkYCT317odoQ4OMfhyY7dL_Ps_BLp8KL_x3GzYTjVT1cE7vDOTz3l0JHnxx90fB-SLIwc8XQ1Ezbs-JNKiph1_-J6YLAb1Y3U953C-t2EkZ");
        String urlParameters = parameters;

        //post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(urlParameters.getBytes("UTF-8"));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        StringBuffer response = new StringBuffer();
        Log.i("fcm", "ResponseCode : " + responseCode);

        if (responseCode == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }
        //result
        System.out.println(response.toString());
        return response.toString();
    }
}