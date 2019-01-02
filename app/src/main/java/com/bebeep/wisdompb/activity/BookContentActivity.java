package com.bebeep.wisdompb.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.readpage.bean.BookList;
import com.bebeep.readpage.util.BrightnessUtil;
import com.bebeep.readpage.util.Config;
import com.bebeep.readpage.util.PageFactory;
import com.bebeep.readpage.view.PageWidget;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.BookEntity;
import com.bebeep.wisdompb.databinding.ActivityBookContentBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 * 文章内容
 *
 * 1、点右边-下一页
 * 2、点左边-上一页
 * 3、点中间-呼出菜单
 * 4、向右滑-上一页
 * 5、向左滑-下一页
 *
 * 记录阅读的位置
 * 记录设置的主题
 * 记录字体
 * 记录背景
 * 记录标记
 * 记录笔记  https://blog.csdn.net/u014614038/article/details/74451484
 * 记录书签
 *
 */
public class BookContentActivity extends BaseActivity implements View.OnClickListener{
    private ActivityBookContentBinding binding;

    private String id;

    private Config config;
    private WindowManager.LayoutParams lp;
    private BookList bookList;
    private PageFactory pageFactory;
    private int screenWidth, screenHeight;


    // 接收电池信息更新的广播
    private BroadcastReceiver myReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                pageFactory.updateBattery(level);
            }else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)){
                pageFactory.updateTime();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_content);
        init();
    }

    private void init(){
        bookList = (BookList) getIntent().getSerializableExtra("bookList");
        binding.setVariable(BR.onClickListener,this);

        IntentFilter mfilter = new IntentFilter();
        mfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mfilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(myReceiver, mfilter);

        config = Config.getInstance();
        pageFactory = PageFactory.getInstance();

        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);
        screenWidth = displaysize.x;
        screenHeight = displaysize.y;

        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //改变屏幕亮度
        if (!config.isSystemLight()) {
            BrightnessUtil.setBrightness(this, config.getLight());
        }
        binding.bookpage.setPageMode(config.getPageMode());

        pageFactory.setPageWidget(binding.bookpage);

        initView();

    }


    private void initView(){
        if(bookList == null) finish();
        try {
            pageFactory.openBook(bookList);
        } catch (IOException e) {
            e.printStackTrace();
            MyTools.showToast(this,"打开章节内容失败");
        }


        binding.bookpage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public void center() {
                LogUtil.showLog("center");
            }

            @Override
            public Boolean prePage() {
                pageFactory.prePage();
                return true;
            }

            @Override
            public Boolean nextPage() {
                pageFactory.nextPage();
                return true;
            }

            @Override
            public void cancel() {
                pageFactory.cancelPage();
            }
        });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_note:
                startActivity(new Intent(this,MyNoteActivity.class));
                break;
        }
    }
}
