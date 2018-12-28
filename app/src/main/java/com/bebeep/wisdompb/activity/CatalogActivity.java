package com.bebeep.wisdompb.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.PerformerListAdapter;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.BookEntity;
import com.bebeep.wisdompb.bean.CatalogEntity;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.databinding.ActivityCatalogBinding;
import com.bebeep.wisdompb.util.ListDividerItemDecoration;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.free.sticky.StickyItemDecoration;

/**
 * 图书目录
 */
public class CatalogActivity extends BaseSlideActivity {
    private ActivityCatalogBinding binding;
    private List<CommonTypeEntity> list = new ArrayList<>();
    private CommonAdapter adapter;

    private String id;//图书id


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_catalog);
        init();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        if(TextUtils.isEmpty(id) || TextUtils.isEmpty(title)){
            MyTools.showToast(this,"该书籍不存在");
            finish();
        }
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText(title);
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initAdapter();
        getData();
    }

    private void initAdapter(){
        adapter = new CommonAdapter<CommonTypeEntity>(this,R.layout.item_catalog_title,list){
            @Override
            protected void convert(ViewHolder holder, final CommonTypeEntity entity, int position) {
                holder.setText(R.id.tv_top,entity.getTitle());
                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //点击目录
                        startActivity(new Intent(CatalogActivity.this,BookContentActivity.class).putExtra("id",entity.getId()));
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    private void getData() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("books.id", id);
        OkHttpClientManager.postAsyn(URLS.LIBRARY_CATALOG_LIST, new OkHttpClientManager.ResultCallback<BaseList<CommonTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
            }
            @Override
            public void onResponse(BaseList<CommonTypeEntity> response) {
                Log.e("TAG", "图书目录 response: " + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                      list = response.getData();
                      adapter.refresh(list);
                } else {
                    MyTools.showToast(CatalogActivity.this,response.getMsg());
                    if (response.getErrorCode() == 1) refreshToken();
                }
            }
        }, header, map);
    }





    //吸附效果
    private void initData(){
        List<CatalogEntity> list = new ArrayList<>();
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


        LinearLayoutManager manager = new LinearLayoutManager(this);
        PerformerListAdapter adapter = new PerformerListAdapter(this, list);
        binding.recyclerView.addItemDecoration(new StickyItemDecoration());
        binding.recyclerView.addItemDecoration(new ListDividerItemDecoration());
        binding.recyclerView.setLayoutManager(manager);
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
}
