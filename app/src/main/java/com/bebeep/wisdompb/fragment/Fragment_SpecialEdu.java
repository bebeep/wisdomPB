package com.bebeep.wisdompb.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ItemViewDelegate;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.LoginActivity;
import com.bebeep.wisdompb.activity.WebViewActivity;
import com.bebeep.wisdompb.base.BaseFragment;
import com.bebeep.wisdompb.base.CommonFragment;
import com.bebeep.wisdompb.bean.AdsEntity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.LoginEntity;
import com.bebeep.wisdompb.bean.SpecialEduEntity;
import com.bebeep.wisdompb.databinding.FragmentSpecialEduBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class Fragment_SpecialEdu extends CommonFragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private FragmentSpecialEduBinding binding;

    public static Fragment_SpecialEdu newInstance(String uId) {
        Bundle args = new Bundle();
        args.putString("uId", uId);
        Fragment_SpecialEdu fragment = new Fragment_SpecialEdu();
        fragment.setArguments(args);
        return fragment;
    }


    private List<SpecialEduEntity> list = new ArrayList<>();
    private List<String> imgList = new ArrayList<>(),titleList = new ArrayList<>();
    private List<AdsEntity> adsList = new ArrayList<>();
    private MultiItemTypeAdapter adapter;
    private int pageNo = 1;
    private String uId = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uId = getArguments().getString("uId");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding ==null ){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_special_edu,container,false);
            init();
        }
        return binding.getRoot();
    }

    private void init(){
        initAdapter();
        getads();
        getData();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);

        setOnRefreshTokenListener(new OnRefreshTokenListener() {
            @Override
            public void onRefreshTokenSuccess() {
                getads();
                getData();
            }
            @Override
            public void onRefreshTokenFail() {
                startActivityForResult(new Intent(getActivity(), LoginActivity.class).putExtra("tag",1),88);
            }
        });
    }


    private void initAdapter(){
        adapter = new MultiItemTypeAdapter(getActivity(),list);
        adapter.addItemViewDelegate(new ItemViewDelegate<SpecialEduEntity>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_special_edu_top;
            }
            @Override
            public boolean isForViewType(SpecialEduEntity s, int position) {
                return position == 0;
            }

            @Override
            public void convert(ViewHolder holder, SpecialEduEntity s, int position) {
                Banner banner = holder.getView(R.id.banner);
                initBanner(banner);
            }
        });
        adapter.addItemViewDelegate(new ItemViewDelegate<SpecialEduEntity>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_special_edu_bottom;
            }
            @Override
            public boolean isForViewType(SpecialEduEntity s, int position) {
                return position != 0;
            }
            @Override
            public void convert(ViewHolder holder, final SpecialEduEntity entity, int position) {
                holder.setImageUrl((ImageView) holder.getView(R.id.iv_head), URLS.IMAGE_PRE + entity.getPictureAddress(),R.drawable.default_error,60,90);
                holder.setText(R.id.tv_title, entity.getTitle());
                holder.setText(R.id.tv_time,entity.getUpdateDate());
                holder.setVisible(R.id.iv_link, TextUtils.equals(entity.getWhetherUrlAddress(),"1"));
                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.equals(entity.getWhetherUrlAddress(),"1")){
                            startActivity(new Intent(getActivity(),WebViewActivity.class).putExtra("title",entity.getTitle()).putExtra("url",entity.getUrl()));
                        }
                    }
                });
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);

    }

    /**
     * 获取广告
     */
    private void getads(){
        HashMap header = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.SPECIAL_EDU_ADS, new OkHttpClientManager.ResultCallback<BaseList<AdsEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<AdsEntity> response) {
                binding.srl.setRefreshing(false);
                Log.e("TAG","getads json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    imgList.clear();
                    titleList.clear();
                    adsList = response.getData();
                    if(adsList!=null && adsList.size()>0){
                        for (AdsEntity entity: adsList){
                            imgList.add(URLS.IMAGE_PRE + entity.getPictureAddress());
                            titleList.add(entity.getTitle());
                        }
                        adapter.refresh(list);
                    }

                }else{
                    MyTools.showToast(getActivity(), response.getMsg());
                }
            }
        },header,map);
    }

    private void getData(){
        HashMap header = new HashMap(), map = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        map.put("pageNo", String.valueOf(pageNo));
        map.put("pageSize", MyApplication.pageSize);
        map.put("homeNewsTypeIds", uId);
        OkHttpClientManager.postAsyn(URLS.SPECIAL_EDU_LIST, new OkHttpClientManager.ResultCallback<BaseList<SpecialEduEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<SpecialEduEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                LogUtil.showLog("专题教育-列表："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo == 1){
                        list.clear();
                        list.add(new SpecialEduEntity());
                        list.addAll(response.getData());
                    }else {
                        if(response.getData() ==null&&response.getData().size() ==0){
                            MyTools.showToast(getActivity(),"没有更多数据了");
                        }else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    if(response.getErrorCode() == 1) refreshToken();
                }
            }
        },header,map);
    }



    //初始化轮播图
    private void initBanner(Banner banner){
        if(imgList==null||imgList.size() ==0) return;
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setImages(imgList)
                .setBannerAnimation(Transformer.Default)
                .setBannerTitles(titleList)
                .setDelayTime(2000)
                .isAutoPlay(true)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        if(TextUtils.isEmpty((CharSequence) path)){
                            Picasso.with(context).load(com.bebeep.commontools.R.drawable.icon_error).into(imageView);
                        }else{
                            Picasso.with(context).load(String.valueOf(path))
                                    .placeholder(com.bebeep.commontools.R.drawable.defaultpic)
                                    .config(Bitmap.Config.RGB_565)
                                    .error(com.bebeep.commontools.R.drawable.icon_error)
                                    .into(imageView);
                        }
                    }
                })
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        String url = adsList.get(position).getUrl();
                        if(!TextUtils.isEmpty(url)){
                            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url",imgList.get(position)).putExtra("title",titleList.get(position)));
                        }
                    }
                })
                .start();

    }


    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo=1;
                getads();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 88 && resultCode == getActivity().RESULT_OK) getData();
    }
}
