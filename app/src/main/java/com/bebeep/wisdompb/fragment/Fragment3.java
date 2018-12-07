package com.bebeep.wisdompb.fragment;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.MeetingDetailsActivity;
import com.bebeep.wisdompb.activity.MeetingMinutesActivity;
import com.bebeep.wisdompb.activity.MyMeetingActivity;
import com.bebeep.wisdompb.activity.OrderMeetingActivity;
import com.bebeep.wisdompb.base.BaseFragment;
import com.bebeep.wisdompb.databinding.Fragment3Binding;

import java.util.ArrayList;
import java.util.List;

public class Fragment3 extends BaseFragment implements View.OnClickListener{

    private Fragment3Binding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;

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
        binding.setVariable(BR.onClickListener,this);
        binding.title.tvTitle.setText("会议签到");
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_right:
                MyTools.showToast(getActivity(),"search");
                break;
            case R.id.ll_f3_1://预约会议
                startActivity(new Intent(getActivity(),OrderMeetingActivity.class));
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
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(getActivity(),R.layout.item_f3,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(getActivity(),MeetingDetailsActivity.class));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }
}
