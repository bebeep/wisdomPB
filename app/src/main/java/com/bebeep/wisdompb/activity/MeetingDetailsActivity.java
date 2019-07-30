package com.bebeep.wisdompb.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acker.simplezxing.activity.CaptureActivity;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.MeetingEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityMeetingDetailsBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 三会一课详情
 */
public class MeetingDetailsActivity extends BaseSlideActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityMeetingDetailsBinding binding;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private List<UserInfo> list = new ArrayList<>();
    private CommonAdapter adapter;


    private String id = "";
    private String str1 = "议题 ";
    private String str2 = "参会要求 ";
    private CustomProgressDialog customProgressDialog;
    private UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_details);
        init();
    }

    private void init(){
        customProgressDialog = new CustomProgressDialog(this);
        userInfo = MyApplication.getInstance().getUserInfo();
        initAdapter();
        id = getIntent().getStringExtra("id");
        getData();
        binding.msv.smoothScrollTo(0,0);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("会议详情");
        binding.srl.setOnRefreshListener(this);

    }

    private void initUI(MeetingEntity entity){
        if(entity == null) return;
        binding.tvTitle.setText(entity.getTheme());
        PicassoUtil.setImageUrl(this,binding.ivHead, URLS.IMAGE_PRE + MyApplication.getInstance().getUserInfo().getPhoto(),R.drawable.icon_head,40,40);
        binding.tvName.setText(MyApplication.getInstance().getUserInfo().getName());

        if(!TextUtils.isEmpty(entity.getStartTime())&& entity.getStartTime().length()>=16
                && !TextUtils.isEmpty(entity.getEndTime())&& entity.getEndTime().length()>=16){
            String startDay = entity.getStartTime().substring(0,10);
            String endDay = entity.getEndTime().substring(0,10);

            String startTime = entity.getStartTime().substring(11,16);
            String endTime = entity.getEndTime().substring(11,16);
            if(TextUtils.equals(startDay,endDay)){
                binding.tvTime.setText(startDay + " "+entity.getWeek()+" "+startTime+"~"+endTime);
            }else binding.tvTime.setText(entity.getStartTime() +"~"+ entity.getEndTime());
        }else binding.tvTime.setText(entity.getStartTime() +"~"+ entity.getEndTime());
        binding.tvRoom.setText(entity.getAddress());
        splitTextColor(binding.tvMeetingTitle,str1 + entity.getIssue(),2);
        splitTextColor(binding.tvMeetingRequest,str2 + entity.getRequirement(),4);
        binding.tvJoinedPerson.setText("已有"+entity.getParticipateNum()+"人参加");
        binding.tvLeavedPerson.setText(entity.getLeaveNum()+"人请假");
        list = entity.getMeetingInFoUserBizList();
        adapter.refresh(list);
        String state = entity.getState();

        if(TextUtils.equals(state, "1")){ //已请假
            binding.tvLeave.setText("已请假");
            binding.tvLeave.setTextColor(getResources().getColor(R.color.c_gray));
            binding.tvLeave.setClickable(false);
        }

        if(entity.getMettingSignJurisdictionType() == 1){
            binding.tvJoin.setText("统计会议签到");
        }else{
            if(TextUtils.equals(state, "0")){
                binding.tvJoin.setText("已参加");
                binding.tvJoin.setTextColor(getResources().getColor(R.color.c_gray));
                binding.tvJoin.setClickable(false);
            }
        }

    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("id",id);
        OkHttpClientManager.postAsyn(URLS.MEETING_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<MeetingEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<MeetingEntity> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("会议详情："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    MeetingEntity entity = response.getData();
                    initUI(entity);
                }else{
                    MyTools.showToast(MeetingDetailsActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }

            }
        },header,map);
    }




    private void join(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("meetingId","");
        OkHttpClientManager.postAsyn(URLS.MEETING_JOIN, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                customProgressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                customProgressDialog.cancel();
                LogUtil.showLog("参加："+ MyApplication.gson.toJson(response));
                MyTools.showToast(MeetingDetailsActivity.this, response.getMsg());
                if(response.isSuccess()){
                    getData();
                }else{
                    if(response.getErrorCode() == 1)refreshToken();
                }

            }
        },header,map);
    }


    /**
     * 签到
     */
    private void sign(String qrId){
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("qrId",qrId);
        map.put("type","0");
        LogUtil.showLog("会议签到："+map.toString());
        OkHttpClientManager.postAsyn(URLS.ACT_SIGN, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("签到 response："+ MyApplication.gson.toJson(response));
                MyTools.showToast(MeetingDetailsActivity.this,response.getMsg());
                if(response.isSuccess()){
                    getData();
                }else MyTools.showToast(MeetingDetailsActivity.this, response.getMsg());
            }
        },header,map);
    }


    @SuppressLint("NewApi")
    private void initAdapter(){
        adapter = new CommonAdapter<UserInfo>(this, R.layout.item_meeting_details,list){
            @Override
            protected void convert(ViewHolder holder, UserInfo entity, int position) {
                holder.setImageUrl((ImageView) holder.getView(R.id.img_head),URLS.IMAGE_PRE+entity.getPhoto(),R.drawable.icon_head,60,60);
                holder.setText(R.id.tv_name,entity.getName());
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        binding.recyclerView.setAdapter(adapter);



        binding.msv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                binding.srl.setEnabled(scrollY == 0);
            }
        });
    }



    private void splitTextColor(TextView tv, String content, int splitIndex){
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#db3a32")), 0, splitIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#585756")), splitIndex,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_leave://请假
                startActivityForResult(new Intent(this, MeetingLeaveActivity.class).putExtra("id",id),88);
                break;
            case R.id.tv_join://参加
                if(TextUtils.equals(binding.tvJoin.getText().toString(),"参加")) {
                    LogUtil.showLog("参会");
                    if (ContextCompat.checkSelfPermission(MeetingDetailsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MeetingDetailsActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                    } else  startCaptureActivityForResult();
                }else {
                    startActivity(new Intent(MeetingDetailsActivity.this, MeetingJoinActivity.class).putExtra("id",id).putExtra("type",0));
                }
                break;
        }
    }


    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
               getData();
            }
        },400);
    }


    private void startCaptureActivityForResult() {
        Intent intent = new Intent(MeetingDetailsActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    /**
     * 请求权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    MyTools.showToast(MeetingDetailsActivity.this,"请在设置中对本应用授权");
                }
            }
            break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 88 && resultCode == RESULT_OK){
            getData();
        }else if(requestCode == CaptureActivity.REQ_CODE){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String key = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                    LogUtil.showLog("扫描得到的活动："+key);
                    HashMap map = MyApplication.gson.fromJson(key,HashMap.class);
                    if(map == null) {
                        MyTools.showToast(MeetingDetailsActivity.this,"无效的二维码");
                        return;
                    }

                    if(TextUtils.isEmpty(String.valueOf(map.get("content")))) {
                        MyTools.showToast(MeetingDetailsActivity.this,"无效的二维码");
                        return;
                    }
                    sign(String.valueOf(map.get("content")));
                    break;
                case Activity.RESULT_CANCELED:
                    if (data != null) {
                        MyTools.showToast(MeetingDetailsActivity.this,"请在设置中对本应用授权");
                    }
                    break;
            }
        }
    }
}
