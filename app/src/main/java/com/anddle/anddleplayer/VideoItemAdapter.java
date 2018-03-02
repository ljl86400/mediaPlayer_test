package com.anddle.anddleplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

//声明一个继承ArrayAdapter<VideoItem>的公共类子VideoItemAdapter
//继承ArrayAdapter，将显示的数据类型指定成VideoItem；重新构造函数，
// 传入Context，数据项布局使用的布局ID，要显示的数据列表；重写它的getView()方法；
public class VideoItemAdapter extends ArrayAdapter<VideoItem> {
    //声明私有最终域LayoutInflater型的mInflater、int型的mResource
    private final LayoutInflater mInflater;
    private final int mResource;

    //声明构造方法
    //context 它描述的是一个应用程序环境的信息，通过它我们可以获取应用程序的资源和类，
    // 也包括一些应用级别操作
    // http://blog.csdn.net/new_life_sjtu/article/details/52276519
    //在构造函数中，保存好布局ID以后使用，通过Context获取Inflater，为以后数据项布局的创建做准备，
    public VideoItemAdapter(Context context, int resource, List<VideoItem> objects) {
        //调用父类的相应构造方法
        super(context, resource, objects);
        //在实际开发中LayoutInflater这个类还是非常有用的，它的作用类似于findViewById()。
        // 不同点是LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化；
        // 而findViewById()是找xml布局文件下的具体widget控件(如 Button、TextView等)。
        mInflater = LayoutInflater.from(context);
        //实例化域mResource
        mResource = resource;
    }

    @Override
    //公共的View型方法getView取得图像
    //在getView()函数中，创建数据项的布局，并为他们赋值，最后将这个布局返回给ListView，让它显示
    //这里的convertView就是数据项所代表的那个布局，当ListView刚创建，还没有产生任何数据项的时候，
    // 它就是为null的，此时我们就需要创建一个布局，并通过getView()将这个布局返回给ListView。
    //假如ListView上的数据项布局已经足够了，那么这里传入的convertView就不会再是“null”，
    // 而是之前的某个数据项布局，我们就不必为此重新创建了，只需要更新上面的内容就好。这样提高了界面刷新的效率。
    public View getView(int position, View convertView, ViewGroup parent) {

        //对于一个没有被载入或者想要动态载入的界面，都需要使用LayoutInflater类中
        // 的inflate()方法来载入
        //http://blog.csdn.net/robertcpp/article/details/51523218
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, parent, false);
        }
        //新建变量item并引用getItem(position)
        VideoItem item = getItem(position);
        //新建变量并引用convertView.findViewById(R.id.video_title)强制类型转换后的结果
        TextView title = (TextView) convertView.findViewById(R.id.video_title);
        //设置标题
        title.setText(item.name);
        //新建变量createTime存放创建时间
        // 并应用convertView.findViewById(R.id.video_date)强制类型转换后的结果
        TextView createTime = (TextView) convertView.findViewById(R.id.video_date);
        //调用createTime.setText设定文本内容
        createTime.setText(item.createdTime);
        //新建变量存放缩略图
        ImageView thumb = (ImageView) convertView.findViewById(R.id.vidoe_thumb);
        //缩略图的一些设置
        thumb.setImageBitmap(item.thumb);
        //返回方法运行结果
        return convertView;
    }
}
