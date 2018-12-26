package com.bebeep.wisdompb.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.ExamActivity;
import com.bebeep.wisdompb.base.BaseFragment;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.bean.MeetingEntity;
import com.bebeep.wisdompb.databinding.Fragment2Binding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class Fragment2 extends BaseFragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private Fragment2Binding binding;
    private CommonAdapter adapter;
    private List<ExamEntity> list = new ArrayList<>();
    private int flag = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment2,container,false);
            init();
        }
        return binding.getRoot();
    }


    private void init(){
        initAdapter();
        getData();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("在线考试");
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);

        binding.title.ivTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTools.showToast(getActivity(),"search");
            }
        });

        binding.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb1:
                        flag = 1;
                        getData();
                        break;
                    case R.id.rb2:
                        flag = 2;
                        getData();
                        break;
                }
            }
        });
    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(flag == 1?URLS.EXAM_LIST : URLS.EXAMED_LIST, new OkHttpClientManager.ResultCallback<BaseList<ExamEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<ExamEntity> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("待考试列表 ："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(response.getData());
                }else{
                    MyTools.showToast(getActivity(), response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);

            }
        },header,map);
    }


    private void initAdapter(){
        adapter = new CommonAdapter<ExamEntity>(getActivity(),R.layout.item_f2,list){
            @Override
            protected void convert(ViewHolder holder, ExamEntity entity, int position) {
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

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                ExamEntity entity = list.get(position);
//                if(TextUtils.equals(entity.getState(),"0")){
//                    MyTools.showToast(getActivity(),"考试还未开始");
//                }else if(TextUtils.equals(entity.getState(),"1")){
//                    startActivityForResult(new Intent(getActivity(),ExamActivity.class).putExtra("id",entity.getId()), MyApplication.ACTIVITY_BACK_CODE);
//                }else if(TextUtils.equals(entity.getState(),"2")){
//                    MyTools.showToast(getActivity(),"考试已过期");
//                }
                startActivityForResult(new Intent(getActivity(),ExamActivity.class).putExtra("id",entity.getId()), MyApplication.ACTIVITY_BACK_CODE);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
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




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK){
            if(requestCode == MyApplication.ACTIVITY_BACK_CODE){
                mainActivity.showFragment(0);
            }
        }
    }
}
