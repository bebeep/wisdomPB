package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.bean.SubmitEntity;
import com.bebeep.wisdompb.databinding.ActivityMySubmitBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.dagger.photopicker.bean.Image;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我提交的
 */
public class MySubmitActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityMySubmitBinding binding;
    private CommonAdapter adapter;
    private List<SubmitEntity> list = new ArrayList<>();

    private int pageNo = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_submit);
        init();
    }

    private void init(){
        initAdapter();
        getData();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我提交的");
        binding.title.ivBack.setVisibility(View.VISIBLE);

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void initAdapter(){
        adapter = new CommonAdapter<SubmitEntity>(this,R.layout.item_my_submit,list){
            @Override
            protected void convert(ViewHolder holder, SubmitEntity entity, int position) {
                holder.setText(R.id.tv_title,entity.getTitle());
                holder.setText(R.id.tv_time,entity.getCreateDate());
                holder.setText(R.id.tv_content,entity.getContent());

                String imgs = entity.getImgsrcs();
                holder.setVisible(R.id.recyclerView, !TextUtils.isEmpty(imgs));
                if(!TextUtils.isEmpty(imgs)){
                    String[] imgArr = imgs.split(",");
                    holder.setVisible(R.id.recyclerView, imgArr != null && imgArr.length > 0);
                    if(imgArr!=null&&imgArr.length>0){
                        RecyclerView recyclerView = holder.getView(R.id.recyclerView);
                        setInnerAdapter(recyclerView,imgArr);
                    }
                }

                holder.setVisible(R.id.ll_replay, !TextUtils.isEmpty(entity.getReplycontent()));
                holder.setText(R.id.tv_reply_time,"回复时间："+entity.getUpdateDate());
                holder.setText(R.id.tv_reply_content, entity.getReplycontent());
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }



    private void setInnerAdapter(RecyclerView recyclerView,String[] imgArr){
        List<String> innerList = new ArrayList<>();
        for (String s:imgArr) innerList.add(s);
        CommonAdapter adapter = new CommonAdapter<String>(this,R.layout.item_my_comment_inner,innerList){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.iv),URLS.IMAGE_PRE+s,R.drawable.default_error,90,60);
            }
        };
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        recyclerView.setAdapter(adapter);
    }



    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize",MyApplication.pageSize);
        OkHttpClientManager.postAsyn(URLS.MY_SUBMIT, new OkHttpClientManager.ResultCallback<BaseList<SubmitEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<SubmitEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                LogUtil.showLog("我提交的："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo==1) list = response.getData();
                    else {
                        if(response.getData()==null || response.getData().size() ==0) MyTools.showToast(MySubmitActivity.this,"没有更多内容了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MySubmitActivity.this, response.getMsg());
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
                pageNo ++;
                getData();
            }
        },500);
    }
}
