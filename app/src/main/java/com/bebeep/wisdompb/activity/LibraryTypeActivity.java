package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.databinding.library.baseAdapters.BR;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.LibraryTypeEntity;
import com.bebeep.wisdompb.databinding.ActivityLibraryTypeBinding;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 图书馆-图书类型
 */
public class LibraryTypeActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityLibraryTypeBinding binding;
    private List<LibraryTypeEntity> list = new ArrayList<>();
    private CommonAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_type);
        init();
    }

    private void init() {
        initAdapter();
        getData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("图书馆");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setOnRefreshTokenListener(new OnRefreshTokenListener() {
            @Override
            public void onRefreshTokenSuccess() {
                getData();
            }
            @Override
            public void onRefreshTokenFail() {
                startActivityForResult(new Intent(LibraryTypeActivity.this, LoginActivity.class).putExtra("tag",1),88);
            }
        });
    }


    private void initAdapter(){
        adapter = new CommonAdapter<LibraryTypeEntity>(this,R.layout.item_library_type,list){
            @Override
            protected void convert(ViewHolder holder, LibraryTypeEntity entity, int position) {
                holder.setImageUrl((ImageView) holder.getView(R.id.img_head), URLS.IMAGE_PRE + entity.getImgsrc(),R.drawable.default_error,90,60);
                holder.setText(R.id.tv_name,entity.getName());
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(LibraryTypeActivity.this,LibraryListActivity.class)
                        .putExtra("title",list.get(position).getName()).putExtra("id",list.get(position).getId()));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }



    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        map.put("parentId","0");
        OkHttpClientManager.postAsyn(URLS.LIBRARY_TYPE, new OkHttpClientManager.ResultCallback<BaseList<LibraryTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<LibraryTypeEntity> response) {
                Log.e("TAG","图书类型 response: "+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    if(response.getErrorCode() == 1){
                        refreshToken();
                    }
                }
            }
        },header,map);
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
                binding.nrl.onLoadFinished();
            }
        },500);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 88 && resultCode == RESULT_OK) getData();
    }
}
