package com.bebeep.wisdompb.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.MeetingDetailsActivity;
import com.bebeep.wisdompb.activity.MeetingMinutesActivity;
import com.bebeep.wisdompb.activity.MyMeetingActivity;
import com.bebeep.wisdompb.activity.OrderMeetingActivity;
import com.bebeep.wisdompb.base.BaseFragment;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.MeetingEntity;
import com.bebeep.wisdompb.databinding.Fragment3Binding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment3 extends BaseFragment implements View.OnClickListener{

    private Fragment3Binding binding;
    private List<MeetingEntity> list = new ArrayList<>();
    private CommonAdapter adapter;


    @Override
    public void onResume() {
        super.onResume();
        if(binding!=null) getData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment3,container,false);
            init();
        }
        return binding.getRoot();
    }

    private void init(){
        initAdapter();
        getData();
        PicassoUtil.setImageUrl(getActivity(),binding.rimgHead, URLS.IMAGE_PRE + MyApplication.getInstance().getUserInfo().getPhoto(),R.drawable.icon_head,60,60);
        binding.setVariable(BR.onClickListener,this);
        binding.title.tvTitle.setText("三会一课");
//        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);

    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.MEETING_LIST, new OkHttpClientManager.ResultCallback<BaseList<MeetingEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<MeetingEntity> response) {
                LogUtil.showLog("三会一课列表："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    binding.tvNum.setVisibility(list==null||list.size()==0?View.GONE:View.VISIBLE);
                    binding.tvNum.setText(list==null?"0":String.valueOf(list.size()));
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(getActivity(), response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);

            }
        },header,map);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_right:

                break;
            case R.id.ll_f3_1://预约会议
                if(MyApplication.getInstance().getUserInfo().getMettingJurisdictionType() == 1){
                    startActivity(new Intent(getActivity(),OrderMeetingActivity.class));
                }else  MyTools.showToast(getActivity(),"无权限");
                break;
            case R.id.ll_f3_2://我的会议
                startActivity(new Intent(getActivity(),MyMeetingActivity.class));
                break;
            case R.id.ll_f3_3://会议纪要
                startActivity(new Intent(getActivity(),MeetingMinutesActivity.class));
                break;
        }
    }




    private void initAdapter(){
        adapter = new CommonAdapter<MeetingEntity>(getActivity(),R.layout.item_f3,list){
            @Override
            protected void convert(ViewHolder holder, MeetingEntity entity, int position) {
                String startTime = entity.getStartTime();
                if(!TextUtils.isEmpty(startTime) && startTime.length() >= 16){
                    String month = startTime.substring(0,7);
                    String day = startTime.substring(8,10);
                    holder.setText(R.id.tv_month,month);
                    holder.setText(R.id.tv_day,day);
                }
                holder.setText(R.id.tv_week,entity.getWeek());
                holder.setText(R.id.tv_title,entity.getTheme());
                holder.setText(R.id.tv_content,entity.getStartTime().substring(5,entity.getStartTime().length()) +"~"+entity.getEndTime().substring(5,entity.getEndTime().length())+"  "+entity.getAddress());
            }
        };

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(getActivity(),MeetingDetailsActivity.class).putExtra("id",list.get(position).getId()));
            }
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }
}
