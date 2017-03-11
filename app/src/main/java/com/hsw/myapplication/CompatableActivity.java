package com.hsw.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
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
 * Created by HSW on 2017/3/9
 */

public class CompatableActivity extends AppCompatActivity implements View.OnClickListener{

  private IjkVideoView mVideoView;
  private ViewPager mViewPager;
  private FragmentPagerAdapter mAdapter;
  private List<Fragment> datas;
  private TextView detailstextview;
  private TextView commenttextview;
  private TextView relativetextview;
  private ImageView tabline;
//  private Toolbar mToolbar;
  private ActionBar actionBar;

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

    //判断有没有写外部存储的权限
//    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            != PackageManager.PERMISSION_GRANTED) {
//      //进入到这里代表没有权限.
//      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},1);
//    }

    mVideoView = (IjkVideoView)findViewById(R.id.compatable_video_view);
    mHudView = (TableLayout)findViewById(R.id.compatable_hud_view);
//    mToolbar = (Toolbar)findViewById(R.id.compatable_toolbar);
//    setSupportActionBar(mToolbar);
//    actionBar = getSupportActionBar();

    mVideoPath = "http://9890.vod.myqcloud.com/9890_9c1fa3e2aea011e59fc841df10c92278.f20.mp4";
//    Intent intent = getIntent();
//    videoId = intent.getIntExtra("videoId",558);

//    DbUserImpl dbUser = new DbUserImpl(this);
//    int id = dbUser.hasUser();
//    if(id != 0){
//      cookie = dbUser.getUserBean(id).getCookie();
//    }

    initialTabline();//初始化Tabline
    initial(); //测试代码
    initPlayer();


    //联网
//    hisonHttp = new HisonHttp(this) {
//      @Override
//      public String httpRequest(RequestParam param) {
//        NetUtil netUtil = NetUtil.getNetUtil();
//        netUtil.addVideoRecord(cookie,videoId);
//        String json = netUtil.getVideoInformation(NetUtil.getVideoAction,videoId,cookie);
//        return json;
//      }
//    };
//    hisonHttp.setOnFlushUiListener(new OnFlushUiListener() {
//      @Override
//      public void onFlush(String json) {
//        if(json == null){
//          Toast.makeText(VideoPlayerActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
//          return;
//        }
//        bean = BuildBeanFromJson.getVideoBeanInfoByIdFromJson(json);
//
//        //判断是否需要登陆才能观看
//        if (cookie == null && bean.getUser_only() == 1) {
//          Toast.makeText(VideoPlayerActivity.this, USER_ONLY_MESSAGE, Toast.LENGTH_SHORT).show();
//
//          Intent i = new Intent(VideoPlayerActivity.this, MyLoginActivity.class);
//          i.putExtra("fromWhere", 1);
//          startActivity(i);
//
//        }else {
//          initPlayer();
//        }
//      }
//    });
//    hisonHttp.executeHttp(null);

    //添加评论按钮监听
    imageButton = (ImageButton) findViewById(R.id.addComment);
    imageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //判断是否有用户登陆，没有跳到登陆界面，有跳到添加评论界面
//        if(cookie == null){
//          Toast.makeText(hison.hisonedu.videoplay.VideoPlayerActivity.this,"请登录后再添加评论",Toast.LENGTH_SHORT).show();
//          startActivity(new Intent(VideoPlayerActivity.this, MyLoginActivity.class));
//        }else {
//          Intent i = new Intent(VideoPlayerActivity.this, AddCommentActivity.class);
//          i.putExtra("videoId", videoId);
//          startActivity(i);
//        }
      }
    });

