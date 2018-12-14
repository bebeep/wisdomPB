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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.BookDetailsActivity;
import com.bebeep.wisdompb.base.CommonFragment;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.LibraryListEntity;
import com.bebeep.wisdompb.bean.LibraryTypeEntity;
import com.bebeep.wisdompb.databinding.FragmentLibraryListBinding;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class Fragment_LibraryList extends CommonFragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    public static Fragment_LibraryList newInstance(String uId) {
        Bundle args = new Bundle();
        args.putString("uId", uId);
        Fragment_LibraryList fragment = new Fragment_LibraryList();
        fragment.setArguments(args);
        return fragment;
    }


    private FragmentLibraryListBinding binding;
    private List<LibraryListEntity> list = new ArrayList<>();
    private CommonAdapter adapter;
    private String id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getArguments().getString("uId");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding ==null ){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_library_list,container,false);
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
    }




    private void initAdapter(){
        adapter = new CommonAdapter<LibraryListEntity>(getActivity(),R.layout.item_library_list,list){
            @Override
            protected void convert(ViewHolder holder, LibraryListEntity entity, int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.iv_head), URLS.IMAGE_PRE + entity.getImgsrc(),R.drawable.default_error, 90,120);
                holder.setText(R.id.tv_name,"书名："+entity.getTitle());
                holder.setText(R.id.tv_author, entity.getAuthor());
                holder.setText(R.id.tv_reduce, entity.getEditorRecommendation());
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(getActivity(), BookDetailsActivity.class));
            }
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }


    private void getData() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        map.put("booksCategory.id", id);
        OkHttpClientManager.postAsyn(URLS.LIBRARY_LIST, new OkHttpClientManager.ResultCallback<BaseList<LibraryListEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
            }
            @Override
            public void onResponse(BaseList<LibraryListEntity> response) {
                Log.e("TAG", "图书列表 response: " + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                    list = response.getData();
                    adapter.refresh(list);
                } else {
                    if (response.getErrorCode() == 1) {
                        refreshToken();
                    }
                }
            }
        }, header, map);
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
}
