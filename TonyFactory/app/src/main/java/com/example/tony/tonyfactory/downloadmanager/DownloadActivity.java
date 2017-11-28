package com.example.tony.tonyfactory.downloadmanager;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tony.tonyfactory.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2016-12-01.
 */

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {

    private DownloadManager downloadManager;
    private long Music_DownloadId, Image_DownloadId;

    @BindView(R.id.download_music)
    Button musicBtn;
    @BindView(R.id.download_image)
    Button imageBtn;
    @BindView(R.id.download_status)
    Button statusBtn;
    @BindView(R.id.cancel_download)
    Button cancelBtn;

    Unbinder unbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        unbinder = ButterKnife.bind(this);

        musicBtn.setOnClickListener(this);
        imageBtn.setOnClickListener(this);
        statusBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Uri image_uri = Uri.parse("http://www.androidtutorialpoint.com/wp-content/uploads/2016/09/Beauty.jpg");
        Uri music_uri = Uri.parse("http://www.androidtutorialpoint.com/wp-content/uploads/2016/09/AndroidDownloadManager.mp3");

        //noinspection SimplifiableIfStatement
        switch (v.getId()) {
            case R.id.download_music:
                View music_view = findViewById(R.id.download_music);
                Music_DownloadId = DownloadData(music_uri, music_view);
                break;
            case R.id.download_image:
                View image_view = findViewById(R.id.download_image);
                Image_DownloadId = DownloadData(image_uri, image_view);
                break;
            case R.id.cancel_download:
                downloadManager.remove(Music_DownloadId);
                downloadManager.remove(Image_DownloadId);
                break;
            case R.id.download_status:
                CheckImageStatus(Image_DownloadId);
                CheckMusicStatus(Music_DownloadId);
                break;
        }
    }

    private long DownloadData (Uri uri, View v) {

        long downloadReference;

        //안드로이드 다운로드매니저 Request 생성
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Request 타이틀 지정(Notification bar에 나타나는 타이틀)
        request.setTitle("Data Download");
        //Request 내용 지정(Notification bar에 나타나는 내용)
        request.setDescription("다운로드매니저를 이용한 데이터 다운로드");

        if (v.getId() == R.id.download_music)
            request.setDestinationInExternalFilesDir(DownloadActivity.this, Environment.DIRECTORY_DOWNLOADS, "AndroidTutorialPoint.mp3");
        else if (v.getId() == R.id.download_image)
            request.setDestinationInExternalFilesDir(DownloadActivity.this, Environment.DIRECTORY_DOWNLOADS,"AndroidTutorialPoint.jpg");
        //Enq
        downloadReference = downloadManager.enqueue(request);

        return downloadReference;
    }

    private void CheckImageStatus(long Image_DownloadId) {
        //다운로드 매니저 쿼리 선언
        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
        ImageDownloadQuery.setFilterById(Image_DownloadId);
        //커서 선언
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if (cursor.moveToFirst()) {
            DownloadStatus(cursor, Image_DownloadId);
        }
    }

    private void CheckMusicStatus(long Music_DownloadId) {
        DownloadManager.Query MusicDownloadQuery = new DownloadManager.Query();
        MusicDownloadQuery.setFilterById(Music_DownloadId);

        Cursor cursor = downloadManager.query(MusicDownloadQuery);
        if (cursor.moveToFirst()) {
            DownloadStatus(cursor, Music_DownloadId);
        }
    }

    private void DownloadStatus(Cursor cursor, long DownloadId) {
        //다운로드 상태 컬럼
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //다운로드가 정지 또는 실패 했을 때 Reason code
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //다운로드 파일명
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch (status) {
            case DownloadManager.STATUS_FAILED :
                statusText = "STATUS_FAILED";
                switch (reason) {
                    case DownloadManager.ERROR_CANNOT_RESUME :
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND :
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS :
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR :
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR :
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE :
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS :
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE :
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN :
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED :
                statusText = "STATUS_PAUSED";
                switch (reason) {
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI :
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN :
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK :
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY :
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING :
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING :
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL :
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }

        if (DownloadId == Music_DownloadId) {
            Toast toast = Toast.makeText(this, "Music Download Status: "
                    + "\n" + statusText
                    + "\n" + reasonText, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, "Image Download Status: "
                    + "\n" + statusText
                    + "\n" + reasonText, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 3000);
        }
    }

    /**
     * 다운로드가 완료됬을 경우 사용자에게 알리기 위해 Broadcast Receiver 등록
     */
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if (referenceId == Image_DownloadId) {
                Toast toast = Toast.makeText(DownloadActivity.this,
                        "Image Download Complete", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            } else {
                Toast toast = Toast.makeText(DownloadActivity.this,
                        "Music Download Complete", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP, 25, 400);
                toast.show();
            }
        }
    };
}