//    NetBroadcastReceiver netBroadcastReceiver = new NetBroadcastReceiver();
//    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
//    //注册网络广播接收者
//    registerReceiver(netBroadcastReceiver,intentFilter);
  }

  //初始化播放器
  private void initPlayer(){
    mMediaController = new AndroidMediaController(this, false);
//    mMediaController.setSupportActionBar(actionBar);
    IjkMediaPlayer.loadLibrariesOnce(null);
    IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    mVideoView.setMediaController(mMediaController);
    mVideoView.setHudView(mHudView);

    mVideoView.setVideoPath(mVideoPath);

    mVideoView.start();

    //刷新简介页面
//    ( (DetailsFragment)datas.get(0)).flushView(bean,cookie);
//    list = new ArrayList<>();
//    HttpProxyCacheServer proxy = App.getProxy(VideoPlayerActivity.this);
//    proxy.registerCacheListener(this,bean.getVideopath());
//    String proxyUrl = proxy.getProxyUrl(bean.getVideopath());
//    Log.i("senninha",proxyUrl);
//    list.add(new VideoStreamBean(proxyUrl,"标清"));

//    videoView = new GiraffePlayer(VideoPlayerActivity.this,list);
//    videoView.setTitle(bean.getVideoname());
//    videoView.onComplete(new Runnable() {
//      @Override
//      public void run() {
//        //callback when video is finish
//        Toast.makeText(getApplicationContext(), "播放完毕",Toast.LENGTH_SHORT).show();
//      }
//    }).onInfo(new GiraffePlayer.OnInfoListener() {
//      @Override
//      public void onInfo(int what, int extra) {
//        switch (what) {
//          case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
//            //do something when buffering start
//            break;
//          case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
//            //do something when buffering end
//            break;
//          case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
//            //download speed
//            Log.i("senninha","下载速度");
//            break;
//          case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//            //do something when video rendering
//            Log.i("senninha","加载速度");
//            break;
//        }
//      }
//    }).onError(new GiraffePlayer.OnErrorListener() {
//      @Override
//      public void onError(int what, int extra) {
//        Toast.makeText(getApplicationContext(), "video play error",Toast.LENGTH_SHORT).show();
//      }
//    });
//
//    //设置是否直播
//    videoView.senninhaLive(false);
//    videoView.play(proxyUrl);

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
    detailstextview.setTextColor(getResources().getColor(R.color.colorAccent));

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
                    setTextColor(getResources().getColor(R.color.colorAccent));
            break;
          case 1:
            commenttextview.
                    setTextColor(getResources().getColor(R.color.colorAccent));
            break;
          case 2:
            relativetextview.
                    setTextColor(getResources().getColor(R.color.colorAccent));
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

//  @Override
//  protected void onRestart() {
//    super.onRestart();
//    if(flagToLogin == 1){
//      initPlayer();
//      flagToLogin = 0;
//    }else if(flagToLogin == 2){
//      flagToLogin = 0;
//      new AlertDialog.Builder(this).setTitle(USER_ONLY_MESSAGE).setPositiveButton("登录", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//          Toast.makeText(VideoPlayerActivity.this, USER_ONLY_MESSAGE, Toast.LENGTH_SHORT).show();
//          Intent intent = new Intent(VideoPlayerActivity.this, MyLoginActivity.class);
//          intent.putExtra("fromWhere",1);
//          startActivity(intent);
//        }
//      }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//          finish();
//        }
//      }).setOnKeyListener(new DialogInterface.OnKeyListener() {
//        @Override
//        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
//          if(keyCode == KeyEvent.KEYCODE_BACK){
//            finish();
//          }
//          return  true;
//        }
//      }).show();
//    }
//  }

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

//  @Override
//  public void onBackPressed() {
//    if (videoView != null && videoView.onBackPressed()==1) {
//      videoView.toggleFullScreen();
//      return;
//    }else if(videoView != null && videoView.onBackPressed()==2){
//      Toast.makeText(this,"请解锁后退出",Toast.LENGTH_SHORT).show();
//      return;
//    }
//    super.onBackPressed();
//  }

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


  class NetBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = manager.getActiveNetworkInfo();
      if(networkInfo == null && netWorkFlag == 0){
        netWorkFlag ++;
      }else if(netWorkFlag == 1 && networkInfo != null){
//        hisonHttp.executeHttp(null);
        netWorkFlag ++;
      }
    }
  }

}

