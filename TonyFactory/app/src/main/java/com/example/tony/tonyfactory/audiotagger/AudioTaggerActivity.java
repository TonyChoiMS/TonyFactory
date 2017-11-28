package com.example.tony.tonyfactory.audiotagger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.tony.tonyfactory.R;
import com.example.tony.tonyfactory.utils.Dispatch;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017-09-05.
 */

public class AudioTaggerActivity extends AppCompatActivity {

    private final String TAG = "AudioTagger";
    @BindView(R.id.ivCover)
    ImageView ivCover;

    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        unbinder = ButterKnife.bind(this);
        new Dispatch<Bitmap>() {

            @Override
            public Bitmap onWork() throws IOException {
                Bitmap coverImage = getBitmapFromURL("https://dc591.4shared.com/img/Q4hPGTzr");
                if (coverImage != null) {
                    return coverImage;
                }
                return null;
            }

            @Override
            public void onComplete(Bitmap result) throws IOException {
                if (result != null) {
                    ivCover.setImageBitmap(result);
                    //File tempDir = AudioTaggerActivity.this.getCacheDir();
                    File mp3File = SaveBitmapToFileCache(result);
                    Mp3FileTagEdit(mp3File);
                }
            }
        };
    }


    private Bitmap getBitmapFromURL(String url) {
        Bitmap bitmap = null;
        try {
            bitmap = Glide.with(this)
                    .load(url)
                    .asBitmap()
                    .into(250, 250)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    private File SaveBitmapToFileCache(Bitmap bitmap) {
        File fileCacheItem = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        FileOutputStream fos = null;
        try {
            fileCacheItem.createNewFile();
            byte[] bitmapdata = bos.toByteArray();
            Log.i(TAG, "byte array length : " + bitmapdata.length);
            fos = new FileOutputStream(fileCacheItem);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileCacheItem;
    }

    private void Mp3FileTagEdit(File coverFile) {
        String mp3FilePath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/09 숨.mp3";
        File tempFile = new File(mp3FilePath);
        Log.i(TAG,"file path : " + tempFile.getAbsolutePath());
        Log.i(TAG, "conWrite : " + tempFile.canWrite());
        if (!tempFile.canWrite())
            tempFile.setWritable(true);

        try {
            TagOptionSingleton.getInstance().setAndroid(true);
//            MP3File mp3File = new MP3File(tempFile);
//            AudioFile f = AudioFileIO.read(tempFile);
            MP3File mp3File = (MP3File) AudioFileIO.read(tempFile);

            Artwork cover = Artwork.createArtworkFromFile(coverFile);

            mp3File.setTag(new ID3v23Tag());
            Tag mp3Tag = mp3File.getTag();
            if (mp3Tag.getArtworkList() != null)
                mp3Tag.deleteArtworkField();
            mp3Tag.setField(cover);
            mp3Tag.setField(FieldKey.ARTIST, "효신");
            mp3Tag.setField(FieldKey.ALBUM, "55집 앨범");
            mp3File.commit();


//            f.setTag(new ID3v23Tag());
//            Tag tag = f.getTag();
//            tag.setField(FieldKey.ARTIST, "박효신");
//            tag.setField(FieldKey.ALBUM, "테스트앨범");
//            tag.setField(cover);
//
//            f.commit();
            AudioFileIO.write(mp3File);
            Log.i("tag", "artwork : " + mp3File.getTag().getFirstArtwork());

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + mp3FilePath)));
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + mp3FilePath)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("complete", "commit complete");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
