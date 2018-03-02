package com.anddle.anddleplayer;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

//根据AndroidManiFest.xml文档中的定义，本VideoListActivity是这个安卓app的程序入口activity
//android应用程序提供的是入口Activity,而非入口函数（实际上这个入口是从父类里面继承来的，真正的入口
// 应该是父类中的onCreate，现在是VideoListActivity的onCreate）.
//http://blog.csdn.net/lll1204019292/article/details/52349896
//声明一个VideoListActivity公共类，继承AppCompatActivity父类，并扩展AdapterView.OnItemClickListener接口
//这个接口定义了当AdapterView中一元素被点击时，一个回调函数被调用。
//http://blog.csdn.net/wd916913/article/details/7026791
public class VideoListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    //公共静态域，不可修改：Tag
    public static final String TAG = "Media Player";
    //私有同步任务类型域
    private AsyncTask mVideoUpdateTask;
    //私有泛型列表mVideoList，用来存储VideoItem型对象元素，根据VideoItem类的声明，VideoItem对象包含
    // 名字、路径、缩略图创建时间等，以及父类的一些定义
    private List<VideoItem> mVideoList;
    //私有图像列表域mVideoListView
    private ListView mVideoListView;
    //私有的按键选项域mRefreshMenuItem
    private MenuItem mRefreshMenuItem;

    @Override
    //受保护的创建方法，传入savedInstanceState（之前窗口被关闭时保存下来的状态标记）
    //在一个Activity结束前保存状态，就将状态数据保存在onSaveInstanceState中，
    // 以key-value的形式放入到saveInstanceState。当一个Activity被创建时，
    // 就能从onCreate的参数saveInstanceState中获得状态数据。
    //根据android VM和AndroidManifest.xml中的设定；程序以VideoListActivity的onActivity为
    //整个安卓程序的入口
    protected void onCreate(Bundle savedInstanceState) {
        //调用父类onCreate方法，按照之前窗口关闭前保存的状态创建一个窗口
        //功能类似于恢复窗口
        super.onCreate(savedInstanceState);
        //替换视图，传入参数是activity_video_list.xml的视图描述方法
        //http://blog.csdn.net/nugongahou110/article/details/49662211
        setContentView(R.layout.activity_video_list);
        //调用本类实例的setTitle方法，设置实例的题目
        this.setTitle(R.string.video_list);
        //私有域引用一个实例化的泛型数组列表变量，用来存放VideoItem型元素对象
        mVideoList = new ArrayList<VideoItem>();
        //在相关的xml文件中有@+id/video_list 这样的语法形式时，VM将自动在R.java文件中
        // 创建 R.id.item_detail_container 这样的常量
        //进行前置类型转换
        mVideoListView = (ListView) findViewById(R.id.video_list);
        //新建一个变量引用实例化的VideoItemAdapter对象，对象内容包括视频显示？以及视频相关信息
        // 创建时间、缩略图等的设定
        //ListView负责内容的显示，Adapter负责为ListView提供要展示的数据。
        VideoItemAdapter adapter = new VideoItemAdapter(this, R.layout.video_item, mVideoList);
        mVideoListView.setAdapter(adapter);
        mVideoListView.setOnItemClickListener(this);
        //更新视频列表
        updateVideoList();
    }

    private void updateVideoList()
    {
        //实例化一个视频更新任务对象
        //启动VideoUpdateTask，开始查询符合我们要求的视频信息。
        mVideoUpdateTask = new VideoUpdateTask();
        mVideoUpdateTask.execute();
        if(mRefreshMenuItem != null) {
            mRefreshMenuItem.setTitle(R.string.in_refresh);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在视频列表Activity退出的时候，判断VideoUpdateTask是否还在运行，如果还在运行，就让它停止
        if((mVideoUpdateTask != null) &&
                (mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING))
        {
            mVideoUpdateTask.cancel(true);
        }
        mVideoUpdateTask = null;
    }

    @Override
    //为ListView添加一个数据项点击时的监听函数
    //实现ListView的OnItemClickListener接口
    //把要播放视频地址的URI放入Intent
    //通过Intent，启动视频播放器的Activity－VideoPlayer
    //Intent是安卓系统当中连接各个组件之间的桥梁，它可以：
    //唤醒指定的组件，让它开始运行。例如在Activity A启动Activity B，或者启动一个Service A；
    //向各个组件传递数据；
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        VideoItem item = mVideoList.get(position);
        Intent i = new Intent(this, VideoPlayer.class);
        i.setData(Uri.parse(item.path));
        startActivity(i);
    }

    @Override
    //在onCreateOptionsMenu()函数中，使用定义的菜单，获取“刷新”功能的菜单项，
    // 根据当前VideoUpdateTask的状态，来确定要显示的菜单名字
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        //获取“刷新”菜单项
        mRefreshMenuItem = menu.findItem(R.id.menu_refresh);
        //当VideoUpdateTask处于运行的状态时，菜单项的标题显示“停止刷新”
        if((mVideoUpdateTask != null) && (mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING)) {
            mRefreshMenuItem.setTitle(R.string.in_refresh);
        }
        //当VideoUpdateTask没有处于运行的状态时，菜单项的标题显示“刷新”
        else {
            mRefreshMenuItem.setTitle(R.string.refresh);
        }
        return true;
    }

    @Override
    //在onOptionsItemSelected()中，根据当前VideoUpdateTask的状态，来确定如何响应用户的点击操作
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.menu_refresh:
            {
                if((mVideoUpdateTask != null) && (mVideoUpdateTask.getStatus() == AsyncTask.Status.RUNNING)) {
                    //当VideoUpdateTask处于运行的状态时，取消VideoUpdateTask的工作
                    mVideoUpdateTask.cancel(true);
                    mVideoUpdateTask = null;
                }
                else {
                    updateVideoList();
                }
            }
            break;
            case R.id.menu_about:
            {
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
            }
            break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }


    //自己定义个AsyncTask－VideoUpdateTask
    //1】不需要为新创建的线程传入参数；所以Param设置成Object；
    //2】因为查询的过程很长，所以需要时不时通知主线程查询的状态，每查询到一条，
     // 就将视频数据传递给主线程；所以Progress设置成VideoItem；
    //3】查询的结果已经在查询的过程中发送给了主线程，全部完成后，不需要再传递什么结果给主线程了，
     // 所以Result设置成Void；
    //4】将查询视频信息的操作放到doInBackground()中进行，这是一个新创建的工作线程；
    //5】工作线程中，每发现一个视频，就通知给主线程；
    private class VideoUpdateTask  extends AsyncTask<Object, VideoItem, Void> {
        List<VideoItem> mDataList = new ArrayList<VideoItem>();

        @Override
        protected Void doInBackground(Object... params) {
            Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            String[] searchKey = new String[] {
                    MediaStore.Video.Media.TITLE,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_ADDED
            };
            String where = MediaStore.Video.Media.DATA + " like \"%"+getString(R.string.search_path)+"%\"";
            String [] keywords = null;
            String sortOrder = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            ContentResolver resolver = getContentResolver();
            Cursor cursor = resolver.query(uri, searchKey, where, keywords, sortOrder);
            //添加视频信息到ListView的数据集－mVideoItemList之前，先判断里面是否已
            //经包含了这个视频，如果没有包含，才发送给主线程更新界面；比较视频是否相同的依据，
            // 就是前面VideoItem中重写的equals()函数
            if(cursor != null)
            {
                while(cursor.moveToNext() && ! isCancelled())
                {
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                    String createdTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                    VideoItem data = new VideoItem(path, name, createdTime);
                    Log.d(TAG, "real video found: " + path);
                    if(mVideoList.contains(data) == false) {
                        //判断需要添加，才创建缩略图
                        data.createThumb();
                        publishProgress(data);
                    }
                    //如果用户暂停刷新后，设备中的视频因为别的原因被删除了一个
                    // ListView中的保存的视频信息个数就会比真实刷新到的要多，
                    // 因为没有把ListView中多余的数据给清除掉。
                    //所以，要把ListView中所有多余的视频清除，这只能在视频查询完成后才能进行，
                    // 而且这些真正存在的视频信息还得保存下来，在最后的比较中会使用到。
                    mDataList.add(data);
                }
                cursor.close();
            }
            return null;
        }

        @Override
        //将获取的新的视频信息，添加到数据列表中，并使用notifyDataSetChanged()刷新
        protected void onProgressUpdate(VideoItem... values) {
            VideoItem data = values[0];
            mVideoList.add(data);
            VideoItemAdapter adapter = (VideoItemAdapter) mVideoListView.getAdapter();
            adapter.notifyDataSetChanged();
        }

        @Override
        //在VideoUpdateTask被成功取消工作后，调用onCancelled()方法；
        // 在VideoUpdateTask工作顺利完成后，调用onPostExecute()方法，
        // 它们是在主线程中运行的，所以可以在这里修改界面
        protected void onPostExecute(Void result) {
            Log.d(TAG, "Task has been finished");
            updateResult();
        }

        @Override
        protected void onCancelled() {
            Log.d(TAG, "Task has been cancelled");
            updateResult();
        }

        //VideoUpdateTask结束工作以后(无论是取消还是顺利完成)，
        // 会调用到onCancelled() onPostExecute()，所以可以在它们共同调用的updateResult()
        // 中清除ListView中多余的视频信息
        private void updateResult()
        {
            for(int i = 0; i < mVideoList.size(); i++)
            {
                if(!mDataList.contains(mVideoList.get(i)))
                {
                    //释放缩略图占用的内存资源
                    mVideoList.get(i).releaseThumb();
                    //从ListView的数据集中移除多余的视频信息
                    mVideoList.remove(i);
                    //因为移除了一个视频项，下一个视频项的序号就被减小了一个1
                    i--;
                }
            }
            mDataList.clear();
            VideoItemAdapter adapter = (VideoItemAdapter) mVideoListView.getAdapter();
            adapter.notifyDataSetChanged();
            //修改菜单项的标题为“停止刷新”
            if(mRefreshMenuItem != null) {
                mRefreshMenuItem.setTitle(R.string.refresh);
            }
        }
    }
}
