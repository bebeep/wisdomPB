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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.BookDetailsActivity;
import com.bebeep.wisdompb.databinding.FragmentLibraryListBinding;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class Fragment_LibraryList extends Fragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    public static Fragment_LibraryList newInstance(String uId) {
        Bundle args = new Bundle();
        args.putString("uId", uId);
        Fragment_LibraryList fragment = new Fragment_LibraryList();
        fragment.setArguments(args);
        return fragment;
    }


    private FragmentLibraryListBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;


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
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
    }




    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(getActivity(),R.layout.item_library_list,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

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
