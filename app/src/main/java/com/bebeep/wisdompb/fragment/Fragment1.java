package com.bebeep.wisdompb.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.acker.simplezxing.activity.CaptureActivity;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.AddressBookActivity;
import com.bebeep.wisdompb.activity.ChargeActivity;
import com.bebeep.wisdompb.activity.GalleryListActivity;
import com.bebeep.wisdompb.activity.LibraryTypeActivity;
import com.bebeep.wisdompb.activity.LoginActivity;
import com.bebeep.wisdompb.activity.MainActivity;
import com.bebeep.wisdompb.activity.NewsDetailActivity;
import com.bebeep.wisdompb.activity.NoticeActivity;
import com.bebeep.wisdompb.activity.PartyActActivity;
import com.bebeep.wisdompb.activity.PublicShowActivity;
import com.bebeep.wisdompb.activity.SpecialEduActivity;
import com.bebeep.wisdompb.activity.WebViewActivity;
import com.bebeep.wisdompb.base.BaseFragment;
import com.bebeep.wisdompb.bean.AdsEntity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.bean.NewsEntity;
import com.bebeep.wisdompb.databinding.Fragment1Binding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class Fragment1 extends BaseFragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private Fragment1Binding binding;
    private LinearLayout[] menus ;
    private static final int REQ_CODE_PERMISSION = 0x1111;

    private List<String> imgList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();
    private CommonAdapter adapter;

    private List<NewsEntity> list = new ArrayList<>();
    private List<CommonTypeEntity> typeList = new ArrayList<>();
    private String selectTypeId = "",selectTypeName = "";//被选中的类型id
    private int pageNo = 1;

    private List<AdsEntity> adsList;


    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null)PicassoUtil.setImageUrl(getActivity(),binding.title.rimgHead, URLS.IMAGE_PRE + MyApplication.getInstance().getUserInfo().getPhoto(),R.drawable.icon_head,40,40);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment1,container,false);
            init();
        }
        return binding.getRoot();
    }


    @SuppressLint("NewApi")
    private void init(){
        firstBanner();
        getads();
        initAdapter();
        initMenus();
        getType();

        mainActivity = (MainActivity) getActivity();
        mainActivity.addIgnoredView(binding.banner);
        mainActivity.addIgnoredView(binding.hs);
        mainActivity.addIgnoredView(binding.tabF1Title);

        binding.setVariable(BR.onClickListener,this);
        binding.title.flHead.setOnClickListener(this);
        binding.title.flHead.setVisibility(View.VISIBLE);
//        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
//        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);
        binding.title.tvTitle.setText("智慧党建");
        PicassoUtil.setImageUrl(getActivity(),binding.title.rimgHead, URLS.IMAGE_PRE + MyApplication.getInstance().getUserInfo().getPhoto(),R.drawable.icon_head,40,40);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);


        setOnRefreshTokenListener(new OnRefreshTokenListener() {
            @Override
            public void onRefreshTokenSuccess() {
                getads();
            }
            @Override
            public void onRefreshTokenFail() {
                startActivityForResult(new Intent(getActivity(), LoginActivity.class).putExtra("tag",1),88);
            }
        });


    }




    private void initTab(){
        if(typeList == null || typeList.size() == 0){
            binding.tvEmpty.setVisibility(View.VISIBLE);
            return;
        }
        binding.tabF1Title.removeAllTabs();
        for (int i=0;i<typeList.size();i++) {
            binding.tabF1Title.addTab(binding.tabF1Title.newTab().setText(typeList.get(i).getTitle()).setTag(i));
        }
        selectTypeId = typeList.get(0).getId();
        selectTypeName = typeList.get(0).getTitle();
        getData();
        binding.tabF1Title.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = (int) tab.getTag();
                LogUtil.showLog("tab:"+tab.getTag());
                pageNo = 1;
                selectTypeId = typeList.get(position).getId();
                selectTypeName = typeList.get(position).getTitle();
                getData();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //主界面
            case R.id.fl_head://点击头像-
                mainActivity.showMenu();
                break;
            case R.id.iv_title_right://-
//                MyTools.showToast(getActivity(),"search!");
                break;
            case R.id.ll_f1_t1://图书馆-
                startActivity(new Intent(getActivity(), LibraryTypeActivity.class));
                break;
            case R.id.ll_f1_t2://党组织活动-
                startActivity(new Intent(getActivity(), PartyActActivity.class));
                break;
            case R.id.ll_f1_t3://党内公示-
                startActivity(new Intent(getActivity(), PublicShowActivity.class));
                break;
            case R.id.ll_f1_t4://党费缴纳-
//                startActivity(new Intent(getActivity(), ChargeActivity.class));
                MyTools.showToast(getActivity(),"该功能正在研发中...");
                break;
            case R.id.ll_f1_t5://党建通讯录-
                startActivity(new Intent(getActivity(), AddressBookActivity.class));
                break;
            case R.id.ll_f1_t6://党建相册-
                startActivity(new Intent(getActivity(), GalleryListActivity.class));
                break;
            case R.id.ll_f1_t7://专题教育-
                startActivity(new Intent(getActivity(), SpecialEduActivity.class));
                break;
            case R.id.ll_f1_t8://通知公告-
                startActivityForResult(new Intent(getActivity(), NoticeActivity.class),77);
                break;
        }
    }

    private void initMenus(){
        menus = new LinearLayout[]{binding.llF1T1,binding.llF1T2,binding.llF1T3,binding.llF1T4,binding.llF1T5,binding.llF1T6,binding.llF1T7,binding.llF1T8};
        for (LinearLayout linearLayout:menus){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            params.width = MyTools.getWidth(getActivity()) / 4;
            linearLayout.setLayoutParams(params);
        }
    }


    private void initAdapter(){
        adapter = new CommonAdapter<NewsEntity>(getActivity(),R.layout.item_f1,list){
            @Override
            protected void convert(ViewHolder holder, final NewsEntity entity, int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.iv_head),URLS.IMAGE_PRE + entity.getPictureAddress(),R.drawable.default_error,80,60);
                holder.setText(R.id.tv_title,entity.getTitle());
                holder.setText(R.id.tv_time,entity.getUpdateDate());
                holder.setVisible(R.id.iv_link,TextUtils.equals(entity.getWhetherUrlAddress(),"1"));

                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(TextUtils.equals(entity.getWhetherUrlAddress(),"1")){
                            startActivity(new Intent(getActivity(),WebViewActivity.class).putExtra("title",entity.getTitle()).putExtra("url",entity.getUrl()));
                        }else startActivity(new Intent(getActivity(),NewsDetailActivity.class).putExtra("title",entity.getTitle()).putExtra("id",entity.getId()).putExtra("tag",1));
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
    }


    private void firstBanner(){
        adsList = new ArrayList<>();
        adsList.add(new AdsEntity("123213213"));
        initBanner();
    }



    //初始化轮播图
    private void initBanner(){
        if(adsList == null || adsList.size()==0)return;
        imgList.clear();
        titleList.clear();
        for (AdsEntity entity: adsList) {
            imgList.add(URLS.IMAGE_PRE+entity.getPictureAddress());
            titleList.add(entity.getTitle()+"");
        }
        binding.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setImages(imgList)
                .setBannerAnimation(Transformer.Default)
                .setBannerTitles(titleList)
                .setDelayTime(2000)
                .isAutoPlay(true)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        Log.e("TAG","path:"+path);
                        if(TextUtils.isEmpty((CharSequence) path)){
                            Picasso.with(context).load(com.bebeep.commontools.R.drawable.icon_error).into(imageView);
                        }else{
                            Picasso.with(context).load(String.valueOf(path))
                                    .placeholder(com.bebeep.commontools.R.drawable.icon_error)
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
                            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url",url).putExtra("title",titleList.get(position)));
                        }
                    }
                })
                .start();
        binding.banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                if(i== 0 || i == 1 || i == imgList.size()+1){
                   mainActivity.removeIgnoredView(binding.banner);
                }else mainActivity.addIgnoredView(binding.banner);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }



    /**
     * 获取广告
     */
    private void getads(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.ADS, new OkHttpClientManager.ResultCallback<BaseList<AdsEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<AdsEntity> response) {
                Log.e("TAG","getads json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    adsList = response.getData();
                    initBanner();
                }else{
                    MyTools.showToast(getActivity(), response.getMsg());
                }
            }
        },header,map);
    }
    /**
     * 获取类型
     */
    private void getType(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("", "");
        OkHttpClientManager.postAsyn(URLS.HOST_TYPE, new OkHttpClientManager.ResultCallback<BaseList<CommonTypeEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<CommonTypeEntity> response) {
                binding.srl.setRefreshing(false);
                Log.e("TAG","获取新闻类型 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    typeList = response.getData();
                    initTab();
                }else{
                    MyTools.showToast(getActivity(), response.getMsg());
                }
            }
        },header,map);
    }


    /**
     * 获取新闻列表
     */
    private void getData(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("pageNo", String.valueOf(pageNo));
        map.put("pageSize", "20");
        map.put("homeNewsTypeIds", selectTypeId);
        OkHttpClientManager.postAsyn(URLS.HOST_LIST, new OkHttpClientManager.ResultCallback<BaseList<NewsEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                statusMsg(e,code);
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<NewsEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                Log.e("TAG","获取新闻列表 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo == 1)list = response.getData();
                    else {
                        if(response.getData() == null || response.getData().size() ==0)MyTools.showToast(getActivity(),"没有更多内容了");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(getActivity(), response.getMsg());
                }
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }


    public void startPlay(){
        if(binding.banner!=null) {
            binding.banner.startAutoPlay();
        }
    }

    public void stopPlay(){
        if(binding.banner!=null) {
            binding.banner.stopAutoPlay();
        }
    }




    @Override
    public void onRefresh(AbsRefreshLayout listLoader) {
    }
    @Override
    public void onLoading(AbsRefreshLayout listLoader) {
        binding.nrl.postDelayed(new Runnable() {
            @Override
            public void run() {pageNo ++;getData();
            }
        },300);
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                getads();
                if(typeList == null || typeList.size() == 0)getType();
                else {
                    pageNo = 1;
                    getData();
                }
            }
        },300);
    }


    private void startCaptureActivityForResult() {
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    /**
     * 请求权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    MyTools.showToast(getActivity(),"请在设置中对本应用授权");
                }
            }
            break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 88 && resultCode == getActivity().RESULT_OK) {
            getads();
            getType();
        }else if(requestCode == 77) {
            if(resultCode == 1){//三会一课
                mainActivity.showFragment(2);
            }else if(resultCode == 2){//在线考试
                mainActivity.showFragment(1);
            }
        }else if(requestCode == CaptureActivity.REQ_CODE){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String key = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                    LogUtil.showLog("扫描二维码："+key);
                    break;
                case Activity.RESULT_CANCELED:
                    if (data != null) {
                        // for some reason camera is not working correctly
                        MyTools.showToast(getActivity(),data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                    }
                    break;
            }
        }
    }
}
