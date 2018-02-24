package com.anddle.anddleplayer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

//新建一个AboutActivity类 用来显示视频相关信息的类
public class AboutActivity extends AppCompatActivity {

    //检查是否将父类中的onCreate方法是否被重写
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //onCreate方法继承父类AppCompatActivity中的onCreat方法，传入参数为‘savedInstanceState’
        //onCreate创建一个以savedInstanceState为传入参数的activity
        super.onCreate(savedInstanceState);

        //setContentView就是设置一个Activity的显示界面，
        //这句话就是设置上面创建的activity采用R.layout下的
        //activity_about布局文件进行布局
        setContentView(R.layout.activity_about);

        //actionBar.setDisplayHomeAsUpEnabled(true) 给左上角图标的左边加上一个返回的图标
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //设置一个PackageManager方法变量manager，引用getPackageManager()方法。
        //http://blog.sina.com.cn/s/blog_8984d3f301011peb.html
        PackageManager manager = getPackageManager();
        PackageInfo info = null;

        //执行变量info的引用，如果引用过程中出现异常，打印输出异常
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //对变量version进行判断，并根据判断结果设置字符串变量version的引用
        //如果是空，则version=Unknown
        //如果不是空的，则version=info.versionName
        String version = info == null ? getString(R.string.unknown): info.versionName;

        //字符串变量msg引用，string.format是占位符
        //占位符的使用见http://blog.csdn.net/qq_25925973/article/details/54407994
        String msg = String.format(getString(R.string.verion_info), version);

        //创建textView变量ver，并引用findViewById(R.id.version_info)的结果
        //实际上就是让ver引用R.id.version_info代表的view
        TextView ver = (TextView) findViewById(R.id.version_info);

        //调用ver.setText的方法，将msg（也就是R.string.verion_info）的信息传递给ver相应的域
        ver.setText(msg);
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
}
