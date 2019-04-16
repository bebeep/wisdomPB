package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.bebeep.commontools.views.CustomRoundAngleImageView;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.ExamEntity;
import com.bebeep.wisdompb.bean.GalleryEntity;
import com.bebeep.wisdompb.bean.LibraryListEntity;
import com.bebeep.wisdompb.bean.LibraryTypeEntity;
import com.bebeep.wisdompb.bean.MeetingEntity;
import com.bebeep.wisdompb.bean.NewsEntity;
import com.bebeep.wisdompb.bean.SpecialEduEntity;
import com.bebeep.wisdompb.databinding.ActivitySearchBinding;
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
 * 搜索
 */
public class SearchActivity extends BaseEditActivity implements View.OnClickListener,OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivitySearchBinding binding;

    private List list ;
    private String url="",idName = "";
    private int pageNo = 1,type=1,layoutResId;
    private CommonAdapter<Object> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        init();
    }

    private void init(){
        type = getIntent().getIntExtra("type",1);
        binding.setVariable(BR.onClickListener,this);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(type==2||type==5||type==6||type==7);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("搜索");
        binding.title.ivBack.setVisibility(View.VISIBLE);
        initType();
        initAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                if(TextUtils.isEmpty(binding.etContent.getText().toString())){
                    MyTools.showToast(this,"请输入搜索内容");
                    return;
                }
                binding.ivSearch.setEnabled(false);
                pageNo = 1;
                getData();
                break;
        }
    }


    private void initType(){
        switch (type){
            case 1://图书
                list = new ArrayList<LibraryListEntity>();
                url = URLS.LIBRARY_LIST;
                layoutResId =  R.layout.item_library_list;
                break;
            case 2://首页
                list = new ArrayList<NewsEntity>();
                url = URLS.HOST_LIST;
                layoutResId =  R.layout.item_f1;
                break;
            case 3://考试
                list = new ArrayList<ExamEntity>();
                url = URLS.EXAM_LIST;
                layoutResId =  R.layout.item_f2;
                break;
            case 4://三会一课
                list = new ArrayList<MeetingEntity>();
                url = URLS.MEETING_LIST;
                layoutResId =  R.layout.item_f3;
                break;
            case 5://党内公示
                list = new ArrayList<NewsEntity>();
                url = URLS.PUBLIC_SHOW_LIST;
                layoutResId =  R.layout.item_public_show;
                break;
            case 6://专题教育
                list = new ArrayList<SpecialEduEntity>();
                url = URLS.SPECIAL_EDU_LIST;
                layoutResId =  R.layout.item_special_edu_bottom;
                break;
            case 7://相册
                list = new ArrayList<GalleryEntity>();
                url = URLS.PHOTO_LIST;
                layoutResId =  R.layout.item_gallery;
                break;
        }
    }


    private void initAdapter(){
        adapter = new CommonAdapter<Object>(this,layoutResId,list) {
            @Override
            protected void convert(ViewHolder holder, Object o, int position) {
                if(type == 1){//图书
                    final LibraryListEntity entity = MyApplication.gson.fromJson(MyApplication.gson.toJson(o),LibraryListEntity.class);
                    holder.setImageUrl((ImageView)holder.getView(R.id.iv_head), URLS.IMAGE_PRE + entity.getImgsrc(),R.drawable.default_error, 90,120);
                    holder.setText(R.id.tv_name,"书名："+entity.getTitle());
                    holder.setText(R.id.tv_author, entity.getAuthor());
                    holder.setText(R.id.tv_reduce, entity.getEditorRecommendation());
                    holder.setOnClickListener(R.id.fl_parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SearchActivity.this, BookDetailsActivity.class).putExtra("id",entity.getId()));
                        }
                    });
                }else if(type == 2){//首页
                    final NewsEntity entity = MyApplication.gson.fromJson(MyApplication.gson.toJson(o),NewsEntity.class);
                    holder.setImageUrl((ImageView)holder.getView(R.id.iv_head),URLS.IMAGE_PRE + entity.getPictureAddress(),R.drawable.default_error,80,60);
                    holder.setText(R.id.tv_title,entity.getTitle());
                    holder.setText(R.id.tv_time,entity.getUpdateDate());
                    holder.setVisible(R.id.iv_link, TextUtils.equals(entity.getWhetherUrlAddress(),"1"));

                    holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.equals(entity.getWhetherUrlAddress(),"1")){
                                startActivity(new Intent(SearchActivity.this,WebViewActivity.class).putExtra("title",entity.getTitle()).putExtra("url",entity.getUrl()));
                            }else startActivity(new Intent(SearchActivity.this,NewsDetailActivity.class).putExtra("title",entity.getTitle()).putExtra("id",entity.getId()).putExtra("tag",1));
                        }
                    });
                }else if(type == 3){//考试
                    LogUtil.showLog("搜索 考试："+MyApplication.gson.toJson(o));
                    final ExamEntity entity = MyApplication.gson.fromJson(MyApplication.gson.toJson(o),ExamEntity.class);
                    if(TextUtils.equals(entity.getState(),"1")) {
                        holder.setText(R.id.tv_state,"进行中");
                        holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_yellow);
                    }else if(TextUtils.equals(entity.getState(),"0")) {
                        holder.setText(R.id.tv_state,"未开始");
                        holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_green);
                    }else if(TextUtils.equals(entity.getState(),"2")) {
                        holder.setText(R.id.tv_state,"已过期");
                        holder.setBackgroundRes(R.id.tv_state, R.drawable.bg_rec_2dp_gray);
                    }
                    holder.setText(R.id.tv_title,entity.getTitle());
                    if(!TextUtils.isEmpty(entity.getStartTime())&& entity.getStartTime().length()>=16
                            && !TextUtils.isEmpty(entity.getEndTime())&& entity.getEndTime().length()>=16){
                        String startDay = entity.getStartTime().substring(0,10);
                        String endDay = entity.getEndTime().substring(0,10);

                        String startTime = entity.getStartTime().substring(11,16);
                        String endTime = entity.getEndTime().substring(11,16);
                        if(TextUtils.equals(startDay,endDay)){
                            holder.setText(R.id.tv_time,startDay + " "+startTime+"~"+endTime);
                        }else holder.setText(R.id.tv_time,entity.getStartTime() +"~"+ entity.getEndTime());
                    }else holder.setText(R.id.tv_time,entity.getStartTime() +"~"+ entity.getEndTime());
                    holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.equals(entity.getState(),"0")){
                                MyTools.showToast(SearchActivity.this,"考试还未开始");
                            }else if(TextUtils.equals(entity.getState(),"1")){
                                startActivityForResult(new Intent(SearchActivity.this,ExamActivity.class).putExtra("id",entity.getId()), MyApplication.ACTIVITY_BACK_CODE);
                            }else if(TextUtils.equals(entity.getState(),"2")){
                                MyTools.showToast(SearchActivity.this,"考试已过期");
                            }
                        }
                    });
                }else if(type == 4){//三会一课
                    final MeetingEntity entity = MyApplication.gson.fromJson(MyApplication.gson.toJson(o),MeetingEntity.class);
                    String startTime = entity.getStartTime();
                    if(!TextUtils.isEmpty(startTime) && startTime.length() >= 16){
                        String month = startTime.substring(0,7);
                        String day = startTime.substring(8,10);
                        holder.setText(R.id.tv_month,month);
                        holder.setText(R.id.tv_day,day);
                    }
                    holder.setText(R.id.tv_week,entity.getWeek());
                    holder.setText(R.id.tv_title,entity.getTheme());
                    holder.setText(R.id.tv_content,entity.getStartTime().substring(5,entity.getStartTime().length()) +"~"+entity.getEndTime().substring(5,entity.getEndTime().length())+"  "+entity.getAddress());
                    holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SearchActivity.this,MeetingDetailsActivity.class).putExtra("id",entity.getId()));
                        }
                    });
                }else if(type == 5){//党内公示
                    final NewsEntity entity = MyApplication.gson.fromJson(MyApplication.gson.toJson(o),NewsEntity.class);
                    ImageView iv = holder.getView(R.id.iv_head);
                    holder.setImageUrl(iv,URLS.IMAGE_PRE+entity.getPictureAddress(),R.drawable.default_error,80,60);
                    holder.setText(R.id.tv_title,entity.getTitle());
                    holder.setText(R.id.tv_time,entity.getUpdateDate());
                    holder.setText(R.id.tv_time,entity.getUpdateDate());
                    holder.setVisible(R.id.iv_link,TextUtils.equals(entity.getWhetherUrlAddress(),"1"));
                    holder.setOnClickListener(R.id.parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.equals(entity.getWhetherUrlAddress(),"1")){
                                startActivity(new Intent(SearchActivity.this,WebViewActivity.class).putExtra("title",entity.getTitle()).putExtra("url",entity.getUrl()));
                            }else startActivity(new Intent(SearchActivity.this,NewsDetailActivity.class).putExtra("title",entity.getTitle()).putExtra("id",entity.getId()).putExtra("tag",3));

                        }
                    });
                }else if(type == 6){//专题教育
                    final SpecialEduEntity entity = MyApplication.gson.fromJson(MyApplication.gson.toJson(o),SpecialEduEntity.class);
                    holder.setImageUrl((ImageView) holder.getView(R.id.iv_head), URLS.IMAGE_PRE + entity.getPictureAddress(),R.drawable.default_error,80,60);
                    holder.setText(R.id.tv_title, entity.getTitle());
                    holder.setText(R.id.tv_time,entity.getUpdateDate());
                    holder.setVisible(R.id.iv_link, TextUtils.equals(entity.getWhetherUrlAddress(),"1"));
                    holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(TextUtils.equals(entity.getWhetherUrlAddress(),"1")){
                                startActivity(new Intent(SearchActivity.this,WebViewActivity.class).putExtra("title",entity.getTitle()).putExtra("url",entity.getUrl()));
                            }else startActivity(new Intent(SearchActivity.this,NewsDetailActivity.class).putExtra("title",entity.getTitle()).putExtra("id",entity.getId()).putExtra("tag",2));
                        }
                    });
                }else if(type == 7){//党建相册
                    final GalleryEntity entity = MyApplication.gson.fromJson(MyApplication.gson.toJson(o),GalleryEntity.class);
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
                    holder.setOnClickListener(R.id.parent, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(SearchActivity.this,GalleryActivity.class).putExtra("id",entity.getId()));
                        }
                    });
                }
            }
        };

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

    }





    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put(idName,"");
        map.put("title", binding.etContent.getText().toString());
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize",MyApplication.pageSize);

        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(url, new OkHttpClientManager.ResultCallback<BaseList>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.ivSearch.setEnabled(true);
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList response) {
                binding.ivSearch.setEnabled(true);
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                LogUtil.showLog("搜索结果："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    if(pageNo == 1)list = response.getData();
                    else {
                        if(response.getData() == null || response.getData().size()==0)MyTools.showToast(SearchActivity.this,"没有更多内容");
                        else list.addAll(response.getData());
                    }
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(SearchActivity.this, response.getMsg());
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
                    final Bitmap finalBitmap = EasyBlur.with(SearchActivity.this)
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == MyApplication.ACTIVITY_BACK_CODE){
                finish();
                MainActivity.getInstance().showFragment(0);
            }
        }
    }

    @Override
    public void onRefresh() {
        binding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo = 1;
                getData();
            }
        },500);

    }

    @Override
    public void onRefresh(AbsRefreshLayout listLoader) {

    }

    @Override
    public void onLoading(AbsRefreshLayout listLoader) {
        binding.recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo ++;
                getData();
            }
        },500);
    }
}
