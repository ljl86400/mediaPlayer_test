package com.anddle.anddleplayer;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


public class VideoPlayer extends AppCompatActivity {

    private VideoView mVideoView;
    private int mLastPlayedTime;
    private final String LAST_PLAYED_TIME = "LAST_TIME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //通过启动VideoPlayer的Intent，获取视频播放的地址
        super.onCreate(savedInstanceState);
        Uri uri = getIntent().getData();
        String path = uri.getPath();

        if(path == null) {
            exit();
            return;
        }
        setContentView(R.layout.activity_video_player);

        //判断当前Activity是横屏还是竖屏
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String[] searchKey = new String[]{
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Video.Media.WIDTH,
                    MediaStore.Video.Media.HEIGHT,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATE_ADDED
            };
            String where = MediaStore.Video.Media.DATA + " = '" + path + "'";
            String[] keywords = null;
            String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, searchKey, where, keywords, sortOrder);

            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToNext();
                    String createdTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    int size = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                    int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
                    int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
                    VideoItem item = new VideoItem(path, name, createdTime);
                    TextView title = (TextView) findViewById(R.id.video_title);
                    title.setText(item.name);
                    TextView created = (TextView) findViewById(R.id.video_create_time);
                    created.setText(item.createdTime);
                    TextView screen = (TextView) findViewById(R.id.video_width_height);
                    screen.setText(width + "*" + height);
                    TextView fileSize = (TextView) findViewById(R.id.video_size);
                    fileSize.setText(String.valueOf(size / 1024 / 1024) + "M");
                } else {
                    TextView title = (TextView) findViewById(R.id.video_title);
                    title.setText(R.string.unknown);
                    TextView created = (TextView) findViewById(R.id.video_create_time);
                    created.setText(R.string.unknown);
                    TextView screen = (TextView) findViewById(R.id.video_width_height);
                    screen.setText(R.string.unknown);
                    TextView fileSize = (TextView) findViewById(R.id.video_size);
                    fileSize.setText(R.string.unknown);
                }
                cursor.close();
            }
        }
        //对这个Activity进行全屏的设置
        //因为要使应用全屏，所以需要修改Activity所属的窗口-Window的属性。以此告诉系统需要隐藏状态栏和导航栏。
        //还需要隐藏ActionBar。
        else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }
        //播放视频可以使用Android SDK提供的现成的控件VideoView，它是对media player和surface的封装。
        //获取布局文件中的VideoView，让后设置要播放的视频地址
        mVideoView = (VideoView) findViewById(R.id.video_view);
        mVideoView.setVideoPath(path);
        //为VideoView添加控制面板-MediaController，这个面板集成了播放进度拖动、暂停、继续播放等功能，
        // 还可以自动隐藏或显示。如果VideoView有父布局，那么为它添加的MediaController是附着在父布局
        // 的底部的。因此为了界面美观，我们在布局文件中，将VideoView单独放到一个FrameLayout当中。
        MediaController controller = new MediaController(this);
        mVideoView.setMediaController(controller);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        mLastPlayedTime = mVideoView.getCurrentPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
        if(mLastPlayedTime > 0) {
            mVideoView.seekTo(mLastPlayedTime);
        }
    }

    //在旋转的过程中,这个Activity要被重新创建一次，所以当视频正在播放的时候，
    // 我们要保存好视频当前播放的位置，Activity重建以后才能从之前播放到的位置继续播放。
    //我们要在onSaveInstanceState()里面保存当前播放的位置
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_PLAYED_TIME, mVideoView.getCurrentPosition());
    }

    //在onRestoreInstanceState()里面取出打断播放时的位置，并存储到mLastPlayedTime里面，
    // 当Activity在onResume()的时候，就能够跳转到播放点开始播放了。
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastPlayedTime = savedInstanceState.getInt(LAST_PLAYED_TIME);
    }

    private void exit()
    {
        Toast.makeText(this, R.string.no_playing_target, Toast.LENGTH_SHORT).show();
        finish();
    }
}
