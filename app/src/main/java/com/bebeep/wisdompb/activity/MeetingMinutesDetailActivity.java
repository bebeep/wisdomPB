package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.listener.SoftKeyBoardListener;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.NewsEntity;
import com.bebeep.wisdompb.databinding.ActivityNewsDetailBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MeetingMinutesDetailActivity extends BaseEditActivity implements View.OnClickListener{

    private ActivityNewsDetailBinding binding;
    private WebSettings settings;
    private String id;


    private NewsEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_detail);
        init();
    }

    private void init(){
        String title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        getData();
        binding.setOnClickListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText(title);

        binding.ivZan.setVisibility(View.GONE);
        binding.ivCollect.setVisibility(View.GONE);
        binding.llZan.setVisibility(View.GONE);
        binding.flComment.setVisibility(View.GONE);
    }




    private void initUI(){
        if(entity == null) return;
        initWeb(URLS.HOST+entity.getInfoUrl());
        if(TextUtils.isEmpty(entity.getEnclosureNmae()) || TextUtils.isEmpty(entity.getEnclosureUrl())) binding.flFile.setVisibility(View.GONE);
        else{
            if(entity.getEnclosureNmae().endsWith(".doc")||entity.getEnclosureNmae().endsWith(".docx")){
                binding.imgFileType.setImageResource(R.drawable.icon_doc);
            }else if(entity.getEnclosureNmae().endsWith(".pdf")){
                binding.imgFileType.setImageResource(R.drawable.icon_file_pdf);
            }else if(entity.getEnclosureNmae().endsWith(".xls")||entity.getEnclosureNmae().endsWith(".xlsx")){
                binding.imgFileType.setImageResource(R.drawable.icon_file_excel);
            }
            binding.tvFileName.setText(entity.getEnclosureNmae());
            binding.tvFileSize.setText(entity.getFilesSize());
        }


    }


    //加载webview
    private void initWeb(String url) {
        settings = binding.webview.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webview.loadUrl(url);
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });
        binding.webview.setWebViewClient(new WebViewClient());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_file://下载文件
                downloadFile();
                break;
        }
    }




    private void getData(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("id", id);
        OkHttpClientManager.postAsyn(URLS.MEETING_MINUTES_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<NewsEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<NewsEntity> response) {
                Log.e("TAG","获取会议纪要详情 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    initUI();
                }else{
                    MyTools.showToast(MeetingMinutesDetailActivity.this, response.getMsg());
                }
            }
        },header,map);
    }




    private void downloadFile(){
        OkHttpClientManager.downloadAsyn(URLS.IMAGE_PRE + entity.getEnclosureUrl(), MyApplication.FILE_PATH, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                MyTools.showToast(MeetingMinutesDetailActivity.this,"下载出错，请重试");
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("下载文件："+ MyApplication.gson.toJson(response));
                if(!TextUtils.isEmpty(response)){
                    MyTools.showToast(MeetingMinutesDetailActivity.this,"下载文件成功，已保存到"+MyApplication.FILE_PATH+"文件夹下");
                }else  MyTools.showToast(MeetingMinutesDetailActivity.this,"下载失败，请重试");
            }
        });
    }
}
