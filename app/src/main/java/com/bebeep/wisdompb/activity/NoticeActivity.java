package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import com.android.databinding.library.baseAdapters.BR;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.NoticeEntity;
import com.bebeep.wisdompb.bean.OrgActEntity;
import com.bebeep.wisdompb.databinding.ActivityNoticeBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 通知公告
 */
public class NoticeActivity extends BaseEditActivity implements View.OnClickListener,OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityNoticeBinding binding;
    private List<NoticeEntity> list =new ArrayList<>();
    private CommonAdapter adapter;

    private int pageNo =1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_notice);
        init();
    }

    private void init(){
        initAdapter();
        getData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("通知公告");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_menu://左上角的按钮

                break;
        }
    }


    private void initAdapter(){
        adapter = new CommonAdapter<NoticeEntity>(this,R.layout.item_notice,list){
            @Override
            protected void convert(ViewHolder holder, final NoticeEntity entity, int position) {
                holder.setText(R.id.tv_title, entity.getTypeTitle());
                holder.setText(R.id.time,entity.getCreateDate());
                holder.setVisible(R.id.iv_link, !TextUtils.isEmpty(entity.getUrl())&&entity.getType() == 0);
                holder.setOnClickListener(R.id.fl_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (entity.getType()){
                            case 0: //后台发布的
                                if(!TextUtils.isEmpty(entity.getUrl())){
                                    startActivity(new Intent(NoticeActivity.this, WebViewActivity.class).putExtra("title",entity.getTypeTitle()).putExtra("url",entity.getUrl()));
                                }
                                break;
                            case 2://党费提醒

                                break;
                            case 3://会议
                                setResult(1);
                                finish();
                                break;
                            case 4://活动
                                startActivity(new Intent(NoticeActivity.this, MyActActivity.class));
                                break;
                            case 5://政治生日卡
                                startActivity(new Intent(NoticeActivity.this, BirthdayCardActivity.class));
                                break;
                            case 6://意见反馈
                                startActivity(new Intent(NoticeActivity.this, MySubmitActivity.class));
                                break;
                            case 7://考试通知
                                setResult(2);
                                finish();
                                break;
                        }
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }



    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize",MyApplication.pageSize);
        OkHttpClientManager.postAsyn(URLS.MY_NOTICE, new OkHttpClientManager.ResultCallback<BaseList<NoticeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<NoticeEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                LogUtil.showLog("通知公告："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo==1) list = response.getData();
                    else {
                        if(response.getData()==null || response.getData().size() ==0) MyTools.showToast(NoticeActivity.this,"没有更多内容了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(NoticeActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo = 1;
                getData();
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
                pageNo ++ ;
                getData();
            }
        },500);
    }
}
