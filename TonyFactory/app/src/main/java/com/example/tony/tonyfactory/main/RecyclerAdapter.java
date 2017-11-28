package com.example.tony.tonyfactory.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tony.tonyfactory.R;

import java.util.List;

/**
 * Created by Administrator on 2016-11-30.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{

    private List<Movie> movieList;

    //커스텀 뷰홀더 클래스
    //각 row에 들어가는 widget 선언
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView year;
        public TextView genre;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            year = (TextView) v.findViewById(R.id.year);
            genre = (TextView) v.findViewById(R.id.genre);
        }
    }

    //Adapter 생성자
    public RecyclerAdapter(List<Movie> object) {
        this.movieList = object;
    }

    //레이아웃을 설정하는 메소드
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    //뷰홀더 바인드
    //각 필드에 내용을 입력하는 부분
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.title.setText(movie.getTitle());
        holder.year.setText(movie.getYear());
        holder.genre.setText(movie.getGenre());
    }

    //아이템의 갯수를 리턴
    @Override
    public int getItemCount() {
        return movieList.size();
    }
}
