package com.anddle.anddleplayer;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import java.text.SimpleDateFormat;
import java.util.Date;

//建立一个公共的类VideoItem
public class VideoItem {

    //建立域，应该是公共的：包含名字、路径、缩略图、创建时间
    String name;
    String path;
    Bitmap thumb;
    String createdTime;

    //构造方法，传入字符串型路径、名字、创建时间
    VideoItem(String strPath, String strName, String createdTime) {

        //域值“路径”引用传入的路径字符串，域值“名字”引用传入的名字字符串
        this.path = strPath;
        this.name = strName;

        //创建一个时间类型变量，引用新建的时间对象
        SimpleDateFormat sf = new SimpleDateFormat("yy年MM月dd日HH时mm分");
        //新建一个date变量d，引用新建立的date实例
        Date d = new Date(Long.valueOf(createdTime)*1000);
        //通过调用sf.format方法计算域值createdTime的要引用的值
        this.createdTime = sf.format(d);
    }

    //定义一个创建缩略图的方法用来设置域值中的thumb变量，没有返回值，没有传入参数
    void createThumb()
    {
        //如果域值thumb是空，则调用ThumbnailUtils.createVideoThumbnail方法创建一个缩略图，并引用
        if(this.thumb == null)
        {
            this.thumb = ThumbnailUtils.createVideoThumbnail(this.path, MediaStore.Images.Thumbnails.MINI_KIND);
        }
    }

    //定义一个释放缩略图的方法
    void releaseThumb()
    {
        //如果域值thumb不为空，将thumb设置为空
        if(this.thumb != null){
            //将调用此方法的缩略图回收
            this.thumb.recycle();
            //将缩略图的域设置为空
            this.thumb = null;
        }
    }

    @Override
    //当应用启动后，VideoUpdateTask开始更新视频信息，此时用户点击“暂停刷新”，任务停止了，
    // 然后用户又点击“刷新”。我们会发现以前被列出的视频再次被列了出来。
    //这是因为刷新的时候，没有将已经显示的视频与没有显示的视频区分开，已经添加过的又被重新添加了。
    //解决的办法就是，在添加视频信息到数据列表里面之前，先检查一下这些视频是否已经被添加过了，
    // 如果添加过了，那就不用再添加了。
    //重写VideoItem的比较方法，让比较两个VideoItem的原则变成：只要文件所在的路径是相同的，
    // 就认为这两个比较项指的是同一个
    //公共boolean型方法，返回值为：false|true，传入参数是一个对象（Object）变量
    public boolean equals(Object o) {
        //创建一个VideoItem变量another，并引用传入的对象变量o；
        VideoItem another = (VideoItem) o;
        //返回传入对象的路径是否等于调用此方法的对象的路径判断结果：false|true
        return another.path.equals(this.path);
    }
}