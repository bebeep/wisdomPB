package com.bebeep.wisdompb.activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.showbigimage.DeviceConfig;
import com.bebeep.commontools.showbigimage.ShowMultiBigImageDialog;
import com.bebeep.commontools.showbigimage.ShowSingleBigImageDialog;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomRoundAngleImageView;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.GalleryEntity;
import com.bebeep.wisdompb.bean.TestingEntity;
import com.bebeep.wisdompb.databinding.ActivityGalleryDetailBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 党建相册
 */
public class GalleryActivity extends BaseSlideActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityGalleryDetailBinding binding;
    private List<GalleryEntity> list = new ArrayList<>();
    private CommonAdapter adapter;

    private String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery_detail);
        init();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        initDeviceDensity();
        initAdapter();
        getData();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
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
        adapter = new CommonAdapter<GalleryEntity>(this,R.layout.item_gallery_detail,list){
            @Override
            protected void convert(ViewHolder holder, GalleryEntity entity, int position) {
                CustomRoundAngleImageView iv = holder.getView(R.id.iv);
                holder.setImageUrl(iv, URLS.IMAGE_PRE + entity.getImgsrc(),R.drawable.bg_book);
                holder.setText(R.id.tv_zan, entity.getReadingQuantity());
                holder.setImageResource(R.id.iv_zan, TextUtils.equals(entity.getIsDz(),"1")?R.drawable.icon_zan_yellow:R.drawable.icon_zan_gallery);

                holder.setOnClickListener(R.id.fl_zan, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                new ShowSingleBigImageDialog(GalleryActivity.this).show(URLS.IMAGE_PRE + list.get(position).getImgsrc(),R.drawable.bg_book); //单张图片
//                new ShowMultiBigImageDialog(GalleryActivity.this, list).show();
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
        map.put("id",id);
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.PHOTO_DETAILS, new OkHttpClientManager.ResultCallback<BaseList<GalleryEntity>>() {
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
                LogUtil.showLog("相册详情："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(GalleryActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }


    /**
     * 点赞
     */
    private void zan(){
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("type","");
        OkHttpClientManager.postAsyn(URLS.ZAN, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("点赞 response："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    getData();
                }else MyTools.showToast(GalleryActivity.this, response.getMsg());
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
        },300);
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
        },300);
    }

    private void initDeviceDensity(){
        DisplayMetrics metrics = new DisplayMetrics();//通过DisplayMetrics类可以得到手机屏幕的参数
        getWindowManager().getDefaultDisplay().getMetrics(metrics);//然后将DisplayMetrics对象传入
        DeviceConfig.EXACT_SCREEN_HEIGHT = metrics.heightPixels;//得到手机屏幕的高的分辨率
        DeviceConfig.EXACT_SCREEN_WIDTH = metrics.widthPixels;//得到手机屏幕宽的分辨率
    }
}
