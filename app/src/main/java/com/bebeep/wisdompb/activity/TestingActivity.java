package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
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
import com.bebeep.wisdompb.bean.ExamEntity;
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

    private List<String> list = new ArrayList<>();

    private String id = "";
    private TabFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing);
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener, this);
        binding.title.tvTitle.setText("在线考试");
        id = getIntent().getStringExtra("id");


        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");

        initFrgament();

        getData();
    }


    @Override
    public void onClick(View v) {

    }


    /**
     * 初始化fragment
     */
    private void initFrgament() {
        for (int i=0;i<list.size(); i++) {
            Fragment fragment = new Fragment_Testing().newInstance("");
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
        OkHttpClientManager.postAsyn(URLS.EXAM_TESTING, new OkHttpClientManager.ResultCallback<String>() { //TestingEntity
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("考试题目信息 ："+response);
//                if(response.isSuccess()){
//
//                }else{
//                    MyTools.showToast(TestingActivity.this, response.getMsg());
//                    if(response.getErrorCode() == 1)refreshToken();
//                }
            }
        },header,map);
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.showLog("onKeyDown:"+keyCode);
        if(keyCode == KeyEvent.KEYCODE_BACK){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
