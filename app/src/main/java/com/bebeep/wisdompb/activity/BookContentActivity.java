package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseActivity;
import com.bebeep.wisdompb.base.BaseAppCompatActivity;
import com.bebeep.wisdompb.databinding.ActivityBookContentBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.HashMap;


/**
 * 文章内容
 *
 */
public class BookContentActivity extends BaseActivity implements View.OnClickListener{
    private ActivityBookContentBinding binding;

    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_content);
        init();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        getData();
        binding.setVariable(BR.onClickListener,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_note:
                startActivity(new Intent(this,MyNoteActivity.class));
                break;
        }
    }



    private void getData() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("bookChaptersId", id);
        LogUtil.showLog("map:"+map.toString());
        OkHttpClientManager.postAsyn(URLS.LIBRARY_CATALOG_CONTENT, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
            }
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "章节详情 response: " + response);
//                if (response.isSuccess()) {
//
//                } else {
//                    MyTools.showToast(BookContentActivity.this,response.getMsg());
//                    if (response.getErrorCode() == 1)  refreshToken();
//                }
            }
        }, header, map);
    }
}
