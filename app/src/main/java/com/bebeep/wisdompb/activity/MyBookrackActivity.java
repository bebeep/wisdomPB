package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.BookEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityMyBookrackBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的书架
 */
public class MyBookrackActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityMyBookrackBinding binding;
    private CommonAdapter adapter;
    private List<BookEntity> list = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter!=null) getData();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_bookrack);
        init();
    }


    private void init(){
        initAdapter();
        getData();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我的书架");

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter(){
        list.add(new BookEntity());
        adapter = new CommonAdapter<BookEntity>(this,R.layout.item_my_bookrack,list){
            @Override
            protected void convert(ViewHolder holder, final BookEntity entity, final int position) {
                final boolean isLastPosition = position == list.size()-1;
                holder.setVisible(R.id.fl_head,!isLastPosition);
                holder.setVisible(R.id.iv_add,isLastPosition);
                holder.setVisible(R.id.tv_book_name,!isLastPosition);
                holder.setVisible(R.id.tv_book_author,!isLastPosition);

                if(!isLastPosition){
                    holder.setImageUrl((ImageView)holder.getView(R.id.iv_head),URLS.IMAGE_PRE+entity.getImgsrc(),R.drawable.default_error,90,120);
                    holder.setText(R.id.tv_book_name,entity.getTitle());
                    holder.setText(R.id.tv_book_author,entity.getAuthor());
                }

                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isLastPosition){
                            startActivity(new Intent(MyBookrackActivity.this, LibraryTypeActivity.class));
                        }else {
                            startActivity(new Intent(MyBookrackActivity.this, BookDetailsActivity.class).putExtra("id",entity.getId()).putExtra("flag",1));
                        }
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        binding.recyclerView.setAdapter(adapter);
    }



    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.MY_BOOK, new OkHttpClientManager.ResultCallback<BaseList<BookEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<BookEntity> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("我的书架："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    if(list==null) list =new ArrayList<>();
                    list.add(new BookEntity());
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MyBookrackActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }



    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
               getData();
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

            }
        },500);
    }
}
