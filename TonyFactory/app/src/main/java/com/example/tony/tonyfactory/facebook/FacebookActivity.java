package com.example.tony.tonyfactory.facebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.example.tony.tonyfactory.R;
import com.example.tony.tonyfactory.utils.PrefUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016-11-29.
 */

public class FacebookActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginBtn;
    private TextView btnLogin;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (PrefUtils.getCurrentUser(FacebookActivity.this) != null) {
            Intent intent = new Intent(FacebookActivity.this, LogoutActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        callbackManager = CallbackManager.Factory.create();

        loginBtn = (LoginButton) findViewById(R.id.login_button);

        loginBtn.setReadPermissions("public_profile", "email", "user_friends");

        //페북 로그인 버튼을 커스터마이징 하기 위해 페북 로그인 버튼이 아닌
        //텍스트뷰를 사용해서 로그인 버튼을 정의
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(FacebookActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.show();

                //텍스트뷰를 누르면 페북 로그인버튼을 클릭하게 설정
                loginBtn.performClick();
                loginBtn.setPressed(true);
                loginBtn.invalidate();
                loginBtn.registerCallback(callbackManager, mCallBack);
                loginBtn.setPressed(false);
                loginBtn.invalidate();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            pDialog.dismiss();
            //App code
            //액세스 토큰을 받아옴.
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.e("response: ", response + "");
                            try {
                                fbUser user = new fbUser();
                                user.name = object.getString("name").toString();
                                user.email = object.getString("email").toString();
                                user.facebookID = object.getString("id").toString();
                                user.gender = object.getString("gender").toString();
                                //현재 접속한 사용자를 SharedPreferences를 통해 저장
                                PrefUtils.setCurrentUser(user, FacebookActivity.this);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(FacebookActivity.this, LogoutActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
            );

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
            pDialog.dismiss();
        }

        @Override
        public void onError(FacebookException error) {
            pDialog.dismiss();
        }
    };
}
