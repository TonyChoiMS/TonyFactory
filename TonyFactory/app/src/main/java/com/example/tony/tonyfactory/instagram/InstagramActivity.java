package com.example.tony.tonyfactory.instagram;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tony.tonyfactory.Constants;
import com.example.tony.tonyfactory.R;
import com.example.tony.tonyfactory.utils.Dispatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Administrator on 2017-02-27.
 */

public class InstagramActivity extends AppCompatActivity {

    private InstagramApp mApp;
    private InstagramSession mSession;
    private Button btnConnect;
    private TextView tvSummary;
    private ListView listView;

    private ArrayList<FollowList> sortList;
    private FollowListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        mSession = new InstagramSession(this);
        mApp = new InstagramApp(this, Constants.CLIENT_ID,
                Constants.CLIENT_SECRET, Constants.CALLBACK_URL);
        mApp.setListener(listener);

        tvSummary = (TextView) findViewById(R.id.tvSummary);

        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mApp.hasAccessToken()) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            InstagramActivity.this);
                    builder.setMessage("Disconnect from Instagram?")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            mApp.resetAccessToken();
                                            btnConnect.setText("Connect");
                                            tvSummary.setText("Not connected");
                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    final AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    mApp.authorize();
                }
            }
        });

        if (mApp.hasAccessToken()) {
            tvSummary.setText("Connected as " + mApp.getUserName());
            btnConnect.setText("Disconnect");
        }

        listView = (ListView) findViewById(R.id.listView);
        sortList = new ArrayList<FollowList>();

        adapter = new FollowListAdapter(InstagramActivity.this, R.layout.follow_list_item, sortList);
        listView.setAdapter(adapter);

    }

    InstagramApp.OAuthAuthenticationListener listener = new InstagramApp.OAuthAuthenticationListener() {

        @Override
        public void onSuccess() {
            tvSummary.setText("Connected as " + mApp.getUserName());
            btnConnect.setText("Disconnect");
        }

        @Override
        public void onFail(String error) {
            Toast.makeText(InstagramActivity.this, error, Toast.LENGTH_SHORT).show();
        }
    };

    private void getFollowList() {
//        final ArrayList<FollowList> followList = new ArrayList<FollowList>();
        new Dispatch<String>() {

            @Override
            public String onWork() throws IOException {
                StringBuilder response = new StringBuilder();
                //params(url, access_token)
                String urlBuild = getString(R.string.follow_list_url, "Instagram App id");
//                 String urlBuild = getString(R.string.follow_list_url, mSession.getAccessToken());
//                Log.i("url", "url : " + urlBuild);
//                Log.i("access Token", "access_token : " + mSession.getAccessToken());
                URL url = new URL(urlBuild);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                Log.i("response", "response : " + response.toString());

                String followerURLBuild = getString(R.string.follower_list_url, "Instagram App id");
//                 String followerURLBuild = getString(R.string.follow_list_url, mSession.getAccessToken());
//                Log.i("url", "url : " + followerURLBuild);
//                Log.i("access Token", "access_token : " + mSession.getAccessToken());
                URL followerURL = new URL(followerURLBuild);
                HttpURLConnection followerConn = (HttpURLConnection) followerURL.openConnection();
                BufferedReader followerReader = new BufferedReader(new InputStreamReader(followerConn.getInputStream()));

                String lines;
                while ((lines = followerReader.readLine()) != null) {
                    Log.i("follower", lines);
                    response.append(lines);
                }

                followerReader.close();
                Log.i("response", "response : " + response.toString());
                return response.toString();
            }

            @Override
            public void onComplete(String result) throws IOException {
                Log.i("result", "result : " + result);
                try {
                    JSONObject dataObj = new JSONObject(result);
                    JSONArray jsonArray = dataObj.getJSONArray("data");
                    Log.i("isze", String.valueOf(jsonArray.length()));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObj = jsonArray.getJSONObject(i);

                        String username = jObj.getString("username");
                        String profile_url = jObj.getString("profile_picture");
                        String fullname = jObj.getString("full_name");
                        String id = jObj.getString("id");

                        FollowList follow = new FollowList();
                        follow.setUsername(username);
                        follow.setProfile_picture(profile_url);
                        follow.setFull_name(fullname);
                        follow.setId(id);

                        sortList.add(follow);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
//        return followList;
    }

//    private ArrayList<FollowList> getFollowerList() {
//        final ArrayList<FollowList> followerList = new ArrayList<FollowList>();
//        new Dispatch<String>() {
//
//            @Override
//            public String onWork() throws IOException {
//                StringBuilder response = new StringBuilder();
//                //params(url, access_token)
//
//                return response.toString();
//            }
//
//            @Override
//            public void onComplete(String result) throws IOException {
//                try {
//                    JSONObject dataObj = new JSONObject(result);
//                    JSONArray jsonArray = dataObj.getJSONArray("data");
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jObj = jsonArray.getJSONObject(i);
//
//                        String username = jObj.getString("username");
//                        String profile_url = jObj.getString("profile_picture");
//                        String fullname = jObj.getString("full_name");
//                        String id = jObj.getString("id");
//
//                        FollowList follow = new FollowList();
//                        follow.setUsername(username);
//                        follow.setProfile_picture(profile_url);
//                        follow.setFull_name(fullname);
//                        follow.setId(id);
//
//                        followerList.add(follow);
//                    }
////                    adapter.notifyDataSetChanged();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        return followerList;
//    }

    public void showList(View v) {
        getFollowList();
//        ArrayList<FollowList> followList = getFollowList();
////        ArrayList<FollowList> followerList = getFollowerList();
//        ArrayList<FollowList> equalList = new ArrayList<>();
//        Log.i("follow", "followList 1st : " + followList.size());
//        for (int i = 0; i < followList.size(); i++) {
//            String followId = followList.get(i).getId();
//            for (int k = 0; k < followerList.size(); k++) {
//                String followerId = followerList.get(k).getId();
//                if (followId.equals(followerId)) {
//                    equalList.add(followList.get(i));
//                }
//            }
//        }
//        Log.i("follow", "followList 2nd : " + followList.size());
//        Log.i("equalList", String.valueOf(equalList.size()));
//        followList.removeAll(equalList);
//        sortList = followList;
//        Log.i("sort", String.valueOf(sortList.size()));
//        adapter.notifyDataSetChanged();
    }

    private static class FollowListAdapter extends ArrayAdapter<FollowList> {
        private Context mContext;
        private int resId;
        private ArrayList<FollowList> itemList;

        public FollowListAdapter(Context context, int resource, ArrayList<FollowList> objects) {
            super(context, resource, objects);
            this.mContext = context;
            this.resId = resource;
            this.itemList = objects;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Nullable
        @Override
        public FollowList getItem(int position) {
            return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            FollowList list = itemList.get(position);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                v = vi.inflate(resId, null);
            }

            ImageView img = (ImageView) v.findViewById(R.id.img);
            Glide.with(mContext)
                    .load(list.getProfile_picture())
                    .into(img);

            TextView tvUserName = (TextView) v.findViewById(R.id.username);
            tvUserName.setText(list.getUsername());

            TextView tvFullName = (TextView) v.findViewById(R.id.fullname);
            tvFullName.setText(list.getFull_name());

            Button btnFollow = (Button) v.findViewById(R.id.btnFollow);
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String user_id = "4643467729";
//                    String user_id = itemList.get(position).getId();
                    final InstagramSession mSession = new InstagramSession(mContext);
                    new Dispatch<String>() {

                        @Override
                        public String onWork() throws IOException {
                            StringBuilder response = new StringBuilder();
                            //params = url, user_id, access_token
                            String urlBuild = mContext.getString(R.string.post_follow_url, user_id, "Instagram App id");
                            Log.i("access_token", "access_token : " + mSession.getAccessToken());
//                            String urlBuild = mContext.getString(R.string.post_follow_url, user_id, mSession.getAccessToken());
                            Log.i("url", "url : " + urlBuild);
                            URL url = new URL(urlBuild);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoInput(true);
                            conn.setDoOutput(true);

                            OutputStream os = conn.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(os, "UTF-8"));
                            HashMap<String, String> follow = new HashMap<String, String>();
                            follow.put("action", "unfollow");

                            writer.write(getPostDataString(follow));

                            writer.flush();
                            writer.close();
                            os.close();
                            int responseCode = conn.getResponseCode();

                            if (responseCode == HttpsURLConnection.HTTP_OK) {
                                String line;
                                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                while ((line = br.readLine()) != null) {
                                    response.append(line);
                                }
                            }

                            Log.i("response", "response : " + response.toString());
                            return response.toString();
                        }

                        @Override
                        public void onComplete(String result) throws IOException {

                        }
                    };
                }
            });
            return v;
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
            return result.toString();
        }
    }
}
