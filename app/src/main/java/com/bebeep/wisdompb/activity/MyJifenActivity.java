package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityMyJifenBinding;
import com.bebeep.wisdompb.util.ListDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的积分
 */
public class MyJifenActivity extends SlideBackActivity implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener,OnPullListener {

    private ActivityMyJifenBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_jifen);
        init();
    }

    private void init(){
        initAdapter();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("参会扫码");
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_myjifen_top_right);
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
            case R.id.iv_title_right:

                break;
            case R.id.ll_details:
                startActivity(new Intent(MyJifenActivity.this, JifenDetailsActivity.class));
                break;
        }
    }

    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(this,R.layout.item_my_jifen,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                if(position == 0){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range1);
                }else if(position == 1){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range2);
                }else if(position == 2){
                    holder.setVisible(R.id.tv_range,false);
                    holder.setVisible(R.id.iv_range,true);
                    holder.setImageResource(R.id.iv_range,R.drawable.icon_jifen_range3);
                }else {
                    holder.setVisible(R.id.tv_range,true);
                    holder.setVisible(R.id.iv_range,false);
                    holder.setText(R.id.tv_range,""+(position+1));
                }
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
