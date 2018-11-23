package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bebeep.commontools.utils.DataCleanManager;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityConfigBinding;

/**
 * 设置
 */
public class ConfigActivity extends SlideBackActivity implements View.OnClickListener{
    private ActivityConfigBinding binding;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_config);
        init();
    }


    private void init(){
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
                startActivity(new Intent(this,WebViewActivity.class)
                        .putExtra("title","免责声明")
                        .putExtra("url","http://www.xinhuanet.com/2018-11/20/c_129998550.htm"));
                break;
            case R.id.fl_clear_cache://清除缓存
                DataCleanManager.clearAllCache(this);
                MyTools.showToast(this,"缓存已清空");
                setCacheData();
                break;
            case R.id.fl_update://版本更新
                MyTools.showToast(this,"已是最新版本");
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
}
