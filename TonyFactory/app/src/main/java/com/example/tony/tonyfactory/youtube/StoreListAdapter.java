package com.example.tony.tonyfactory.youtube;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tony.tonyfactory.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016-11-28.
 */

public class StoreListAdapter extends ArrayAdapter<SearchVideo> {

    private ArrayList<SearchVideo> items;
    SearchVideo model;
    private int resId;
    private Context ctx;

    public StoreListAdapter(Context context, int resource, ArrayList<SearchVideo> objects) {
        super(context, resource, objects);
        this.items = objects;
        this.resId = resource;
        this.ctx = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Nullable
    @Override
    public SearchVideo getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        model = items.get(position);

        if(v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = vi.inflate(resId, null);
        }

        ImageView img = (ImageView) v.findViewById(R.id.img);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView date = (TextView) v.findViewById(R.id.date);

        String url = model.getUrl();

        String startUrl = "";
        String endUrl = "";
        startUrl = url.substring(0, url.lastIndexOf("/") + 1);
        endUrl = url.substring(url.lastIndexOf("/") + 1, url.length());
        try {
            endUrl = URLEncoder.encode(endUrl, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String new_url = startUrl + endUrl;

        Glide.with(ctx)
                .load(new_url)
                .into(img);

        v.setTag(position);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer) v.getTag();

                Intent intent  = new Intent(ctx, VideoViewActivity.class);
                intent.putExtra("id", items.get(pos).getVideoId());
                ctx.startActivity(intent);
            }
        });

        title.setText(model.getTitle());
        date.setText(model.getPublishedAt());


        return v;
    }
}
