package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.LinearLayout;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.TitleFragmentAdapter;
import com.bebeep.wisdompb.base.BaseFragmentActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.databinding.ActivitySpecialEducationBinding;
import com.bebeep.wisdompb.fragment.Fragment_SpecialEdu;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 专题教育
 */
public class SpecialEduActivity extends BaseFragmentActivity {
    private ActivitySpecialEducationBinding binding;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();
    private TitleFragmentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_special_education);
        init();
    }

    private void init(){
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("专题教育");
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getData();

        setOnRefreshTokenListener(new OnRefreshTokenListener() {
            @Override
            public void onRefreshTokenSuccess() {
                getData();
            }
            @Override
            public void onRefreshTokenFail() {
                startActivityForResult(new Intent(SpecialEduActivity.this,LoginActivity.class).putExtra("tag",1), 88);
            }
        });


        binding.title.ivTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SpecialEduActivity.this, SearchActivity.class).putExtra("type",6));
            }
        });
    }



    private void setTabWidth(){
        binding.tabFindFragmentTitle.setTabMode(fragmentList.size()>3?TabLayout.MODE_SCROLLABLE:TabLayout.MODE_FIXED);
        binding.tabFindFragmentTitle.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    Field mTabStripField = binding.tabFindFragmentTitle.getClass().getDeclaredField("mTabStrip");
                    mTabStripField.setAccessible(true);
                    LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(binding.tabFindFragmentTitle);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        View tabView = mTabStrip.getChildAt(i);
                        //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.width = (MyTools.getWidth(SpecialEduActivity.this) - MyTools.dip2px(SpecialEduActivity.this,30))/3;
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initFragment(List<CommonTypeEntity> typeList){
        if(typeList == null || typeList.size() ==0)return;
        for (CommonTypeEntity entity: typeList){
            fragmentList.add(new Fragment_SpecialEdu().newInstance(entity.getId()));
            listTitle.add(entity.getTitle());
        }
        adapter = new TitleFragmentAdapter(getSupportFragmentManager(), fragmentList, listTitle);
        binding.vpFindFragmentPager.setAdapter(adapter);
        binding.tabFindFragmentTitle.setupWithViewPager(binding.vpFindFragmentPager);
        setTabWidth();
    }


    private void getData(){
        HashMap header = new HashMap(), map = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.SPECIAL_EDU_TYPE, new OkHttpClientManager.ResultCallback<BaseList<CommonTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<CommonTypeEntity> response) {
                LogUtil.showLog("专题教育-类型："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    initFragment(response.getData());
                }else {

                    refreshToken();
                }
            }
        },header,map);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 88 && resultCode == RESULT_OK) getData();
    }
}
