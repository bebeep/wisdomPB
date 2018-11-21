package com.bebeep.wisdompb.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemDivider;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.Fragment4Binding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class Fragment4 extends Fragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private Fragment4Binding binding;
    private CommonAdapter adapter;
    private List<String> list = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment4,container,false);
            init();
        }
        return binding.getRoot();
    }

    private void init(){
        initAdapter();
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_f3_edit);
        binding.title.ivTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTools.showToast(getActivity(),"edit");
            }
        });

        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
    }


    private void initAdapter(){
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        adapter = new CommonAdapter<String>(getActivity(),R.layout.item_f4,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                showPic(list,holder);
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MultiItemDivider itemDivider = new MultiItemDivider(getActivity(), MultiItemDivider.VERTICAL_LIST, R.drawable.v_divider_line);
        itemDivider.setDividerMode(MultiItemDivider.INSIDE);
        binding.recyclerView.addItemDecoration(itemDivider);
        binding.recyclerView.setAdapter(adapter);

    }

    private void showPic(List<String> picList,ViewHolder holder){
        if(picList == null) {
            holder.setVisible(R.id.ll_pic, false);
            return;
        }
        int i = picList.size();
        if (i == 0) {
            holder.setVisible(R.id.ll_pic, false);
        } else if (i == 1) {
            holder.setVisible(R.id.ll_pic, true);
            holder.setVisible(R.id.iv1, true);
            holder.setVisible(R.id.iv2, false);
            holder.setVisible(R.id.iv3, false);
            showImg((ImageView) holder.getView(R.id.iv1), picList.get(0));
        } else if (i == 2) {
            holder.setVisible(R.id.ll_pic, true);
            holder.setVisible(R.id.iv1, true);
            holder.setVisible(R.id.iv2, true);
            holder.setVisible(R.id.iv3, false);
            showImg((ImageView) holder.getView(R.id.iv1), picList.get(0));
            showImg((ImageView) holder.getView(R.id.iv2), picList.get(1));
        } else if (i > 2) {
            holder.setVisible(R.id.ll_pic, true);
            holder.setVisible(R.id.iv1, true);
            holder.setVisible(R.id.iv2, true);
            holder.setVisible(R.id.iv3, true);
            showImg((ImageView) holder.getView(R.id.iv1), picList.get(0));
            showImg((ImageView) holder.getView(R.id.iv2), picList.get(1));
            showImg((ImageView) holder.getView(R.id.iv3), picList.get(2));

        }
    }


    private void showImg(ImageView iv,String url){
        Picasso.with(getActivity()).load(url + "")
                .placeholder(com.bebeep.commontools.R.drawable.defaultpic)
                .config(Bitmap.Config.RGB_565)
                .error(com.bebeep.commontools.R.drawable.defaultpic)
                .into(iv);
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
