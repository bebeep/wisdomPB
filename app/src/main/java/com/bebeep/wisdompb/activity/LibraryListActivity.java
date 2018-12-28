package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.TitleFragmentAdapter;
import com.bebeep.wisdompb.base.BaseFragmentActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.LibraryTypeEntity;
import com.bebeep.wisdompb.databinding.ActivityLibraryListBinding;
import com.bebeep.wisdompb.fragment.Fragment_LibraryList;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 图书馆列表
 */
public class LibraryListActivity extends BaseFragmentActivity {
    private ActivityLibraryListBinding binding;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();
    private TitleFragmentAdapter adapter;

    private List<LibraryTypeEntity> list = new ArrayList<>();
    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_list);
        init();

    }

    private void init(){
        id = getIntent().getStringExtra("id");
        if(TextUtils.isEmpty(id)){
            MyTools.showToast(this,"内容缺失");
            finish();
        }
        getData();

        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText(getIntent().getStringExtra("title"));
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        setOnRefreshTokenListener(new OnRefreshTokenListener() {
            @Override
            public void onRefreshTokenSuccess() {
                getData();
            }
            @Override
            public void onRefreshTokenFail() {
                startActivityForResult(new Intent(LibraryListActivity.this, LoginActivity.class).putExtra("tag",1),88);
            }
        });
    }


    private void initFragment(){
        if(list == null || list.size() ==0) return;
        for (LibraryTypeEntity entity:list){
            fragmentList.add(new Fragment_LibraryList().newInstance(entity.getId()));
            listTitle.add(entity.getName());
        }
        adapter = new TitleFragmentAdapter(getSupportFragmentManager(), fragmentList, listTitle);
        binding.vpFindFragmentPager.setAdapter(adapter);
        binding.tabFindFragmentTitle.setupWithViewPager(binding.vpFindFragmentPager);
    }

    private void getData() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        map.put("parentId", id);
        OkHttpClientManager.postAsyn(URLS.LIBRARY_TYPE, new OkHttpClientManager.ResultCallback<BaseList<LibraryTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
            }
            @Override
            public void onResponse(BaseList<LibraryTypeEntity> response) {
                Log.e("TAG", "图书类型 response: " + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                    list = response.getData();
                    initFragment();
                } else {
                    if (response.getErrorCode() == 1) {
                        refreshToken();
                    }
                }
            }
        }, header, map);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==88 && resultCode ==RESULT_OK) getData();
    }
}

