package com.example.tony.tonyfactory.main;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tony.tonyfactory.MyApplication;
import com.example.tony.tonyfactory.R;
import com.example.tony.tonyfactory.ItemTouchHelperRecyclerView.RecyclerViewActivity;
import com.example.tony.tonyfactory.audiotagger.AudioTaggerActivity;
import com.example.tony.tonyfactory.cardview.CardViewActivity;
import com.example.tony.tonyfactory.database.DatabaseActivity;
import com.example.tony.tonyfactory.downloadmanager.DownloadActivity;
import com.example.tony.tonyfactory.facebook.FacebookActivity;
import com.example.tony.tonyfactory.fcm.MessagingActivity;
import com.example.tony.tonyfactory.instagram.InstagramActivity;
import com.example.tony.tonyfactory.intro.IntroActivity;
import com.example.tony.tonyfactory.utils.DividerItemDecoration;
import com.example.tony.tonyfactory.utils.PrefUtils;
import com.example.tony.tonyfactory.utils.RecyclerTouchListener;
import com.example.tony.tonyfactory.youtube.YoutubeActivity;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private ImageView topImg;

    private List<Movie> movieList;
    private RecyclerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toolbar에 관한 세팅 메소드
        settingToolbar();
        //FloatingActionButton 세팅
        settingFab();
        //그 외 레이아웃에 대한 세팅
        setLayout();
    }

    //Toolbar
    public void settingToolbar() {
        //xml에 정의한 툴바를 액션바에 세팅
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
    }

    //Floating Action Button
    public void settingFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FAB를 누를 경우 CardviewActivity
                Intent intent = new Intent(MainActivity.this, CardViewActivity.class);
                startActivity(intent);
//                Snackbar.make(v, "FAB를 클릭하셨습니다.", Snackbar.LENGTH_SHORT)
//                        .setAction("Action", null).show();
            }
        });
    }

    private void setLayout() {
        //최상단 DrawerLayout 선언
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mDrawerLayout에 toolbar를 연동
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.openNav, R.string.closeNav);
        //setDrawerListener가 Deprecated 되었기 때문에 addDrawerListener로 대체
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //네비게이션 뷰 설정
        //아이템 클릭 리스너는 Activity에 implements해서 따로 메소드에 정의
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Toolbar 하단에 들어가는 이미지.
        //Glide Library를 통해 url에 저장된 이미지를 불러옴
        topImg = (ImageView) findViewById(R.id.top_img);
        Glide.with(MainActivity.this)
                .load("http://api.androidhive.info/images/minion.jpg")
                .into(topImg);

        //Adapter에 들어갈 List 초기화
        movieList = new ArrayList<Movie>();

        //RecyclerView에 연동할 Adapter 설정
        mAdapter = new RecyclerAdapter(movieList);

        //RecyclerView 설정
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        //아이템 애니메이션
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //RecyclerView Divider 설정
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //모든 설정을 마친 후에 어댑터를 set해준다
        recyclerView.setAdapter(mAdapter);
        //RecyclerView ClickListener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = movieList.get(position);
                makeText(MainActivity.this, movie.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //movieList에 더미 데이터를 집어넣기 위한 메소드
        sampleMovieList();
    }

    //뒤로가기 메소드를 오버라이드해서 네비게이션 뷰가 열려있을 때 뒤로가기를 누를 경우엔
    //네비게이션 뷰만 닫히게 동작
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //툴바 우측의 세팅 버튼
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //네비게이션 뷰의 아이템을 누를 경우 이곳에서 제어
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //FCM
            case R.id.nav_send:
                Intent fcm = new Intent(MainActivity.this, MessagingActivity.class);
                startActivity(fcm);
                break;
            //Instagram Activity
            case R.id.nav_info:
                Intent insta = new Intent(MainActivity.this, InstagramActivity.class);
                startActivity(insta);
                break;
            //Firebase Database
            case R.id.nav_database:
                Intent database = new Intent(MainActivity.this, DatabaseActivity.class);
                startActivity(database);
                break;
            //SlideShow
            case R.id.nav_slideshow:
                Intent slide = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(slide);
                break;
            //페북 및 구글(추가예정)로그인
            case R.id.nav_manage:
                Intent facebook = new Intent(MainActivity.this, FacebookActivity.class);
                startActivity(facebook);
                break;
            //유투브
            case R.id.nav_youtube:
                Intent youtube = new Intent(MainActivity.this, YoutubeActivity.class);
                startActivity(youtube);
                break;
            //Action Send
            case R.id.nav_share:
                makeText(this, "share", Toast.LENGTH_SHORT).show();
                break;
            //페북 Logout
            case R.id.nav_logout:
                PrefUtils.clearCurrentUser(MainActivity.this);
                LoginManager.getInstance().logOut();
                break;

            case R.id.nav_tagger:
                Intent taggerIntent = new Intent(MainActivity.this, AudioTaggerActivity.class);
                startActivity(taggerIntent);
                break;

            case  R.id.nav_recycler:
                Intent recyclerIntent = new Intent(MainActivity.this, RecyclerViewActivity.class);
                startActivity(recyclerIntent);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //우측 상단의 메뉴버튼
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Option Menu Animation Progress OnClickListener Method
    public boolean progress(MenuItem item) {


        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //Animation Progress ON
                MyApplication.getInstance().progressOn(MainActivity.this, "Hello loading");
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //Thread Sleep During 3 sec
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Animation Progress Off
                MyApplication.getInstance().progresOFF();
            }
        }.execute();

        return true;
    }

    //샘플 더미 데이터
    private void sampleMovieList() {
        Movie movie = new Movie("Mad Max: Fury Road", "Action & Adventure", "2015");
        movieList.add(movie);

        movie = new Movie("Inside Out", "Animation, Kids & Family", "2015");
        movieList.add(movie);

        movie = new Movie("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Shaun the Sheep", "Animation", "2015");
        movieList.add(movie);

        movie = new Movie("The Martian", "Science Fiction & Fantasy", "2015");
        movieList.add(movie);

        movie = new Movie("Mission: Impossible Rogue Nation", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Up", "Animation", "2009");
        movieList.add(movie);

        movie = new Movie("Star Trek", "Science Fiction", "2009");
        movieList.add(movie);

        movie = new Movie("The LEGO Movie", "Animation", "2014");
        movieList.add(movie);

        movie = new Movie("Iron Man", "Action & Adventure", "2008");
        movieList.add(movie);

        movie = new Movie("Aliens", "Science Fiction", "1986");
        movieList.add(movie);

        movie = new Movie("Chicken Run", "Animation", "2000");
        movieList.add(movie);

        movie = new Movie("Back to the Future", "Science Fiction", "1985");
        movieList.add(movie);

        movie = new Movie("Raiders of the Lost Ark", "Action & Adventure", "1981");
        movieList.add(movie);

        movie = new Movie("Goldfinger", "Action & Adventure", "1965");
        movieList.add(movie);

        movie = new Movie("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }

    //RecyclerView onClickListener Interface
//RecyclerView는 OnItemClickLitener가 없으므로 따로 정의해주어야한다.
//코드는 RecyclerTouchListener.java
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
