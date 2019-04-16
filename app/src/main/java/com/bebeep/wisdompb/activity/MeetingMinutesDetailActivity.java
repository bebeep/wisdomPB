package com.bebeep.wisdompb.activity;

import android.app.Application;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.bebeep.commontools.showbigimage.ShowMultiBigImageDialog;
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
import com.bebeep.wisdompb.bean.MeetingMinitesEntity;
import com.bebeep.wisdompb.bean.NewsEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityMeetingMinitesDetailsBinding;
import com.bebeep.wisdompb.databinding.ActivityNewsDetailBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MeetingMinutesDetailActivity extends BaseEditActivity implements View.OnClickListener{

    private ActivityMeetingMinitesDetailsBinding binding;
    private String id,title;
    private MeetingMinitesEntity entity;
    private boolean open = false;
    private CommonAdapter adapter;
    private List<String> imgList = new ArrayList<>();
    private ShowMultiBigImageDialog showMultiBigImageDialog;


    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) getData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_minites_details);
        init();
    }

    private void init(){
        title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        UserInfo userInfo = MyApplication.getInstance().getUserInfo();

        binding.title.ivTitleRight.setImageResource(R.drawable.icon_f3_edit);
        binding.title.ivTitleRight.setVisibility(userInfo.getMinutesMeetingType() == 1?View.VISIBLE:View.GONE);


        initAdapter();
        getData();
        binding.setOnClickListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText(title);

    }




    private void initUI(){
        if(entity == null) return;
        binding.tvTitle.setText(title);
        binding.tvTime.setText(entity.getName()+"  "+entity.getUpdateDate());
        if(!TextUtils.isEmpty(entity.getRemarks())) {
            binding.llRemark.setVisibility(View.VISIBLE);
            binding.tvRemark.setText(entity.getRemarks());
        }
        binding.tvContent.setText(entity.getContents());

        List<UserInfo> userList = entity.getUserList();
        if(userList!=null && userList.size()>0){
            binding.flRead.setVisibility(View.VISIBLE);
            binding.tvReadNum.setText("已读成员（"+userList.size()+"）");
            String names = "";
            for (UserInfo userInfo:userList){
                if(!TextUtils.isEmpty(userInfo.getName())) names+= userInfo.getName()+",";
            }
            if(!TextUtils.isEmpty(names) && names.length()>0) names = names.substring(0,names.length()-1);
            binding.tvUserNames.setText(names);
        }
        String img = entity.getImgsrcs();
        String[] imgs = img.split(",");
        if(imgs!=null && imgs.length>0){
            imgList.clear();
            for (String s : imgs)imgList.add(URLS.IMAGE_PRE + s);
            showMultiBigImageDialog = new ShowMultiBigImageDialog(MeetingMinutesDetailActivity.this,imgList);
            adapter.refresh(imgList);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_title_right:
                startActivity(new Intent(MeetingMinutesDetailActivity.this,ReleaseMeetingMinitesActivity.class).putExtra("id",entity.getId()));
                break;
            case R.id.ll_user_open:
                open = !open;
                setUserNames(open);
                break;
        }
    }


    //图
    private void initAdapter(){
        adapter = new CommonAdapter<String>(this, R.layout.item_f4_inner1,imgList){
            @Override
            protected void convert(ViewHolder holder, final String s, final int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.iv), s,R.drawable.default_error,90,60);
                holder.setOnClickListener(R.id.iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //查看大图
                        showMultiBigImageDialog.show(position);
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        binding.recyclerView.setAdapter(adapter);
    }



    private void getData(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("id", id);
        Log.e("TAG","获取会议纪要详情 map="+ map.toString());
        OkHttpClientManager.postAsyn(URLS.MEETING_MINUTES_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<MeetingMinitesEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<MeetingMinitesEntity> response) {
                Log.e("TAG","获取会议纪要详情 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    if(entity == null || TextUtils.isEmpty(entity.getId())){
                        finish();
                        return;
                    }
                    initUI();
                }else{
                    MyTools.showToast(MeetingMinutesDetailActivity.this, response.getMsg());
                }
            }
        },header,map);
    }




}
