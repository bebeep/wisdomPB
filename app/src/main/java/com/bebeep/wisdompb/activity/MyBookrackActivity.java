package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityMyBookrackBinding;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的书架
 */
public class MyBookrackActivity extends SlideBackActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityMyBookrackBinding binding;
    private CommonAdapter adapter;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_bookrack);
        init();
    }


    private void init(){
        initAdapter();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我的收藏");

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(this,R.layout.item_my_bookrack,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                boolean isLastPosition = position == list.size()-1;
                holder.setVisible(R.id.fl_head,!isLastPosition);
                holder.setVisible(R.id.iv_add,isLastPosition);
                holder.setVisible(R.id.tv_book_name,!isLastPosition);
                holder.setVisible(R.id.tv_book_author,!isLastPosition);

            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        binding.recyclerView.setAdapter(adapter);
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
