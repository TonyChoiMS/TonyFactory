package com.example.tony.tonyfactory;

import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.google.firebase.FirebaseApp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016-11-29.
 */

public class MyApplication extends Application {

    private static MyApplication myApplication;
    AppCompatDialog progressDialog;

    public static final String TAG = MyApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    public static synchronized MyApplication getInstance() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        //Firebase Initialize
        FirebaseApp.initializeApp(this);

        //Facebook SDK Initialize
        FacebookSdk.sdkInitialize(getApplicationContext());
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.activity_test.tony.tonyfactory",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash : ", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    //Animation ProgressBar On.
    public void progressOn(Activity activity, String message) {
        //Activity isFinishing, return.
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (progressDialog != null && progressDialog.isShowing()) {
            progresSET(message);
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);                            //can not cancel through loading not complete
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //set Background
            progressDialog.setContentView(R.layout.progress_loading);       //set CustomView
            progressDialog.show();
        }

        final ImageView imgFrame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        final AnimationDrawable frameAnimation = (AnimationDrawable) imgFrame.getBackground();
        imgFrame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tvMessage = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
    }

    //If Animation ProgressBar On, change the progress text.
    public void progresSET(String message) {
        //Activity isFinishing, return.
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

        TextView tvMessage = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setText(message);
        }
    }

    //If Animation ProgressBar On, Progres Bar Off.
    public void progresOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
