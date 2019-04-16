package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.PayEntity;
import com.bebeep.wisdompb.bean.PayResult;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityPayDetailsBinding;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bebeep
 * Time 2018/11/22 20:15
 * Email 424468648@qq.com
 * Tips 缴费详情
 */

public class PayDetailsActivity extends BaseSlideActivity implements View.OnClickListener{

    private ActivityPayDetailsBinding binding;
    private PayEntity entity;

    private UserInfo info;
    private String orderInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_details);
        info = MyApplication.getInstance().getUserInfo();
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvRight.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("党费缴纳");
        binding.title.tvRight.setText("缴费记录");
        getData();
    }


    private void initUI(){
        if(TextUtils.equals(entity.getState(),"0")){
            binding.tvPay.setClickable(true);
            binding.tvPay.setBackgroundResource(R.drawable.bg_btn_join);
            binding.tvPay.setText("支付宝支付");
        }else if(TextUtils.equals(entity.getState(),"1")){
            binding.tvPay.setClickable(false);
            binding.tvPay.setBackgroundResource(R.drawable.bg_rec_5dp_unclickable);
            binding.tvPay.setText("本月已缴费");
        }

        binding.tvName.setText(info.getName());
        binding.tvOrgName.setText(info.getOffice());
        binding.tvType.setText(entity.getTypeName());
        binding.tvItem.setText(entity.getProjectNmae());
        binding.tvMonth.setText(entity.getYearMonth());
        binding.tvRemind.setText(entity.getContent());
        binding.tvEndTime.setText(entity.getEndTime());
        binding.tvMoney.setText("￥"+entity.getMoney());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                startActivity(new Intent(PayDetailsActivity.this,ChargeActivity.class));
                break;
            case R.id.tv_pay://支付
                getSign();
                break;
        }
    }

    /**
     * 获取缴费信息
     */
    private void getData(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.PARTY_PAY_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<PayEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<PayEntity> response) {
                Log.e("TAG","getData json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    if(entity!=null)initUI();
                }else{
                    MyTools.showToast(PayDetailsActivity.this, response.getMsg());
                }
            }
        },header,map);
    }


    /**
     * 获取支付签名
     */
    private void getSign(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.PARTY_PAY_SIGN, new OkHttpClientManager.ResultCallback<BaseObject<PayEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<PayEntity> response) {
                Log.e("TAG","getSign json="+ response);
                if(response.isSuccess()){
                    orderInfo = response.getData().getSign();
                    Thread payThread = new Thread(runnable);
                    payThread.start();
                }else{
                    MyTools.showToast(PayDetailsActivity.this, response.getMsg());
                }
            }
        },header,map);
    }



    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            PayTask payTask = new PayTask(PayDetailsActivity.this);
            HashMap<String,String> result = (HashMap<String, String>) payTask.payV2(orderInfo,true);
            Message message = new Message();
            message.what = 99;
            message.obj = result;
            handler.sendMessage(message);
        }
    };


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PayResult result = new PayResult((Map<String, String>) msg.obj);
            Log.e("TAG","payResult json="+ MyApplication.gson.toJson(result));
            String resultInfo = result.getResult();// 同步返回需要验证的信息
            String resultStatus = result.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                MyTools.showToast(PayDetailsActivity.this,"支付成功");
                getData();
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                MyTools.showToast(PayDetailsActivity.this,"支付失败");
            }

        }
    };

}
