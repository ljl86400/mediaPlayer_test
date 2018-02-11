package com.anddle.anddleplayer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;


public class AboutActivity extends AppCompatActivity {
    //AboutActivity 用来显示视频相关信息的类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //onCreat方法继承父类AppCompatActivity中的onCreat方法，传入参数为‘savedInstanceState’
        //onCreat创建一个以savedInstanceState为传入参数的activity

        setContentView(R.layout.activity_about);
        /*setContentView就是设置一个Activity的显示界面，
        这句话就是设置上面创建的activity采用R.layout下的
        activity_about布局文件进行布局*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayHomeAsUpEnabled(true) 给左上角图标的左边加上一个返回的图标

        PackageManager manager = getPackageManager();
        //设置一个PackageManager方法变量manager，引用getPackageManager()方法。
        /*http://blog.sina.com.cn/s/blog_8984d3f301011peb.html*/
        PackageInfo info = null;

        try {
            info = manager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        String version = info == null ? getString(R.string.unknown): info.versionName;
        //对变量version进行判断，并根据判断结果设置字符串变量version的引用
        //如果是空，则version=Unknown
        //如果不是空的，则version=info.versionName

        String msg = String.format(getString(R.string.verion_info), version);

        TextView ver = (TextView) findViewById(R.id.version_info);
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
