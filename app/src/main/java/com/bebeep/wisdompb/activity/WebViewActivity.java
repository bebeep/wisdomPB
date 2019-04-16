package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseActivity;
import com.bebeep.wisdompb.databinding.ActivityWebviewBinding;


/**
 * 仅需要展示网页时跳转到此页面
 */
public class WebViewActivity extends BaseActivity {
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


        //让页面适应手机屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);


//        webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存
        binding.webview.loadUrl(url);
        binding.webview.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
//                return true;
                return super.shouldOverrideUrlLoading(view,url);
            }
        });
        binding.webview.setWebViewClient(new WebViewClient());


        binding.webview.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                // H5中包含下载链接的话让外部浏览器去处理
                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }


    private void hideTop(){
        try {
            //定义javaScript方法
            String javascript =
                     "javascript:function hideTop() { "
                    +     "document.getElementById('alertShow').style.display='none' "
                    + "}";

            //加载方法
            binding.webview.loadUrl(javascript);
            //执行方法
            binding.webview.loadUrl("javascript:hideTop();");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
