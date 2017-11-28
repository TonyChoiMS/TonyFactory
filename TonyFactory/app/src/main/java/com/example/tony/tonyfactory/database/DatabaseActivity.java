package com.example.tony.tonyfactory.database;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tony.tonyfactory.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016-11-28.
 */

public class DatabaseActivity extends AppCompatActivity {

    private static final String TAG = DatabaseActivity.class.getSimpleName();
    @BindView(R.id.tv_user)
    TextView tvDetails;
    @BindView(R.id.name)
    EditText inputName;
    @BindView(R.id.email)
    EditText inputEmail;
    @BindView(R.id.btn_save)
    Button btnSave;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;

    Unbinder unbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        unbinder = ButterKnife.bind(this);

        mFirebaseInstance = FirebaseDatabase.getInstance();

        //앱이 오프라인 상태일 때도 값을 저장하고 있다가 온라인이 됬을 때 동기화 할 수 있게 설정
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        //"users" 노드의 레퍼런스를 GET
        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        //"app_title" 노드에 app_title을 저장
        mFirebaseInstance.getReference("app_title").setValue("Realtime Database");

        //"app_title" Change Listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);
                //Toolbar 타이틀을 업데이트
//                getSupportActionBar().setTitle(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Failed to read app title value.", databaseError.toException());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();

                //이미 값이 저장되있는지 확인해서 Update와 Create를 구분
                if (checkEmail(email)) {
                    if (TextUtils.isEmpty(userId)) {
                        createUser(name, email);
                    } else {
                        updateUser(name, email);
                    }
                } else {
                    new AlertDialog.Builder(DatabaseActivity.this)
                            .setTitle("이메일 형식 오류")
                            .setMessage("이메일을 다시 한 번 확인해주세요")
                            .setNeutralButton("네", null)
                            .create()
                            .show();
                }
            }
        });
        toggleButton();
    }

    private void updateUser(String name, String email) {
        //user에 포함되있는 노드를 업데이트
        if (!TextUtils.isEmpty(name)) {
            mFirebaseDatabase.child(userId).child("name").setValue(name);
        }

        if (!TextUtils.isEmpty(email)) {
            mFirebaseDatabase.child(userId).child("email").setValue(email);
        }
    }

    private void createUser(String name, String email) {
        //Firebase Database에서 Key값을 받아온다
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }
        User user = new User(name, email);
        mFirebaseDatabase.child(userId).setValue(user);
        addUserChangeListener();
    }

    private void addUserChangeListener() {
        //User 데이터가 변경될 시 호출할 리스너
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                //Null 값이 있는지 확인
                if (user == null) {
                    Log.e(TAG, "User data is null");
                    return;
                }
                Log.e(TAG, "User data is changed" + user.name + ", " + user.email);

                tvDetails.setText(user.name + ", " + user.email);

                //editText내용을 삭제
                inputName.setText("");
                inputEmail.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "User 클래스를 읽어오는 데 실패 했습니다 : " + databaseError.toException());
            }
        });
    }

    private void toggleButton() {
        if(TextUtils.isEmpty(userId)) {
            btnSave.setText("Save");
        } else {
            btnSave.setText("Update");
        }
    }

    /**
     * 이메일 형식을 체크하는 정규식 패턴
     * @param email email 입력값
     * @return true, false
     */
    private boolean checkEmail(String email) {
        String mail = "^[_a-zA-z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(mail);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
