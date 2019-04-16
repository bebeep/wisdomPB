package com.bebeep.wisdompb.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.Toast;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.readpage.bean.BookCatalogue;
import com.bebeep.readpage.bean.BookList;
import com.bebeep.readpage.bean.BookMarks;
import com.bebeep.readpage.dialog.PageModeDialog;
import com.bebeep.readpage.dialog.SettingDialog;
import com.bebeep.readpage.util.BrightnessUtil;
import com.bebeep.readpage.util.Config;
import com.bebeep.readpage.util.PageFactory;
import com.bebeep.readpage.view.PageWidget;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseActivity;
import com.bebeep.wisdompb.base.BaseAppCompatActivity;
import com.bebeep.wisdompb.databinding.ActivityBookContentBinding;
import com.bebeep.wisdompb.util.LogUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class BookContentActivity extends BaseAppCompatActivity implements View.OnClickListener{
    private ActivityBookContentBinding binding;
    private final static String EXTRA_BOOK = "bookList";
    private final static int MESSAGE_CHANGEPROGRESS = 1;

    private String id;

    private Config config;
    private WindowManager.LayoutParams lp;
    private BookList bookList;
    private PageFactory pageFactory;
    private int screenWidth, screenHeight;

    private Boolean isShow = false;
    private SettingDialog mSettingDialog;
    private PageModeDialog mPageModeDialog;
    private Boolean mDayOrNight;

    private ArrayList<BookCatalogue> catalogueList = new ArrayList<>();//目录

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

        binding.toolbar.setTitle("");
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationIcon(R.drawable.return_button);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        IntentFilter mfilter = new IntentFilter();
        mfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mfilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(myReceiver, mfilter);


        config = Config.getInstance();
        pageFactory = PageFactory.getInstance();



        mSettingDialog = new SettingDialog(this);
        mPageModeDialog = new PageModeDialog(this);


        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);
        screenWidth = displaysize.x;
        screenHeight = displaysize.y;

        hideSystemUI();

        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //改变屏幕亮度
        if (!config.isSystemLight()) {
            BrightnessUtil.setBrightness(this, config.getLight());
        }
        binding.bookpage.setPageMode(config.getPageMode());

        pageFactory.setPageWidget(binding.bookpage);

        initListener();
        initDayOrNight();
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
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_note:
                startActivity(new Intent(this,MyNoteActivity.class));
                break;
            case R.id.tv_progress:
                break;
            case R.id.rl_progress:
                break;
            case R.id.tv_pre:
                pageFactory.preChapter();
                break;
            case R.id.sb_progress:
                break;
            case R.id.tv_next:
                pageFactory.nextChapter();
                break;
            case R.id.tv_directory:
                hideReadSetting();
                catalogueList.addAll(pageFactory.getDirectoryList());
                LogUtil.showLog("目录："+ MyApplication.gson.toJson(catalogueList));
                Intent intent = new Intent(BookContentActivity.this, CatalogActivity.class);
                intent.putExtra("catalogueList",catalogueList);
                intent.putExtra("title",bookList.getBookname());
                startActivity(intent);

                break;
            case R.id.tv_dayornight:
                changeDayOrNight();
                break;
            case R.id.tv_pagemode:
                mPageModeDialog.show();
                break;
            case R.id.tv_setting:
                mSettingDialog.show();
                break;
            case R.id.bookpop_bottom:
                break;
            case R.id.rl_bottom:
                break;
        }
    }

    private void initListener() {
        binding.sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            float pro;
            // 触发操作，拖动
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pro = (float) (progress / 10000.0);
                showProgress(pro);
            }

            // 表示进度条刚开始拖动，开始拖动时候触发的操作
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // 停止拖动时候
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                pageFactory.changeProgress(pro);
            }
        });

        mPageModeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        mPageModeDialog.setPageModeListener(new PageModeDialog.PageModeListener() {
            @Override
            public void changePageMode(int pageMode) {
                binding.bookpage.setPageMode(pageMode);
            }
        });

        mSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                hideSystemUI();
            }
        });

        mSettingDialog.setSettingListener(new SettingDialog.SettingListener() {
            @Override
            public void changeSystemBright(Boolean isSystem, float brightness) {
                if (!isSystem) {
                    BrightnessUtil.setBrightness(BookContentActivity.this, brightness);
                } else {
                    int bh = BrightnessUtil.getScreenBrightness(BookContentActivity.this);
                    BrightnessUtil.setBrightness(BookContentActivity.this, bh);
                }
            }

            @Override
            public void changeFontSize(int fontSize) {
                pageFactory.changeFontSize(fontSize);
            }

            @Override
            public void changeTypeFace(Typeface typeface) {
                pageFactory.changeTypeface(typeface);
            }

            @Override
            public void changeBookBg(int type) {
                pageFactory.changeBookBg(type);
            }
        });

        pageFactory.setPageEvent(new PageFactory.PageEvent() {
            @Override
            public void changeProgress(float progress) {
                Message message = new Message();
                message.what = MESSAGE_CHANGEPROGRESS;
                message.obj = progress;
                mHandler.sendMessage(message);
            }
        });

        binding.bookpage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public void center() {
                if (isShow) {
                    hideReadSetting();
                } else {
                    showReadSetting();
                }
            }

            @Override
            public Boolean prePage() {
                if (isShow){
                    return false;
                }

                pageFactory.prePage();
                if (pageFactory.isfirstPage()) {
                    return false;
                }

                return true;
            }

            @Override
            public Boolean nextPage() {
                LogUtil.showLog( "nextPage");
                if (isShow){
                    return false;
                }

                pageFactory.nextPage();
                if (pageFactory.islastPage()) {
                    return false;
                }
                return true;
            }

            @Override
            public void cancel() {
                pageFactory.cancelPage();
            }
        });

    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_CHANGEPROGRESS:
                    float progress = (float) msg.obj;
                    setSeekBarProgress(progress);
                    break;
            }
        }
    };




    @Override
    protected void onResume(){
        super.onResume();
        if (!isShow){
            hideSystemUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.showLog("destroy");
        pageFactory.clear();
        unregisterReceiver(myReceiver);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mSettingDialog.isShowing()){
                mSettingDialog.hide();
                return true;
            }
            if (mPageModeDialog.isShowing()){
                mPageModeDialog.hide();
                return true;
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.read, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_add_bookmark){
            if (pageFactory.getCurrentPage() != null) {
                List<BookMarks> bookMarksList = DataSupport.where("bookpath = ? and begin = ?", pageFactory.getBookPath(),pageFactory.getCurrentPage().getBegin() + "").find(BookMarks.class);
                if (!bookMarksList.isEmpty()){
                    MyTools.showToast(this,"该书签已存在");
                }else {
                    BookMarks bookMarks = new BookMarks();
                    String word = "";
                    for (String line : pageFactory.getCurrentPage().getLines()) {
                        word += line;
                    }
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm ss");
                        String time = sf.format(new Date());
                        bookMarks.setTime(time);
                        bookMarks.setBegin(pageFactory.getCurrentPage().getBegin());
                        bookMarks.setText(word);
                        bookMarks.setBookpath(pageFactory.getBookPath());
                        bookMarks.save();
                        MyTools.showToast(this,"书签添加成功");
                    } catch (Exception e) {
                        MyTools.showToast(this,"添加书签失败");
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * 隐藏菜单。沉浸式阅读
     */
    private void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        //  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    //显示书本进度
    public void showProgress(float progress){
        if (binding.rlProgress.getVisibility() != View.VISIBLE) {
            binding.rlProgress.setVisibility(View.VISIBLE);
        }
        setProgress(progress);
    }

    //隐藏书本进度
    public void hideProgress(){
        binding.rlProgress.setVisibility(View.GONE);
    }

    public void initDayOrNight(){
        mDayOrNight = config.getDayOrNight();
        if (mDayOrNight){
            binding.tvDayornight.setText(getResources().getString(R.string.read_setting_day));
        }else{
            binding.tvDayornight.setText(getResources().getString(R.string.read_setting_night));
        }
    }

    //改变显示模式
    public void changeDayOrNight(){
        if (mDayOrNight){
            mDayOrNight = false;
            binding.tvDayornight.setText(getResources().getString(R.string.read_setting_night));
        }else{
            mDayOrNight = true;
            binding.tvDayornight.setText(getResources().getString(R.string.read_setting_day));
        }
        config.setDayOrNight(mDayOrNight);
        pageFactory.setDayOrNight(mDayOrNight);
    }

    private void setProgress(float progress){
        DecimalFormat decimalFormat=new DecimalFormat("00.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p=decimalFormat.format(progress * 100.0);//format 返回的是字符串
        binding.tvProgress.setText(p + "%");
    }

    public void setSeekBarProgress(float progress){
        binding.sbProgress.setProgress((int) (progress * 10000));
    }


    private void showReadSetting(){
        isShow = true;
        binding.rlProgress.setVisibility(View.GONE);

        showSystemUI();

        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_enter);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_enter);
        binding.rlBottom.startAnimation(topAnim);
        binding.appbar.startAnimation(topAnim);
//        ll_top.startAnimation(topAnim);
        binding.rlBottom.setVisibility(View.VISIBLE);
//        ll_top.setVisibility(View.VISIBLE);
        binding.appbar.setVisibility(View.VISIBLE);
    }

    private void hideReadSetting() {
        isShow = false;
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_exit);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.dialog_top_exit);
        if (binding.rlBottom.getVisibility() == View.VISIBLE) {
            binding.rlBottom.startAnimation(topAnim);
        }
        if (binding.appbar.getVisibility() == View.VISIBLE) {
            binding.appbar.startAnimation(topAnim);
        }
//        ll_top.startAnimation(topAnim);
        binding.rlBottom.setVisibility(View.GONE);
//        ll_top.setVisibility(View.GONE);
        binding.appbar.setVisibility(View.GONE);
        hideSystemUI();
    }
}
