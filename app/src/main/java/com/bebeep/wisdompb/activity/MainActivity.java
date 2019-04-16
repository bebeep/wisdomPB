package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bebeep.commontools.file.FileUtil;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseAppCompatActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.bean.MeetingMinitesEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.bean.VersionEntity;
import com.bebeep.wisdompb.databinding.ActivityMain1Binding;
import com.bebeep.wisdompb.fragment.Fragment1;
import com.bebeep.wisdompb.fragment.Fragment2;
import com.bebeep.wisdompb.fragment.Fragment3;
import com.bebeep.wisdompb.fragment.Fragment4;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseAppCompatActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{
    private ActivityMain1Binding binding;
    private static MainActivity mainActivity;

    private FrameLayout[] fls;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentTransaction transaction = null;
    private FragmentManager fragmentManager = null;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;

    private CustomDialog versionDialog;
    private VersionEntity versionEntity = new VersionEntity();

    public static MainActivity getInstance(){
        return mainActivity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(fragments!=null && fragments.size()>0){
            initUserInfo();
            getNewsNum();
            getRemindDialog();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = this;
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main1);
        binding.rg.setOnCheckedChangeListener(this);
        init();
    }

    private void init(){
        getVersion();
        initUserInfo();
        initPopWindow();
        binding.setVariable(BR.onClickListener,this);
        initRadioButtonSize();
        initFrgament();
        getNewsNum();
        getRemindDialog();
//        initArrowIcons();


        long timestamp = System.currentTimeMillis();
        long deltaMillis = timestamp - PreferenceUtils.getPrefLong("timestamp",0);
        Log.e("TAG","deltaMillis:"+deltaMillis/1000/3600);
        if(deltaMillis >= 2 * 3600 * 1000){ //如果两次刷新token的间隔超过2个小时，就自动刷新
            refreshToken();
        }


        binding.drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {

            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {

            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });



    }

    private void showDialog(){
        versionDialog = new CustomDialog.Builder(this)
                .setMessage("新版本提示：\n"+versionEntity.getContents())
                .setSingleButton("立即更新", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        versionDialog.cancel();
                        Toast.makeText(MainActivity.this,"后台下载中...",Toast.LENGTH_SHORT).show();
                        downloadFile();
                    }
                })
                .setCancelEnable(false)
                .createSingleButtonDialog();
        versionDialog.show();
    }

    private void initUserInfo(){
        UserInfo info = MyApplication.getInstance().getUserInfo();
        PicassoUtil.setImageUrl(this, binding.rimgHead, URLS.IMAGE_PRE + info.getPhoto(),R.drawable.icon_head,60,60);
        binding.tvName.setText(info.getName());
        binding.ivSex.setImageResource(info.getSex() == 1?R.drawable.icon_sex_man:R.drawable.icon_sex_woman);
        binding.tvGroup.setText(info.getOffice());

    }


    @Override
    public void onClick(final View v) {
        if(v.getId() != R.id.fl_msg){
            binding.drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    switch (v.getId()){
                        case R.id.rimg_head://
                            startActivityForResult(new Intent(MainActivity.this, UserInfoActivity.class),88);
                            break;
                        case R.id.fl_menu1://我的考试-
                            startActivity(new Intent(MainActivity.this,MyExamActivity.class));
                            break;
                        case R.id.fl_menu2://我的书架-
                            startActivity(new Intent(MainActivity.this,MyBookrackActivity.class));
                            break;
                        case R.id.fl_menu3://我的笔记-
                            startActivity(new Intent(MainActivity.this,MyNoteListActivity.class));
                            break;
                        case R.id.fl_menu4://我的积分-
                            startActivity(new Intent(MainActivity.this,MyJifenActivity.class));
                            break;
                        case R.id.fl_menu5://我的会议-
                            startActivity(new Intent(MainActivity.this,MyMeetingActivity.class));
                            break;
                        case R.id.fl_menu6://我的活动-
                            startActivity(new Intent(MainActivity.this,MyActActivity.class));
                            break;
                        case R.id.fl_menu7://我的收藏-
                            startActivity(new Intent(MainActivity.this,MyCollectionActivity.class));
                            break;
                        case R.id.fl_menu8://我的评论-
                            startActivityForResult(new Intent(MainActivity.this,MyCommentActivity.class),99);
                            break;
                        case R.id.fl_menu9://我提起的-
                            startActivity(new Intent(MainActivity.this,MySubmitActivity.class));
                            break;
                        case R.id.fl_menu10://意见反馈-
                            startActivity(new Intent(MainActivity.this,TicklingActivity.class));
                            break;
                        case R.id.fl_menu11://设置
                            startActivity(new Intent(MainActivity.this, ConfigActivity.class));
                            break;
                        case R.id.fl_menu12://关于
                            startActivity(new Intent(MainActivity.this, WebViewActivity.class).putExtra("title","关于我们").putExtra("url",URLS.ABOUT));
                            break;
                        case R.id.fl_menu13://政治生日卡-
                            startActivity(new Intent(MainActivity.this, BirthdayCardActivity.class));
                            break;
                    }
                }
            },100);
        }else if(v.getId() == R.id.fl_msg){
            startActivityForResult(new Intent(MainActivity.this, NoticeActivity.class),77);
        }
    }

    public void showBottom(boolean show){
        binding.flBottom.setVisibility(show?View.VISIBLE:View.GONE);
        binding.vEmptySpace.setVisibility(show?View.VISIBLE:View.GONE);
    }

    public void showMenu(){
        if(binding.drawerLayout!=null)binding.drawerLayout.openDrawer(binding.flMenu);
    }

    public void addIgnoredView(View view){
//        binding.mDoubleSlideMenu.addIgnoredView(view);
    }

    public void removeIgnoredView(View view){
//        binding.mDoubleSlideMenu.reMoveIgnoredView(view);
    }

    private void initArrowIcons(){
        fls = new FrameLayout[]{binding.ivArrowRight1,binding.ivArrowRight2,binding.ivArrowRight3,binding.ivArrowRight4,
                binding.ivArrowRight5,binding.ivArrowRight6,binding.ivArrowRight7,binding.ivArrowRight8,binding.ivArrowRight9,
                binding.ivArrowRight10,binding.ivArrowRight11,binding.ivArrowRight12,binding.ivArrowRight13};
        int width = (int) (MyTools.getWidth(this) * 0.2);
        int dpSize = MyTools.dip2px(this,15);
        for (FrameLayout fl : fls){
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fl.getLayoutParams();
            params.setMargins(0,0,width + dpSize,0);
        }
    }


    /**
     * 手动切换显示的fragment
     * @param index
     */
    public void showFragment(int index){
        switchFragment(index);
        switch (index){
            case 0:
                binding.rb1.setChecked(true);
                break;
            case 1:
                binding.rb2.setChecked(true);
                break;
            case 2:
                binding.rb3.setChecked(true);
                break;
            case 3:
                binding.rb4.setChecked(true);
                break;
        }
    }


    /**
     * 初始化fragment
     */
    private void initFrgament() {
        fragments = new ArrayList<>();
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(fragment4);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fl_content, fragment1);
        transaction.add(R.id.fl_content, fragment2);
        transaction.add(R.id.fl_content, fragment3);
        transaction.add(R.id.fl_content, fragment4);
        transaction.show(fragment1).hide(fragment2).hide(fragment3).hide(fragment4);
        transaction.commitAllowingStateLoss();
    }


    /**
     * 更改Fragment对象
     */
    private void switchFragment(int index) {
        transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        for (int i = 0; i < fragments.size(); i++) {
            if (index == i) {
                transaction.show(fragments.get(index));
            } else {
                transaction.hide(fragments.get(i));
            }
        }
        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_1:
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                switchFragment(0);
                break;
            case R.id.rb_2:
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                switchFragment(1);
                break;
            case R.id.rb_3:
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                switchFragment(2);
                break;
            case R.id.rb_4:
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                switchFragment(3);
                break;
            default:
                binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                switchFragment(0);
                break;
        }
    }


    /**
     * 重新设置底部radiobutton图标的大小
     */

    private void initRadioButtonSize() {
        //定义底部标签图片大小和位置
        Drawable drawable_1 = getResources().getDrawable(R.drawable.bg_rb1);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_1.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        //设置图片在文字的哪个方向
        binding.rb1.setCompoundDrawables(null, drawable_1, null, null);

        Drawable drawable_2 = getResources().getDrawable(R.drawable.bg_rb2);
        drawable_2.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        binding.rb2.setCompoundDrawables(null, drawable_2, null, null);

        Drawable drawable_3 = getResources().getDrawable(R.drawable.bg_rb3);
        drawable_3.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        binding.rb3.setCompoundDrawables(null, drawable_3, null, null);

        Drawable drawable_4 = getResources().getDrawable(R.drawable.bg_rb4);
        drawable_4.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        binding.rb4.setCompoundDrawables(null, drawable_4, null, null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 99){
                binding.rb4.setChecked(true);
            }else if(requestCode == 88){
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }else if(requestCode == 77) {
                if(resultCode == 1){//三会一课
                    showFragment(2);
                }else if(resultCode == 2){//在线考试
                    showFragment(1);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(binding.flMenu)) binding.drawerLayout.closeDrawer(binding.flMenu);
        else finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.showLog("onKeyDown:"+keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(binding.drawerLayout.isDrawerOpen(binding.flMenu)) binding.drawerLayout.closeDrawer(binding.flMenu);
            else finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    private void getNewsNum(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.NEWS_NUM, new OkHttpClientManager.ResultCallback<BaseObject<CommonTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.flMsgNum.setVisibility(View.GONE);
            }
            @Override
            public void onResponse(BaseObject<CommonTypeEntity> response) {
                Log.e("TAG","获取消息数量 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    CommonTypeEntity entity = response.getData();
                    if(entity != null &&entity.getCount()!=0 ){
                        binding.flMsgNum.setVisibility(View.VISIBLE);
                        binding.tvTvMsgNum.setText(String.valueOf(entity.getCount()));
                    }else  binding.flMsgNum.setVisibility(View.GONE);
                }else  binding.flMsgNum.setVisibility(View.GONE);
            }
        },header,map);
    }


    private void getRemindDialog(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.HOST_DIALOG, new OkHttpClientManager.ResultCallback<BaseObject<CommonTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);

            }
            @Override
            public void onResponse(BaseObject<CommonTypeEntity> response) {
                Log.e("TAG","获取首页弹窗消息 json="+ MyApplication.gson.toJson(response));
                if(response!=null && response.isSuccess()){
                    CommonTypeEntity entity = response.getData();
                    if(!TextUtils.isEmpty(entity.getImgSrc())){
                        showPop(entity.getImgSrc(),entity.getContent());
                    }
                }
            }
        },header,map);
    }


    private void getVersion(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.VERSION, new OkHttpClientManager.ResultCallback<BaseObject<VersionEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<VersionEntity> response) {
                LogUtil.showLog("版本更新："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    versionEntity = response.getData();
                    if(versionEntity!=null){
                        boolean newVersion = versionEntity.getVersionsort() > MyTools.getVersionCode(MainActivity.this);
                        if(newVersion){
                            showDialog();
                        }
                    }
                }else{
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    private void downloadFile(){
        OkHttpClientManager.downloadAsyn(URLS.IMAGE_PRE + versionEntity.getUrl(), MyApplication.FILE_PATH, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                MyTools.showToast(MainActivity.this,"下载出错，请重试");
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("下载文件："+ MyApplication.gson.toJson(response));
                if(!TextUtils.isEmpty(response)){
                    File file = new File(response);
                    if(file.exists()){
                        FileUtil.openFile(MainActivity.this,file);
                    }else MyTools.showToast(MainActivity.this,"文件打开失败");
                }else  MyTools.showToast(MainActivity.this,"下载失败，请重试");
            }
        });
    }

    private PopupWindow popupWindow;
    private View popView;
    private ImageView iv_content,iv_cancel;
    private TextView tv_content;
    private void initPopWindow(){
        popView = View.inflate(this, R.layout.popwidnow_host_dialog,null);
        iv_content = popView.findViewById(R.id.iv_content);
        iv_cancel = popView.findViewById(R.id.iv_cancel);
        tv_content = popView.findViewById(R.id.tv_content);

        //获取屏幕宽高
        int weight = MyTools.dip2px(this,180);
        int height = MyTools.dip2px(this,180 + 45);
        popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setAnimationStyle(com.bebeep.commontools.R.style.popwin_anim_style);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1.0f;
                getWindow().setAttributes(lp);
            }
        });

        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private void showPop(String url,String content){
        if(popupWindow!=null){
            //popupWindow出现屏幕变为半透明
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.alpha = 0.5f;
            getWindow().setAttributes(lp);
            popupWindow.showAtLocation(popView, Gravity.CENTER,0,0);
            PicassoUtil.setImageUrl(this,iv_content,URLS.IMAGE_PRE + url,R.drawable.default_error,180,180);
            tv_content.setText(content);
        }

    }


}
