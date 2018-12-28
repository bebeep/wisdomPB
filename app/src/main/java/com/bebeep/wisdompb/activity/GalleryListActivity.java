package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.BitmapUtils;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomRoundAngleImageView;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.GalleryEntity;
import com.bebeep.wisdompb.databinding.ActivityGalleryBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 党建相册列表
 */
public class GalleryListActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityGalleryBinding binding;
    private List<GalleryEntity> list = new ArrayList<>();
    private CommonAdapter adapter;
    private int pageNo = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        init();
    }

    private void init(){
        initAdapter();
        getData();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("党建相册");
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void initAdapter(){
        adapter = new CommonAdapter<GalleryEntity>(this,R.layout.item_gallery,list){
            @Override
            protected void convert(ViewHolder holder, GalleryEntity entity, int position) {
                holder.setText(R.id.tv_title, entity.getTitle());
                holder.setText(R.id.tv_time, entity.getCreateDate());
                CustomRoundAngleImageView iv11 = holder.getView(R.id.iv11);
                CustomRoundAngleImageView iv22 = holder.getView(R.id.iv22);
                CustomRoundAngleImageView iv33 = holder.getView(R.id.iv33);
                if(TextUtils.isEmpty(entity.getImgsrcs())){
                    holder.setVisible(R.id.cv_1,false);
                    holder.setVisible(R.id.cv_2,false);
                    holder.setVisible(R.id.cv_3,false);
                    holder.setVisible(R.id.tv_empty, true);
                }else{
                    holder.setVisible(R.id.tv_empty, false);
                    String[] imgs = entity.getImgsrcs().split(",");
                    if(imgs !=null && imgs.length>0){
                        if(imgs.length==1){
                            holder.setVisible(R.id.cv_1,true);
                            holder.setVisible(R.id.cv_2,false);
                            holder.setVisible(R.id.cv_3,false);
                            initBlurImage(URLS.IMAGE_PRE+ imgs[0], iv11);
                            holder.setText(R.id.tv_num1, entity.getImgSize()+"+");
                        }
                        if(imgs.length==2){
                            holder.setVisible(R.id.cv_1,false);
                            holder.setVisible(R.id.cv_2,true);
                            holder.setVisible(R.id.cv_3,false);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv21),URLS.IMAGE_PRE+imgs[0],R.drawable.default_error,100,75);
                            initBlurImage(URLS.IMAGE_PRE+ imgs[1], iv22);
                            holder.setText(R.id.tv_num2, entity.getImgSize()+"+");
                        }
                        if(imgs.length>=3){
                            holder.setVisible(R.id.cv_1,false);
                            holder.setVisible(R.id.cv_2,false);
                            holder.setVisible(R.id.cv_3,true);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv31),URLS.IMAGE_PRE+imgs[0],R.drawable.default_error,100,75);
                            holder.setImageUrl((ImageView)holder.getView(R.id.iv32),URLS.IMAGE_PRE+imgs[1],R.drawable.default_error,100,75);
                            initBlurImage(URLS.IMAGE_PRE+ imgs[2], iv33);
                            holder.setText(R.id.tv_num3, entity.getImgSize()+"+");
                        }
                    }
                }
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(GalleryListActivity.this,GalleryActivity.class).putExtra("id",list.get(position).getId()));
            }
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }




    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize", MyApplication.pageSize);
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.PHOTO_LIST, new OkHttpClientManager.ResultCallback<BaseList<GalleryEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<GalleryEntity> response) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                LogUtil.showLog("相册："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo == 1)list = response.getData();
                    else{
                        if(response.getData()==null||response.getData().size()==0)  MyTools.showToast(GalleryListActivity.this,"没有更多内容了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(GalleryListActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }




    /**
     * 将网络图片模糊处理
     */
    private void initBlurImage(final String url, final ImageView iv){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap sourceImg = BitmapUtils.returnBitMap(url);
                if(sourceImg!=null){
                    final Bitmap finalBitmap = EasyBlur.with(GalleryListActivity.this)
                            .bitmap(sourceImg) //要模糊的图片
                            .radius(2)//模糊半径
                            .scale(4)//指定模糊前缩小的倍数
//                .policy(EasyBlur.BlurPolicy.FAST_BLUR)//使用fastBlur
                            .policy(EasyBlur.BlurPolicy.RS_BLUR)//使用fastBlur
                            .blur();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalBitmap!=null)iv.setImageBitmap(finalBitmap);
                        }
                    });
                }
            }
        }).start();

    }





    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo=1;
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
                pageNo++;
                getData();
            }
        },500);
    }
}
