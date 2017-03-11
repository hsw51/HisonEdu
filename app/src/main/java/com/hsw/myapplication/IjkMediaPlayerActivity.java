package com.hsw.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by HSW on 2017/3/10.
 */

public class IjkMediaPlayerActivity extends FragmentActivity implements View.OnClickListener {

  private IjkMediaPlayer mPlayer;
  private IjkVideoView mVideoView;
  private ViewPager mViewPager;
  private FragmentPagerAdapter mAdapter;
  private List<Fragment> datas;
  private TextView detailstextview;
  private TextView commenttextview;
  private TextView relativetextview;
  private ImageView tabline;

  private AndroidMediaController mMediaController;
  private TableLayout mHudView;

  private int screen1_3;
  private int currentPage;  //当前页码数

  private String mVideoPath;

  //  private List<VideoStreamBean> list;
  private Handler mHandler = new Handler();
  //  private VideoBean bean;
  public int videoId;
  private ImageButton imageButton;
  //判断imageButton是否需要隐藏
  private static boolean isGone;
  public static boolean isFlushComments;

  //若登录，这里记录cookie，若未登录，这里为null
  public  String cookie;
  //当前视频需要登录时，成功登录返回的记录。1表示登录成功，2表示登录不成功
  public static int flagToLogin = 0;

  private final static String USER_ONLY_MESSAGE = "当前视频需要登录";

  //网络状态标记
  private int netWorkFlag = 0;

  //联网
//  private HisonHttp hisonHttp;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.test_compatable);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    mPlayer = new IjkMediaPlayer();
    mVideoView = (IjkVideoView)findViewById(R.id.compatable_video_view);
    mHudView = (TableLayout)findViewById(R.id.compatable_hud_view);

    mVideoPath = "http://9890.vod.myqcloud.com/9890_9c1fa3e2aea011e59fc841df10c92278.f20.mp4";
    initialTabline();//初始化Tabline
    initial(); //测试代码
    initPlayer();


    //添加评论按钮监听
    imageButton = (ImageButton) findViewById(R.id.addComment);
    imageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      }
    });

  }

  //初始化播放器
  private void initPlayer(){
    mMediaController = new AndroidMediaController(this, false);
    IjkMediaPlayer.loadLibrariesOnce(null);
    IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    mVideoView.setMediaController(mMediaController);
    mVideoView.setHudView(mHudView);

    mVideoView.setVideoPath(mVideoPath);

    mVideoView.start();

  }

  //初始化tabline，设置每个tab的宽度
  private void initialTabline(){
    tabline = (ImageView) findViewById(R.id.tabline);
    Display display = getWindowManager().getDefaultDisplay();
    DisplayMetrics metrics = new DisplayMetrics();
    display.getMetrics(metrics);//把屏幕尺寸信息赋值给metrics
    screen1_3 = metrics.widthPixels / 3;
    ViewGroup.LayoutParams layoutParams = tabline.getLayoutParams();
    layoutParams.width = screen1_3;
    tabline.setLayoutParams(layoutParams);
  }

  //初始化界面 ViewPager ，设置 适配器 监听器
  private void initial() {
    mViewPager = (ViewPager) findViewById(R.id.viewpager);
    detailstextview = (TextView) findViewById(R.id.details);
    commenttextview = (TextView) findViewById(R.id.comment);
    relativetextview = (TextView) findViewById(R.id.relative);

    datas = new ArrayList<Fragment>();
//    DetailsFragment details = new DetailsFragment();
//    CommentFragment comment = new CommentFragment();
//    RelativeFragment relative = new RelativeFragment();
    Fragment details = new Fragment();
    Fragment comment = new Fragment();
    Fragment relative = new Fragment();
//    comment.setVideoId(videoId);
    datas.add(details);
    datas.add(comment);
    datas.add(relative);
    detailstextview.setOnClickListener(this);
    commenttextview.setOnClickListener(this);
    relativetextview.setOnClickListener(this);

    mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public int getCount() {
        return datas.size();
      }
      @Override
      public Fragment getItem(int position) {
        return datas.get(position);
      }
    };
    mViewPager.setAdapter(mAdapter);
    mViewPager.setOffscreenPageLimit(2);

    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        tabline = (ImageView) findViewById(R.id.tabline);
//                Log.d("boss", "" + position + "," + positionOffset + ","
//                        + positionOffsetPixels);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)
                tabline.getLayoutParams();
        if (currentPage == 0 && position == 0) {
          lp.leftMargin = (int) (currentPage * screen1_3 +
                  positionOffset * screen1_3);
        } else if (currentPage == 1 && position == 0) {
          lp.leftMargin = (int) (currentPage * screen1_3 +
                  (positionOffset - 1) * screen1_3);
        } else if (currentPage == 1 && position == 1) {
          lp.leftMargin = (int) (currentPage * screen1_3 +
                  positionOffset * screen1_3);
        } else if (currentPage == 2 && position == 1) {
          lp.leftMargin = (int) (currentPage * screen1_3 +
                  (positionOffset - 1) * screen1_3);
        }
        tabline.setLayoutParams(lp);
      }
      @Override
      public void onPageSelected(int position) {
        resetTextViewColor();
        switch (position) {
          case 0:
            detailstextview.
                    setTextColor(getResources().getColor(R.color.colorPrimary));
            break;
          case 1:
            commenttextview.
                    setTextColor(getResources().getColor(R.color.colorPrimary));
            break;
          case 2:
            relativetextview.
                    setTextColor(getResources().getColor(R.color.colorPrimary));
            break;
        }
        currentPage = position;
      }
      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  //重设字体颜色
  protected void resetTextViewColor(){
    detailstextview.setTextColor(Color.BLACK);
    commenttextview.setTextColor(Color.BLACK);
    relativetextview.setTextColor(Color.BLACK);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (mVideoView != null) {
      mVideoView.pause();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mVideoView != null) {
      mVideoView.resume();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mVideoView != null) {
      mVideoView.release(true);
    }
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (mVideoView != null) {
//      videoView.onConfigurationChanged(newConfig);
    }
    if(!isGone) {
      imageButton.setVisibility(View.GONE);
      isGone = true;
    }else{
      imageButton.setVisibility(View.VISIBLE);
      isGone = false;
    }
  }

  //监听器，ViewPager的界面三个按钮，切换page
  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.details:
        mViewPager.setCurrentItem(0);
        break;
      case R.id.comment:
        mViewPager.setCurrentItem(1);
        break;
      case R.id.relative:
        mViewPager.setCurrentItem(2);
        break;
      default:
        break;
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if(mVideoView != null) {
      mVideoView.pause();
    }
  }

}
