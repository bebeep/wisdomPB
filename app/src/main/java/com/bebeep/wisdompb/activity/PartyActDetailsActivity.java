package com.bebeep.wisdompb.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityPartyActDetailsBinding;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 党组织活动详情
 */
public class PartyActDetailsActivity extends SlideBackActivity implements View.OnClickListener,
        OnPullListener,SwipeRefreshLayout.OnRefreshListener,TextWatcher {
    private ActivityPartyActDetailsBinding binding;
    private WebSettings settings;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;
    private String userNames = "吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，" +
            "吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，" +
            "吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，" +
            "吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，" +
            "吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖，吴彦祖";

    private boolean open = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_party_act_details);
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener,this);
        initAdapter();
        initWeb("http://www.xinhuanet.com/2018-11/20/c_129998550.htm");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("党组织活动");

        binding.etComment.addTextChangedListener(this);
        binding.etComment.setOnFocusChangeListener(onFocusChangeListener);

        binding.tvUserNames.setText(userNames);
    }

    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(this,R.layout.item_partyact_details,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_join://我要参与

                break;
            case R.id.ll_user_open://展开/关闭
                open = !open;
                setUserNames(open);
                break;
            case R.id.tv_send://发送评论
                binding.etComment.setText("");
                binding.etComment.clearFocus();
                binding.tvSend.setClickable(false);
                break;
        }
    }


    private void setUserNames(boolean open){
        if(open){
            binding.ivUserOpen.setRotation(180);
            binding.tvUserNames.setMaxLines(Integer.MAX_VALUE);
        }else {
            binding.ivUserOpen.setRotation(0);
            binding.tvUserNames.setMaxLines(2);
        }
    }



    //加载webview
    private void initWeb(String url) {
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

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.srl.setRefreshing(false);
            }
        },500);
    }

    @Override
    public void onRefresh(AbsRefreshLayout listLoader) {

    }
    @Override
    public void onLoading(AbsRefreshLayout listLoader) {
        binding.nrl.postDelayed(new Runnable() {
            @Override
            public void run() {
                list.add("");
                list.add("");
                adapter.refresh(list);
                binding.nrl.onLoadFinished();
            }
        },500);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        binding.tvSend.setBackgroundResource(!TextUtils.isEmpty(s.toString())?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
        binding.tvSend.setClickable(!TextUtils.isEmpty(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.e("TAG","hasFocus:"+hasFocus);
            boolean hasContent = !TextUtils.isEmpty(binding.etComment.getText().toString());
            binding.tvSend.setClickable(hasContent);
            binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
        }
    };




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件

                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }
}
