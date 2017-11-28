package com.example.tony.tonyfactory.youtube;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tony.tonyfactory.Constants;
import com.example.tony.tonyfactory.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-11-28.
 */

public class YoutubeActivity extends AppCompatActivity {

    private Drawable mClearIcon;
    private AutoCompleteTextView etComplete;
    private ArrayList<SearchVideo> searchData;
    private String kind;
    private String vodid;
    private String title;
    private String date;
    private String imgUrl;
    private String searchText;

    private ProgressDialog pDialog;

    private SearchAdapter searchAdapter;
    private StoreListAdapter storeListAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);

        /**
         * 상단 검색창에 대한 설정 소스코드
         */
        etComplete = (AutoCompleteTextView) findViewById(R.id.eturl);
        List<String> searchItem = new ArrayList<String>();

        searchAdapter = new SearchAdapter(this, android.R.layout.simple_dropdown_item_1line, searchItem);
        etComplete.setAdapter(searchAdapter);
        //자동완성 검색 리스트의 아이템 클릭 리스너
        etComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(parent.getAdapter() != null) {
                    String text = (String)parent.getAdapter().getItem(position);
                    etComplete.setText(text);
                    etComplete.onEditorAction(EditorInfo.IME_ACTION_SEARCH);
                }
            }
        });

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mClearIcon = getResources().getDrawable(R.drawable.btn_clear_selector);
        } else {
            Theme theme = getTheme();
            mClearIcon = getResources().getDrawable(R.drawable.btn_clear_selector, theme);
        }
        mClearIcon.setBounds(0, 0, mClearIcon.getIntrinsicWidth(), mClearIcon.getIntrinsicHeight());

        etComplete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(etComplete.getCompoundDrawables()[2] == null) {
                    return false;
                }
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > etComplete.getWidth() - etComplete.getPaddingRight()
                        - mClearIcon.getIntrinsicWidth()) {
                    etComplete.setText("");
                }
                v.performClick();
                return false;
            }
        });

        etComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                etComplete.setCompoundDrawables(null, null, etComplete.getText().toString().equals("") ? null : mClearIcon, null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //actionId에 해당하는 행동을 했을 경우 검색어로 youtube 검색을 실행하도록 searchTask 실행
        etComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_NEXT
                        || actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_SEARCH
                        || (event.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    //Youtube 검색 메소드
                    searchYoutube(v);
                    //AutoComplete DropDown ListView 없앰
                    etComplete.dismissDropDown();
                    return true;
                }
                return false;
            }
        });
        //End

        searchData = new ArrayList<SearchVideo>();

        Button search = (Button) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Youtube 검색 메소드
                searchYoutube(v);
            }
        });
    }

    //Youtube검색을 실행하는 메소드
    public void searchYoutube(View v) {
        closeKeyboard(v);
        searchText = etComplete.getText().toString();
        new searchTask(searchText).execute();
    }

    //키보드 내리는 코드
    public void closeKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //검색 키워드 찾는 AsyncTask, Google Toolbar URL 사용
    private class searchTask extends AsyncTask<Void, Void, Void> {

        String searchText;

        public searchTask(String searchText) {
            this.searchText = searchText;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(YoutubeActivity.this);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Loading...");
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListView searchList = (ListView) findViewById(R.id.searchList);

            StoreListAdapter adapter = new StoreListAdapter(getApplicationContext(), R.layout.youtube_list_item, searchData);
            searchList.setAdapter(adapter);

            pDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... text) {
            searchData.clear();

            URL url;
            StringBuilder response = new StringBuilder();
            try {
                String query = URLEncoder.encode(searchText, "utf-8");
                url = new URL("https://www.googleapis.com/youtube/v3/search?"
                        + "part=snippet&q="+ query
                        + "&key="+ Constants.DeveloperKey
                        + "&maxResults=50");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                JSONObject object = new JSONObject(response.toString());

                JSONArray contents = object.getJSONArray("items");
                for (int i=0; i < contents.length(); i++) {
                    JSONObject items = contents.getJSONObject(i);
                    kind = items.getJSONObject("id").getString("kind");

                    if(kind.equals("youtube#video")) {
                        vodid = items.getJSONObject("id").getString("videoId");
                    } else if (kind.equals("youtube#channel")) {
                        vodid = items.getJSONObject("id").getString("channelId");
                    } else {
                        vodid = items.getJSONObject("id").getString("playlistId");
                    }
                    title = items.getJSONObject("snippet").getString("title");

                    //연월일만 따오기 위해 10자리까지만 가져옴
                    date = items.getJSONObject("snippet").getString("publishedAt").substring(0, 10);

                    imgUrl = items.getJSONObject("snippet")
                            .getJSONObject("thumbnails")
                            .getJSONObject("default")
                            .getString("url");

                    searchData.add(new SearchVideo(vodid, title, imgUrl, date));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
