package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.dokhttp.FileUtils;
import com.bebeep.commontools.file.FileUtil;
import com.bebeep.commontools.listener.SoftKeyBoardListener;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.NewsEntity;
import com.bebeep.wisdompb.databinding.ActivityNewsDetailBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.ufreedom.uikit.FloatingText;
import com.ufreedom.uikit.effect.ScaleFloatingAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsDetailActivity extends BaseEditActivity implements View.OnClickListener,TextWatcher{

    private ActivityNewsDetailBinding binding;
    private WebSettings settings;
    private String id;
    private CustomProgressDialog progressDialog;
    private CustomDialog customDialog;

    private List<CommentEntity> list = new ArrayList<>();
    private CommonAdapter adapter;

    private String  parentId="0",repliedUserId="0",delCommentId="";
    private NewsEntity entity;


    private String URL_DETAILS,KEY_TYPE;
    private FloatingText floatingText;//漂浮文字

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_detail);
        init();
    }

    private void init(){
        progressDialog = new CustomProgressDialog(this);
        String title = getIntent().getStringExtra("title");
        id = getIntent().getStringExtra("id");
        int tag = getIntent().getIntExtra("tag",-1);
        if(TextUtils.isEmpty(id)){
            MyTools.showToast(this,"参数错误！");
            finish();
        }
        initFloatingText();
        initURLS(tag);
        initDialog();
        initAdapter();
        getData();
        getComment();
        binding.setOnClickListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText(title);
        binding.etComment.setOnFocusChangeListener(onFocusChangeListener);
        binding.etComment.addTextChangedListener(this);



        //监听键盘的弹出与隐藏
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                binding.msv.scrollBy(0,height);
            }
            @Override
            public void keyBoardHide(int height) {
                binding.etComment.setText("");
                binding.etComment.clearFocus();
                binding.tvSend.setClickable(false);
//                binding.llComment.setVisibility(View.GONE);
            }
        });
    }

    private void initFloatingText(){
        floatingText = new FloatingText.FloatingTextBuilder(this)
                .textColor(Color.RED) // 漂浮字体的颜色
                .textSize(60)  // 浮字体的大小
                .textContent(" +1 ") // 浮字体的内容
                .offsetY(-50) // FloatingText 相对其所贴附View的垂直位移偏移量
                .build();
        floatingText.attach2Window(); //将FloatingText贴附在Window上
    }

    private void initURLS(int tag){
        switch (tag){
            case 1: //首页
                URL_DETAILS = URLS.HOST_DETAILS;
                KEY_TYPE = "0";
                break;
            case 2: //专题教育
                URL_DETAILS = URLS.SPECIAL_EDU_DETAILS;
                KEY_TYPE = "1";
                break;
            case 3://党内公示
                URL_DETAILS = URLS.PUBLIC_SHOW_DETAILS;
                KEY_TYPE = "2";
                break;
            case 4://首页广告详情
                URL_DETAILS = URLS.ADS_DETAILS;
                KEY_TYPE = "6";
                break;
            case 5://专题教育广告详情
                URL_DETAILS = URLS.SPECIAL_EDU_ADS_DETAILS;
                KEY_TYPE = "7";
                break;
        }
    }


    private void initUI(){
        if(entity == null) return;
        binding.tvTitle.setText(entity.getTitle());
        binding.tvTime.setText(entity.getUpdateDate());
        binding.tvScanNum.setText(entity.getReadingQuantity());
        initWeb(URLS.HOST+entity.getInfoUrl());
        if(TextUtils.isEmpty(entity.getEnclosureNmae()) || TextUtils.isEmpty(entity.getEnclosureUrl())) binding.flFile.setVisibility(View.GONE);
        else{
            if(entity.getEnclosureNmae().endsWith(".doc")||entity.getEnclosureNmae().endsWith(".docx")){
                binding.imgFileType.setImageResource(R.drawable.icon_doc);
            }else if(entity.getEnclosureNmae().endsWith(".pdf")){
                binding.imgFileType.setImageResource(R.drawable.icon_file_pdf);
            }else if(entity.getEnclosureNmae().endsWith(".xls")||entity.getEnclosureNmae().endsWith(".xlsx")){
                binding.imgFileType.setImageResource(R.drawable.icon_file_excel);
            }
            binding.tvFileName.setText(entity.getEnclosureNmae());
            binding.tvFileSize.setText(entity.getFilesSize());
        }
        binding.ivZan.setImageResource(entity.getIsDz() == 0?R.drawable.icon_zan:R.drawable.icon_zan_c);
        binding.ivCollect.setImageResource(entity.getIsCollection() == 0?R.drawable.icon_collect:R.drawable.icon_collect_c);

    }


    //加载webview
    private void initWeb(String url) {
        settings = binding.webview.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webview.loadUrl(url);
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;
            }
        });
        binding.webview.setWebViewClient(new WebViewClient());
    }

    private void initDialog(){
        customDialog = new CustomDialog.Builder(this)
                .setMessage("您确定要删除该条评论吗")
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                    }
                }).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                        delComment(delCommentId);
                    }
                })
                .createTwoButtonDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_file://下载文件
                downloadFile();
                break;
            case R.id.iv_zan://赞
                zan();
                break;
            case R.id.iv_collect://收藏
                collect();
                break;
            case R.id.tv_comment://立即评论
                binding.etComment.setText("");
                binding.llComment.setVisibility(View.VISIBLE);
                binding.etComment.requestFocus();
                MyTools.showKeyboard(this);
                break;
            case R.id.tv_send://发表
                if(TextUtils.isEmpty(binding.etComment.getText().toString().replaceAll(" ",""))){
                    MyTools.showToast(this,"评论内容不能为空");
                }else submitComment();
                break;
        }
    }


    //评论
    private void initAdapter(){
        adapter = new CommonAdapter<CommentEntity>(this, R.layout.item_news_detail_comment,list){
            @Override
            protected void convert(ViewHolder holder, final CommentEntity entity, final int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.rimg_head), URLS.IMAGE_PRE + entity.getPhoto(),R.drawable.icon_head,60,60);
                holder.setText(R.id.tv_name, entity.getName());
                holder.setText(R.id.tv_time, MyTools.formateTimeString(entity.getCreateDate()));

                String replaiedUserName = entity.getRepliedUserName();
                if(!TextUtils.isEmpty(replaiedUserName)){ //表示是回复别人的评论
                    setMultiColorText((TextView)holder.getView(R.id.tv_content),entity.getContent(),replaiedUserName);
                }else holder.setText(R.id.tv_content, entity.getContent());
                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parentId = entity.getId();
                        repliedUserId = entity.getUserId();
                        binding.etComment.setHint("回复"+entity.getName()+":");
                        binding.etComment.requestFocus();
//                        binding.llComment.setVisibility(View.VISIBLE);
                        MyTools.showKeyboard(NewsDetailActivity.this);
                    }
                });

                holder.setOnLongClickListener(R.id.ll_parent, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LogUtil.showLog("用户信息："+MyApplication.gson.toJson(MyApplication.getInstance().getUserInfo()));
                        if(TextUtils.equals(entity.getUserId(),MyApplication.getInstance().getUserInfo().getUserId())) {//表示是用户自己发布的评论
                            delCommentId = entity.getId();
                            if(customDialog!=null)customDialog.show();
                            return true;
                        }return false;
                    }
                });

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
    }


    /**
     * 获取详情
     */
    private void getData(){
        HashMap header = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("id", id);
        OkHttpClientManager.postAsyn(URL_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<NewsEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<NewsEntity> response) {
                Log.e("TAG","获取新闻详情 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    initUI();
                }else{
                    MyTools.showToast(NewsDetailActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    //获取评论
    private void getComment(){
        HashMap header = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("themeId", id);
        map.put("type",KEY_TYPE);
        OkHttpClientManager.postAsyn(URLS.COMMENT_GET, new OkHttpClientManager.ResultCallback<BaseList<CommentEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<CommentEntity> response) {
                Log.e("TAG","获取新闻评论 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(NewsDetailActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }


    /**
     * 提交评论
     */
    private void submitComment(){
        progressDialog.show();
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("parentId",parentId);
        map.put("type",KEY_TYPE);
        map.put("repliedUserId",repliedUserId);
        map.put("content", binding.etComment.getText().toString());
        OkHttpClientManager.postAsyn(URLS.COMMENT_SUBMIT, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("提交评论 response："+ MyApplication.gson.toJson(response));
                progressDialog.cancel();
                MyTools.showToast(NewsDetailActivity.this, response.getMsg());
                if(response.isSuccess()){
                    parentId = "0";
                    repliedUserId = "0";
                    binding.etComment.setText("");
                    binding.etComment.clearFocus();
                    binding.tvSend.setClickable(false);
//                    binding.llComment.setVisibility(View.GONE);
                    getComment();
                }
            }
        },header,map);
    }



    /**
     * 删除评论
     */
    private void delComment(String commentId){
        progressDialog.show();
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("id",commentId);
        map.put("type",KEY_TYPE);
        LogUtil.showLog("删除评论："+ map);
        OkHttpClientManager.postAsyn(URLS.COMMENT_DEL, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("删除评论 response："+ MyApplication.gson.toJson(response));
                progressDialog.cancel();
                MyTools.showToast(NewsDetailActivity.this, response.getMsg());
                if(response.isSuccess()){
                    getComment();
                }
            }
        },header,map);

    }


    /**
     * 点赞
     */
    private void zan(){
        if(TextUtils.equals(KEY_TYPE,"6")) KEY_TYPE = "5";
        else if(TextUtils.equals(KEY_TYPE,"7")) KEY_TYPE = "6";
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("type",KEY_TYPE);
        OkHttpClientManager.postAsyn(URLS.ZAN, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("点赞 response："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    int errorCode = response.getErrorCode();
                    binding.ivZan.setImageResource(errorCode == 6?R.drawable.icon_zan:R.drawable.icon_zan_c);
                    if(errorCode == 7) floatingText.startFloating(binding.ivZan);
                }else MyTools.showToast(NewsDetailActivity.this, response.getMsg());
            }
        },header,map);

    }
    /**
     * 收藏
     */
    private void collect(){
        if(TextUtils.equals(KEY_TYPE,"6")) KEY_TYPE = "5";
        else if(TextUtils.equals(KEY_TYPE,"7")) KEY_TYPE = "6";
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("type", KEY_TYPE);
        OkHttpClientManager.postAsyn(URLS.COLLECT, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("收藏 response："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    int errorCode = response.getErrorCode();
                    binding.ivCollect.setImageResource(errorCode == 6?R.drawable.icon_collect:R.drawable.icon_collect_c);
                }else MyTools.showToast(NewsDetailActivity.this, response.getMsg());
            }
        },header,map);
    }


    private void downloadFile(){
        OkHttpClientManager.downloadAsyn(URLS.IMAGE_PRE + entity.getEnclosureUrl(), MyApplication.FILE_PATH, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                MyTools.showToast(NewsDetailActivity.this,"下载出错，请重试");
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("下载文件："+ MyApplication.gson.toJson(response));
                if(!TextUtils.isEmpty(response)){
                    MyTools.showToast(NewsDetailActivity.this,"下载文件成功，已保存到"+MyApplication.FILE_PATH+"文件夹下");
                }else  MyTools.showToast(NewsDetailActivity.this,"下载失败，请重试");
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        binding.tvSend.setBackgroundResource(!TextUtils.isEmpty(s.toString())?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
        binding.tvSend.setClickable(!TextUtils.isEmpty(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.e("TAG","hasFocus:"+hasFocus);
            boolean hasContent = !TextUtils.isEmpty(binding.etComment.getText().toString());
            binding.tvSend.setClickable(hasContent);
            if(hasFocus){
                binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
            }else{
                binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
            }
        }
    };


    private void setMultiColorText(TextView tv,String s,String name){
        s = "回复"+name+":"+s;
        SpannableString spannableString = new SpannableString(s);
        //设置颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3B3937")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//黑色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0F8DD6")), 2, 2+name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//蓝色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#8e8e8e")), 2+name.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//灰色
        tv.setText(spannableString);
    }
}
