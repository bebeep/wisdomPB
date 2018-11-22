package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityPayDetailsBinding;

/**
 * Created by Bebeep
 * Time 2018/11/22 20:15
 * Email 424468648@qq.com
 * Tips 缴费详情
 */

public class PayDetailsActivity extends SlideBackActivity implements View.OnClickListener{

    private ActivityPayDetailsBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_details);

        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("党费缴纳");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_pay://支付
                MyTools.showToast(this,"缴费成功");
                finish();
                break;
        }
    }
}
