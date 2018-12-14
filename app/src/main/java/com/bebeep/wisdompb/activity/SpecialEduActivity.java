package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.TitleFragmentAdapter;
import com.bebeep.wisdompb.base.BaseActivity;
import com.bebeep.wisdompb.base.BaseFragmentActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.databinding.ActivitySpecialEducationBinding;
import com.bebeep.wisdompb.fragment.Fragment_LibraryList;
import com.bebeep.wisdompb.fragment.Fragment_SpecialEdu;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

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
