package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.RoundImage;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.SubmitEntity;
import com.bebeep.wisdompb.databinding.ActivityMyCommentBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 我的评论
 */
public class MyCommentActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityMyCommentBinding binding;

    private List<CommentEntity> list = new ArrayList<>();
    private CommonAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_comment);
        init();
    }

    private void init(){
        initAdapter();
        getData();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("我的评论");
    }





    private void initAdapter(){
        adapter = new CommonAdapter<CommentEntity>(this, R.layout.item_my_comment,list){
            @Override
            protected void convert(ViewHolder holder, final CommentEntity entity, int position) {
                holder.setImageUrl((RoundImage)holder.getView(R.id.rimg_head),URLS.IMAGE_PRE + entity.getPhoto(),R.drawable.icon_head,45,45);
                holder.setText(R.id.tv_name,entity.getName());
                holder.setText(R.id.tv_time,entity.getCreateDate());
                holder.setText(R.id.tv_comment,entity.getContent());

                if(!TextUtils.isEmpty(entity.getThemeImgsrcs())){
                    String[] imgs = entity.getThemeImgsrcs().split(",");
                    if(imgs!=null && imgs.length>0){
                        holder.setVisible(R.id.iv_source,true);
                        holder.setImageUrl((ImageView) holder.getView(R.id.iv_source),URLS.IMAGE_PRE + imgs[0],R.drawable.icon_head,45,45);
                    }else holder.setVisible(R.id.iv_source,false);
                }else holder.setVisible(R.id.iv_source,false);

                holder.setText(R.id.tv_source_title,entity.getThemeTitle());

                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (entity.getType()){
                            case 0://首页新闻
                                startActivity(new Intent(MyCommentActivity.this,NewsDetailActivity.class).putExtra("title",entity.getThemeTitle()).putExtra("id",entity.getThemeId()).putExtra("tag",1));
                                break;
                            case 1://专题教育
                                startActivity(new Intent(MyCommentActivity.this,NewsDetailActivity.class).putExtra("title",entity.getThemeTitle()).putExtra("id",entity.getThemeId()).putExtra("tag",2));
                                break;
                            case 2://党内公示
                                startActivity(new Intent(MyCommentActivity.this,NewsDetailActivity.class).putExtra("title",entity.getThemeTitle()).putExtra("id",entity.getThemeId()).putExtra("tag",3));
                                break;
                            case 3://发现
                                setResult(RESULT_OK);
                                finish();
                                break;
                            case 4://活动
                                startActivity(new Intent(MyCommentActivity.this,PartyActDetailsActivity.class).putExtra("id",entity.getThemeId()));
                                break;
                            case 5://图书评论
                                startActivity(new Intent(MyCommentActivity.this, BookDetailsActivity.class).putExtra("id",entity.getThemeId()));
                                break;
                        }
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    private void setInnerAdapter(RecyclerView recyclerView){
        List<String> innerList = new ArrayList<>();
        innerList.add("");
        innerList.add("");
        innerList.add("");
        innerList.add("");
        CommonAdapter adapter = new CommonAdapter<String>(this,R.layout.item_my_comment_inner,innerList){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        recyclerView.setAdapter(adapter);
    }


    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.MY_COMMENT, new OkHttpClientManager.ResultCallback<BaseList<CommentEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                binding.tvEmpty.setVisibility(list==null||list.size()==0? View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<CommentEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                LogUtil.showLog("我的评论："+MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(MyCommentActivity.this, response.getMsg());
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
