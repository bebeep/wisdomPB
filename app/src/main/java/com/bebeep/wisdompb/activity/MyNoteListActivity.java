package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.bean.NoteEntity;
import com.bebeep.wisdompb.databinding.ActivityMynoteListBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的笔记列表
 */
public class MyNoteListActivity extends BaseSlideActivity implements View.OnClickListener,OnPullListener,
        SwipeRefreshLayout.OnRefreshListener{
    private ActivityMynoteListBinding binding;
    private CommonAdapter adapter;
    private List<NoteEntity> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mynote_list);
        init();
    }

    private void init(){
        initAdapter();
        getData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("我的笔记");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
    }


    private void initAdapter(){
        adapter = new CommonAdapter<NoteEntity>(this,R.layout.item_note_list,list){
            @Override
            protected void convert(ViewHolder holder, final NoteEntity entity, int position) {

                holder.setImageUrl((ImageView)holder.getView(R.id.iv_head),URLS.IMAGE_PRE+entity.getImgsrc(),R.drawable.default_error,90,120);
                holder.setText(R.id.tv_name,"书名："+entity.getTitle());
                holder.setText(R.id.tv_author,"作者："+entity.getAuthor());
                holder.setText(R.id.tv_note_num, entity.getNoteContent());

                holder.setOnClickListener(R.id.fl_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MyNoteListActivity.this,MyNoteActivity.class).putExtra("id",entity.getId()));
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back://返回
                finish();
                break;
        }
    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.MY_NOTE_LIST, new OkHttpClientManager.ResultCallback<BaseList<NoteEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<NoteEntity> response) {
                LogUtil.showLog("我的笔记 ："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MyNoteListActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
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
}
