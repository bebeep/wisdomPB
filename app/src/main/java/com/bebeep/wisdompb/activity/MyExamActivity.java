package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.databinding.ActivityMyExamBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 我的考试
 */
public class MyExamActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityMyExamBinding binding;
    private CommonAdapter adapter;
    private List<ExamEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_exam);
        init();
    }


    private void init(){
        initAdapter();
        getData();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我的考试");

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        adapter = new CommonAdapter<ExamEntity>(this,R.layout.item_f2,list){
            @Override
            protected void convert(ViewHolder holder, final ExamEntity entity, int position) {
                if(TextUtils.equals(entity.getState(),"1")) {
                    holder.setText(R.id.tv_state,"进行中");
                    holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_yellow);
                }else if(TextUtils.equals(entity.getState(),"0")) {
                    holder.setText(R.id.tv_state,"未开始");
                    holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_green);
                }else if(TextUtils.equals(entity.getState(),"2")) {
                    holder.setText(R.id.tv_state,"已过期");
                    holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_gray);
                }
                holder.setText(R.id.tv_title,entity.getTitle());
                if(!TextUtils.isEmpty(entity.getStartTime())&& entity.getStartTime().length()>=16
                        && !TextUtils.isEmpty(entity.getEndTime())&& entity.getEndTime().length()>=16){
                    String startDay = entity.getStartTime().substring(0,10);
                    String endDay = entity.getEndTime().substring(0,10);

                    String startTime = entity.getStartTime().substring(11,16);
                    String endTime = entity.getEndTime().substring(11,16);
                    if(TextUtils.equals(startDay,endDay)){
                        holder.setText(R.id.tv_time,startDay + " "+startTime+"~"+endTime);
                    }else holder.setText(R.id.tv_time,entity.getStartTime() +"~"+ entity.getEndTime());
                }else holder.setText(R.id.tv_time,entity.getStartTime() +"~"+ entity.getEndTime());


                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.equals(entity.getState(),"0")){
                            MyTools.showToast(MyExamActivity.this,"考试还未开始");
                        }else if(TextUtils.equals(entity.getState(),"1")){
                            startActivityForResult(new Intent(MyExamActivity.this,ExamActivity.class).putExtra("id",entity.getId()), MyApplication.ACTIVITY_BACK_CODE);
                        }else if(TextUtils.equals(entity.getState(),"2")){
                            MyTools.showToast(MyExamActivity.this,"考试已过期");
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
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.EXAM_MY_LIST, new OkHttpClientManager.ResultCallback<BaseList<ExamEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<ExamEntity> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("我的考试 ："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MyExamActivity.this, response.getMsg());
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
            }
        },500);
    }
}
