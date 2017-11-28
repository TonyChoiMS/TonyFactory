package com.example.tony.tonyfactory.intro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tony.tonyfactory.R;

/**
 * Created by Administrator on 2016-11-30.
 */

public class IntroActivity extends AppCompatActivity {

    private ViewPager pager;
    private IntroViewPagerAdapter mAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip;
    private Button btnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_intro);

        pager = (ViewPager) findViewById(R.id.pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnNext = (Button) findViewById(R.id.btnNext);

        //슬라이드 레이아웃을 int형 배열에 선언
        layouts = new int[] {
                R.layout.intro_slide1,
                R.layout.intro_slide2,
                R.layout.intro_slide3,
                R.layout.intro_slide4
        };
        //하단의 점들을 그리는 메소드, 0을 보내는 것은 첫페이지 상태로 초기화하기 위함
        addBottomDots(0);
        //notification bar를 투명으로 설정
        changeStatusBarColor();

        mAdapter = new IntroViewPagerAdapter();
        pager.setAdapter(mAdapter);
        pager.addOnPageChangeListener(pagerPageChangeListener);

        //스킵버튼을 누르면 바로 홈으로 간다.
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        //다음 버튼을 누를 경우 다음 스크린이 있으면 다음장으로, 없으면 홈으로
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if (current < layouts.length) {
                    pager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];
        //활성화와 비활성화 상태의 dot의 색상을 저장
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInActive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInActive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }
    }
    //pager의 현재 페이지에 i를 더한 값을 리턴
    private int getItem(int i) {
        return pager.getCurrentItem() + i;
    }

    //메인 엑티비티로 이동할 메소드
    private void launchHomeScreen() {
        Intent intent = new Intent(IntroActivity.this, com.example.tony.tonyfactory.main.MainActivity.class);
        startActivity(intent);
        finish();
    }

    //뷰페이저 pageChangeListener
    ViewPager.OnPageChangeListener pagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            addBottomDots(position);
            //마지막 페이지에 왔을 경우
            if (position == layouts.length -1) {
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    //롤리팝 이상의 SDK를 사용할 경우 상단 status bar를 투명으로 설정
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 뷰페이저 어댑터 이너클래스
     */
    public class IntroViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public IntroViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View v = layoutInflater.inflate(layouts[position], container, false);
            container.addView(v);
            return v;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }
    }
}
