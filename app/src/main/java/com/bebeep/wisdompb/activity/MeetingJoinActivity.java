package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.showbigimage.ShowSingleBigImageDialog;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.RoundImage;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.bean.OrgActEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityMeetingJoinBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 参会签到
 */
public class MeetingJoinActivity extends BaseSlideActivity implements SwipeRefreshLayout.OnRefreshListener,OnPullListener{

    private ActivityMeetingJoinBinding binding;
    private List<UserInfo> list = new ArrayList<>();
    private CommonAdapter adapter;

    private String id;
    private int type = 0;

    private CommonTypeEntity entity;
    private ShowSingleBigImageDialog showSingleBigImageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_join);
        init();
    }

    private void init(){
        showSingleBigImageDialog = new ShowSingleBigImageDialog(MeetingJoinActivity.this);
        initAdapter();
        id = getIntent().getStringExtra("id");
        type = getIntent().getIntExtra("type",0);

        getQrcode();
        getSignList();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("签到情况");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.imgQrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity!=null && TextUtils.isEmpty(entity.getImgUrl())){
                    showSingleBigImageDialog.show(URLS.IMAGE_PRE+entity.getImgUrl(),R.drawable.default_error); //单张图片
                }
            }
        });
    }

    private void initAdapter(){
        adapter = new CommonAdapter<UserInfo>(this,R.layout.item_meeting_join,list){
            @Override
            protected void convert(ViewHolder holder, UserInfo entity, int position) {
                holder.setImageUrl((RoundImage)holder.getView(R.id.rimg_head),URLS.IMAGE_PRE + entity.getPhoto(),R.drawable.icon_head,60,60);
                holder.setText(R.id.tv_name, entity.getName());
                holder.setText(R.id.tv_time, entity.getSignTime());

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                binding.srl.setEnabled(topRowVerticalPosition >= 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }



    private void getQrcode(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("type",String.valueOf(type));
        OkHttpClientManager.postAsyn(URLS.MEETING_GET_QRCODE, new OkHttpClientManager.ResultCallback<BaseObject<CommonTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                MyTools.showToast(MeetingJoinActivity.this,"获取二维码失败");
            }
            @Override
            public void onResponse(BaseObject<CommonTypeEntity> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("获取二维码："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    PicassoUtil.setImageUrl(MeetingJoinActivity.this,binding.imgQrcode,URLS.IMAGE_PRE+entity.getImgUrl());
                }else{
                    MyTools.showToast(MeetingJoinActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }

    private void getSignList(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put(type==0?"meetingId":"activityId",id);
        OkHttpClientManager.postAsyn(type == 0?URLS.MEETING_SIGN_LIST:URLS.ACT_SIGN_LIST, new OkHttpClientManager.ResultCallback<BaseList<UserInfo>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
            }
            @Override
            public void onResponse(BaseList<UserInfo> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("签到情况："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MeetingJoinActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
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

            }
        },500);
    }
}
