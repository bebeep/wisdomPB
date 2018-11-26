package com.bebeep.wisdompb.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.PerformerListAdapter;
import com.bebeep.wisdompb.bean.CatalogEntity;
import com.bebeep.wisdompb.databinding.ActivityCatalogBinding;
import com.bebeep.wisdompb.util.ListDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import me.free.sticky.StickyItemDecoration;

/**
 * 图书目录
 */
public class CatalogActivity extends SlideBackActivity {
    private ActivityCatalogBinding binding;

    private LinearLayoutManager manager;
    private PerformerListAdapter adapter;
    private List<CatalogEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_catalog);
        init();
    }

    private void init(){
        String title = getIntent().getStringExtra("title");
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText(title);
        initAdapter();
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("NewApi")
    private void initAdapter(){

        initData();
        manager = new LinearLayoutManager(this);
        binding.recyclerView.addItemDecoration(new StickyItemDecoration());
        binding.recyclerView.addItemDecoration(new ListDividerItemDecoration());
        binding.recyclerView.setLayoutManager(manager);
        adapter = new PerformerListAdapter(this, list);
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new PerformerListAdapter.OnItemClickListener() {
            @Override
            public void OnTitleClick(int position) {
                Log.e("TAG","onTitleClick"+position);
            }

            @Override
            public void OnContentClick(int position) {
                Log.e("TAG","onContentClick:"+position);
                startActivity(new Intent(CatalogActivity.this,BookContentActivity.class));
            }
        });


    }

    private void initData(){
        list.add(new CatalogEntity("第一章 。。。看了坚实的卡拉胶施蒂利克快乐撒多即可拉伸建档立卡是点开链接是考虑到（测试多行文字）"));
        list.add(new CatalogEntity("第1节",1));
        list.add(new CatalogEntity("第2节",1));
        list.add(new CatalogEntity("第3节",1));
        list.add(new CatalogEntity("第4节",1));
        list.add(new CatalogEntity("第二章 。。。"));
        list.add(new CatalogEntity("第1节",1));
        list.add(new CatalogEntity("第2节来看左上角的考拉时间段克拉斯点击奥斯卡来得及阿克苏来得及昂克赛拉的（测试多行文字）",1));
        list.add(new CatalogEntity("第3节",1));
        list.add(new CatalogEntity("第4节",1));
        list.add(new CatalogEntity("第三章 。。。"));
        list.add(new CatalogEntity("第1节",1));
        list.add(new CatalogEntity("第2节",1));
        list.add(new CatalogEntity("第3节",1));
        list.add(new CatalogEntity("第4节",1));
        list.add(new CatalogEntity("第四章 。。。"));
        list.add(new CatalogEntity("第1节",1));
        list.add(new CatalogEntity("第2节",1));
        list.add(new CatalogEntity("第3节",1));
        list.add(new CatalogEntity("第4节",1));
    }


}
