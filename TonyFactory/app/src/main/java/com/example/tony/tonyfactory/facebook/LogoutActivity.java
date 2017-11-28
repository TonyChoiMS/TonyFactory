package com.example.tony.tonyfactory.facebook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.example.tony.tonyfactory.R;
import com.example.tony.tonyfactory.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016-11-29.
 */

public class LogoutActivity extends AppCompatActivity {

    @BindView(R.id.btnLogout)
    TextView btnLogout;
    @BindView(R.id.profileImage)
    ImageView profileImg;
    @BindView(R.id.fbId)
    TextView id;
    @BindView(R.id.fbName)
    TextView name;
    @BindView(R.id.fbEmail)
    TextView email;
    @BindView(R.id.fbGender)
    TextView gender;

    private fbUser user;
    private Bitmap bitmap;
    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        //현재 접속중인 사용자 정보를 불러옴.
        user = PrefUtils.getCurrentUser(LogoutActivity.this);
        unbinder = ButterKnife.bind(this);

        //FacebookId를 통해서 사용자 프로필 이미지를 받아옴.
        Glide.with(LogoutActivity.this)
                .load("https://graph.facebook.com/" + user.facebookID + "/picture?type=large")
                .into(profileImg);

        id.setText(user.facebookID);
        name.setText(user.name);
        email.setText(user.email);
        gender.setText(user.gender);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefUtils.clearCurrentUser(LogoutActivity.this);

                LoginManager.getInstance().logOut();

                Intent intent = new Intent(LogoutActivity.this, com.example.tony.tonyfactory.main.MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
