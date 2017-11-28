package com.example.tony.tonyfactory.cardview;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tony.tonyfactory.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016-11-30.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private Context ctx;
    private List<Album> albumList;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.thumbnail)
        ImageView thumbnail;
        @BindView(R.id.overflow)
        ImageView overflow;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public AlbumAdapter(Context ctx, List<Album> albumList) {
        this.ctx = ctx;
        this.albumList = albumList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.count.setText(String.valueOf(album.getNumOfSongs()));

        Glide.with(ctx)
                .load(album.getThumbnail())
                .into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * 3Dot을 누를 경우 메뉴 팝업창 생성
     * @param v
     */
    private void showPopupMenu(View v) {
        //메뉴 Inflate
        PopupMenu popup = new PopupMenu(ctx, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MenuItemClickListener());
        popup.show();
    }

    public class MenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_add_favorite :
                    Toast.makeText(ctx, "Add to favorite", Toast.LENGTH_SHORT).show();
                    return true;

                case R.id.action_play_next :
                    Toast.makeText(ctx, "play next", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }


}
