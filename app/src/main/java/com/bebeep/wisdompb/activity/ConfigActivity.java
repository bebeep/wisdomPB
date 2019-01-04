package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bebeep.commontools.file.FileUtil;
import com.bebeep.commontools.utils.DataCleanManager;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.bean.VersionEntity;
import com.bebeep.wisdompb.databinding.ActivityConfigBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.io.File;
import java.util.HashMap;

/**
 * 设置
 */
public class ConfigActivity extends BaseSlideActivity implements View.OnClickListener{
    private ActivityConfigBinding binding;


    private VersionEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_config);
        init();
    }


    private void init(){
        getVersion();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("设置");
        setCacheData();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_edit_psw://修改密码

                break;
            case R.id.fl_notice://免责声明
                startActivity(new Intent(this, WebViewActivity.class).putExtra("title","免责声明").putExtra("url", URLS.NOTICE));
                break;
            case R.id.fl_clear_cache://清除缓存
                DataCleanManager.clearAllCache(this);
                MyTools.showToast(this,"缓存已清空");
                setCacheData();
                break;
            case R.id.fl_update://版本更新
                if(entity!=null){
                    boolean newVersion = entity.getVersionsort() > MyTools.getVersionCode(ConfigActivity.this);
                    binding.tvVersion.setText(newVersion?"立即更新":"已是最新版本");
                    if(newVersion){
                        downloadFile();
                        MyTools.showToast(this,"开始下载");
                    }else MyTools.showToast(this,"已是最新版本");
                }else MyTools.showToast(this,"已是最新版本");
                break;
        }
    }


    private void setCacheData(){
        try {
            binding.tvCache.setText(DataCleanManager.getTotalCacheSize(this) + "");
        } catch (Exception e) {
            binding.tvCache.setText("0 M");
        }
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
                    entity = response.getData();
                    if(entity!=null){
                        boolean newVersion = entity.getVersionsort() > MyTools.getVersionCode(ConfigActivity.this);
                        binding.tvVersion.setText(newVersion?"立即更新":"已是最新版本");
                    }
                }else{
                    MyTools.showToast(ConfigActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }

    private void downloadFile(){
        OkHttpClientManager.downloadAsyn(URLS.HOST + entity.getUrl(), MyApplication.FILE_PATH, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                MyTools.showToast(ConfigActivity.this,"下载出错，请重试");
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("下载文件："+ MyApplication.gson.toJson(response));
                if(!TextUtils.isEmpty(response)){
                    File file = new File(response);
                    if(file.exists()){
                        FileUtil.openFile(ConfigActivity.this,file);
                    }else MyTools.showToast(ConfigActivity.this,"文件打开失败");
                }else  MyTools.showToast(ConfigActivity.this,"下载失败，请重试");
            }
        });
    }

}
