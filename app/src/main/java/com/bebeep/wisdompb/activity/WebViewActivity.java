package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityWebviewBinding;


/**
 * 仅需要展示网页时跳转到此页面
 */
public class WebViewActivity extends SlideBackActivity {
    private ActivityWebviewBinding binding;
    private WebSettings settings;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);
        init();
    }

    private void init(){
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        String title = getIntent().getStringExtra("title");
        String url = getIntent().getStringExtra("url");
        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(url)){
            MyTools.showToast(this,"参数有误");
            finish();
        }
        binding.title.tvTitle.setText(title);
        initWeb(url);
    }


    private void initWeb(String url){
        settings = binding.webview.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存
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
}
