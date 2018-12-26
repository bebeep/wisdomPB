package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.TabFragmentPagerAdapter;
import com.bebeep.wisdompb.base.BaseFragmentActivity;
import com.bebeep.wisdompb.bean.AnswerEntity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.bean.TestingEntity;
import com.bebeep.wisdompb.bean.TestingItemEntity;
import com.bebeep.wisdompb.databinding.ActivityTestingBinding;
import com.bebeep.wisdompb.fragment.Fragment1;
import com.bebeep.wisdompb.fragment.Fragment2;
import com.bebeep.wisdompb.fragment.Fragment3;
import com.bebeep.wisdompb.fragment.Fragment4;
import com.bebeep.wisdompb.fragment.Fragment_Testing;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 考试进行中
 */
public class TestingActivity extends BaseFragmentActivity implements View.OnClickListener{
    private ActivityTestingBinding binding;
    private List<Fragment> fragments = new ArrayList<>();

    private List<TestingItemEntity> list = new ArrayList<>();

    private String id = "";
    private TabFragmentPagerAdapter adapter;
    private TestingEntity entity;


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    setTime(msg);
                    break;
                case 2:
                    MyTools.showToast(TestingActivity.this,"考试时间到");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing);
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener, this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("在线考试");
        id = getIntent().getStringExtra("id");
        getData();
    }


    private void initUI(){
        Message msg = new Message();
        setTime(msg);
        binding.tvTopNum.setText("1/"+entity.getBizList().size());
        binding.tvNum.setText("1/"+entity.getBizList().size());
        list = entity.getBizList();
        initFrgament();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.iv_back:
                finish();
                break;
        }
    }


    public int getIndex(){
        return binding.viewpager.getCurrentItem();
    }

    public String getId(){
        return id;
    }

    public TestingEntity getTestingEntity(){
        return entity;
    }

    public void nextItem(){
        int currentIndex = binding.viewpager.getCurrentItem();
        if(currentIndex==fragments.size())return;
        binding.viewpager.setCurrentItem(currentIndex+1);
    }

    public void setResult(boolean right){
        int position = binding.viewpager.getCurrentItem();
        TestingItemEntity entity = list.get(position);
        entity.setRight(right);
        LogUtil.showLog("entity:"+MyApplication.gson.toJson(entity));
        list.set(position,entity);
        setNum();
        binding.viewpager.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextItem();
            }
        },300);

        //

    }

    public void refreshData(TestingItemEntity entity){
        int position = binding.viewpager.getCurrentItem();
        list.set(position,entity);

    }


    private void setNum(){
        int rightNum = 0;
        int wrongNum = 0;
        for (TestingItemEntity testingItemEntity:list){
            if(testingItemEntity.isHasChecked()){
                if(testingItemEntity.isRight()) rightNum ++;
                else wrongNum ++;
            }
        }
        binding.tvRight.setText(String.valueOf(rightNum));
        binding.tvWrong.setText(String.valueOf(wrongNum));
    }

    /**
     * 初始化fragment
     */
    private void initFrgament() {
        if (list == null)return;
        for (int i=0;i<list.size(); i++) {
            TestingItemEntity entity = list.get(i);
            Fragment fragment = new Fragment_Testing().newInstance(entity);
            fragments.add(fragment);
        }
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setCurrentItem(0);
        binding.viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                binding.tvTopNum.setText((i+1)+"/"+entity.getBizList().size());
                binding.tvNum.setText((i+1)+"/"+entity.getBizList().size());
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }





    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("answersheetId",id);
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.EXAM_TESTING, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("开始考试："+response);
//                if(response.isSuccess()){
//                    entity =  response.getData();
//                    if(entity == null) {
//                        MyTools.showToast(TestingActivity.this,"题目信息有误");
//                        finish();
//                    }
//                    initUI();
//                }else{
//                    MyTools.showToast(TestingActivity.this, response.getMsg());
//                    if(response.getErrorCode() == 1)refreshToken();
//                }
            }
        },header,map);
    }


    private void setTime(Message msg){
        if(entity == null) return;
        long endTime = entity.getEndTime();
        long currentTime = System.currentTimeMillis();
        String time ;
        long deltaTime = (endTime - currentTime)/1000; //秒
        int day = (int) (deltaTime / (3600 * 24));
        int hour = (int) ((deltaTime % (3600 * 24)) / 3600);
        int min = (int) (deltaTime%3600/ 60);
        int mill = (int) (deltaTime % 60);
        if(hour == 0 && min == 0 && mill ==0){//考试结束
            msg.what = 2;
            binding.tvTime.setText("考试结束");
        }else{
            msg.what = 1;
            time = (day<10?"0"+day:String.valueOf(day)) + "天" +(hour<10?"0"+hour:String.valueOf(hour)) + "小时"+ (min<10?"0"+min:String.valueOf(min)) + "分" +(mill<10?"0"+mill:String.valueOf(mill))+"秒";
            binding.tvTime.setText(time);
        }
        handler.sendEmptyMessageDelayed(msg.what,1000);
    }

    @Override
    public void onBackPressed() {//返回

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.showLog("onKeyDown:"+keyCode);
//        if(keyCode == KeyEvent.KEYCODE_BACK){//返回
//
//            return false;
//        }
        return super.onKeyDown(keyCode, event);
    }
}
