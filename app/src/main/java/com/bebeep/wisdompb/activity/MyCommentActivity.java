package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityMyCommentBinding;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的评论
 */
public class MyCommentActivity extends SlideBackActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityMyCommentBinding binding;

    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_comment);
        init();
    }

    private void init(){
        initAdapter();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我的评论");
    }





    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(this, R.layout.item_my_comment,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                RecyclerView recyclerView = holder.getView(R.id.recyclerView);
                setInnerAdapter(recyclerView);

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    private void setInnerAdapter(RecyclerView recyclerView){
        List<String> innerList = new ArrayList<>();
        innerList.add("");
        innerList.add("");
        innerList.add("");
        innerList.add("");
        CommonAdapter adapter = new CommonAdapter<String>(this,R.layout.item_my_comment_inner,innerList){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        recyclerView.setAdapter(adapter);
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
                list.add("");
                list.add("");
                adapter.refresh(list);
                binding.nrl.onLoadFinished();
            }
        },500);
    }
}
