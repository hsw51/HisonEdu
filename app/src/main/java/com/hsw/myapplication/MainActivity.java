package com.hsw.myapplication;

import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.example.content.RecentMediaStorage;
import tv.danmaku.ijk.media.example.fragments.TracksFragment;
import tv.danmaku.ijk.media.example.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.example.widget.media.MeasureHelper;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class MainActivity extends AppCompatActivity implements TracksFragment.ITrackHolder  {
  private static final String TAG = "MainActivity";

  private String mVideoPath;
  private Uri mVideoUri;

  private AndroidMediaController mMediaController;
  private IjkVideoView mVideoView;
  private TextView mToastTextView;
  private TableLayout mHudView;
  private DrawerLayout mDrawerLayout;
  private ViewGroup mRightDrawer;
  private Toolbar toolbar;
  private Settings mSettings;
  private boolean mBackPressed;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mToastTextView = (TextView)     findViewById(R.id.toast_text_view);
    mHudView       = (TableLayout)  findViewById(R.id.hud_view);
    mDrawerLayout  = (DrawerLayout) findViewById(R.id.drawer_layout);
    mRightDrawer   = (ViewGroup)    findViewById(R.id.right_drawer);
    mVideoView     = (IjkVideoView) findViewById(R.id.video_view);
    toolbar        = (Toolbar)      findViewById(R.id.toolbar);

    mSettings = new Settings(this);

//    mVideoUri = Uri.parse("Android.resource://"+getPackageName()+"/"+R.raw.test);
//    mVideoPath = mVideoUri.getPath();

    mVideoPath = "http://9890.vod.myqcloud.com/9890_9c1fa3e2aea011e59fc841df10c92278.f20.mp4";

    // handle arguments
//    mVideoPath = getIntent().getStringExtra("videoPath");

//    Intent intent = getIntent();
//    String intentAction = intent.getAction();
//    if (!TextUtils.isEmpty(intentAction)) {
//      if (intentAction.equals(Intent.ACTION_VIEW)) {
//        mVideoPath = intent.getDataString();
//      } else if (intentAction.equals(Intent.ACTION_SEND)) {
//        mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//          String scheme = mVideoUri.getScheme();
//          if (TextUtils.isEmpty(scheme)) {
//            Log.e(TAG, "Null unknown scheme\n");
//            finish();
//            return;
//          }
//          if (scheme.equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
//            mVideoPath = mVideoUri.getPath();
//          } else if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
//            Log.e(TAG, "Can not resolve content below Android-ICS\n");
//            finish();
//            return;
//          } else {
//            Log.e(TAG, "Unknown scheme " + scheme + "\n");
//            finish();
//            return;
//          }
//        }
//      }
//    }

    if (!TextUtils.isEmpty(mVideoPath)) {
      new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
    }


//    setSupportActionBar(toolbar);
//    ActionBar actionBar = getSupportActionBar();

    mMediaController = new AndroidMediaController(this, false);
//    mMediaController.setSupportActionBar(actionBar);

//    mDrawerLayout.setScrimColor(Color.TRANSPARENT);

    //初始化
    IjkMediaPlayer.loadLibrariesOnce(null);
    IjkMediaPlayer.native_profileBegin("libijkplayer.so");

    mVideoView.setMediaController(mMediaController);
    mVideoView.setHudView(mHudView);

    if (mVideoPath != null)
      mVideoView.setVideoPath(mVideoPath);
    else if (mVideoUri != null)
      mVideoView.setVideoURI(mVideoUri);
    else {
      Log.e(TAG, "Null Data Source\n");
      finish();
      return;
    }
    mVideoView.start();

  }

  @Override
  public void onBackPressed() {
    mBackPressed = true;

    super.onBackPressed();
  }

  @Override
  protected void onStop() {
    super.onStop();

    if (mBackPressed || !mVideoView.isBackgroundPlayEnabled()) {
      mVideoView.stopPlayback();
      mVideoView.release(true);
      mVideoView.stopBackgroundPlay();
    } else {
      mVideoView.enterBackground();
    }
    IjkMediaPlayer.native_profileEnd();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_player, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_toggle_ratio) {
      int aspectRatio = mVideoView.toggleAspectRatio();
      String aspectRatioText = MeasureHelper.getAspectRatioText(this, aspectRatio);
      mToastTextView.setText(aspectRatioText);
      mMediaController.showOnce(mToastTextView);
      return true;
    } else if (id == R.id.action_toggle_player) {
      int player = mVideoView.togglePlayer();
      String playerText = IjkVideoView.getPlayerText(this, player);
      mToastTextView.setText(playerText);
      mMediaController.showOnce(mToastTextView);
      return true;
    } else if (id == R.id.action_toggle_render) {
      int render = mVideoView.toggleRender();
      String renderText = IjkVideoView.getRenderText(this, render);
      mToastTextView.setText(renderText);
      mMediaController.showOnce(mToastTextView);
      return true;
    } else if (id == R.id.action_show_info) {
      mVideoView.showMediaInfo();
    } else if (id == R.id.action_show_tracks) {
      if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
        if (f != null) {
          FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
          transaction.remove(f);
          transaction.commit();
        }
        mDrawerLayout.closeDrawer(mRightDrawer);
      } else {
        Fragment f = TracksFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.right_drawer, f);
        transaction.commit();
        mDrawerLayout.openDrawer(mRightDrawer);
      }
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public ITrackInfo[] getTrackInfo() {
    if (mVideoView == null)
      return null;

    return mVideoView.getTrackInfo();
  }

  @Override
  public void selectTrack(int stream) {
    mVideoView.selectTrack(stream);
  }

  @Override
  public void deselectTrack(int stream) {
    mVideoView.deselectTrack(stream);
  }

  @Override
  public int getSelectedTrack(int trackType) {
    if (mVideoView == null)
      return -1;

    return mVideoView.getSelectedTrack(trackType);
  }

}
