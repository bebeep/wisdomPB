package com.bebeep.wisdompb.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.acker.simplezxing.activity.CaptureActivity;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.OrgActEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityPartyActDetailsBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.ufreedom.uikit.FloatingText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 党组织活动详情
 */
public class PartyActDetailsActivity extends BaseEditActivity implements View.OnClickListener,
        OnPullListener,SwipeRefreshLayout.OnRefreshListener,TextWatcher {
    private ActivityPartyActDetailsBinding binding;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private WebSettings settings;
    private List<CommentEntity> list = new ArrayList<>();
    private CommonAdapter adapter;

    private boolean open = false;
    private String id;
    private OrgActEntity entity;
    private CustomDialog customDialog;
    private CustomProgressDialog progressDialog;
    private String  parentId="0",repliedUserId="0",delCommentId="";
    private UserInfo userInfo;
    private FloatingText floatingText;//漂浮文字
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_party_act_details);
        progressDialog = new CustomProgressDialog(this);
        init();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        userInfo = MyApplication.getInstance().getUserInfo();
        initFloatingText();
        initAdapter();
        initDialog();
        getOrgDetails();
        getComment();
        binding.setVariable(BR.onClickListener,this);

        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("党组织活动");

        binding.etComment.addTextChangedListener(this);
        binding.etComment.setOnFocusChangeListener(onFocusChangeListener);

    }


    private void initUI(){
        if(entity == null) return;
        binding.tvTitle.setText(entity.getTitle());
        binding.tvTime.setText(entity.getStartTime() + " —— " + entity.getEndTime());
        binding.tvScanNum.setText(entity.getReadingQuantity());
        initWeb(URLS.HOST + entity.getInfoUrl());

        List<UserInfo> userList = entity.getActivityFoUserBizList();
        if(userList==null||userList.size()==0){
            binding.tvCommentUser.setText("已参与人员（0）");
        }else{
            binding.tvCommentUser.setText("已参与人员（"+userList.size()+"）");
            String names = "";
            for (UserInfo userInfo:userList){
                if(!TextUtils.isEmpty(userInfo.getName())) names+= userInfo.getName()+",";
            }
            if(!TextUtils.isEmpty(names) && names.length()>0) names = names.substring(0,names.length()-1);
            binding.tvUserNames.setText(names);
        }

        if(entity.getActivitySignJurisdictionType() == 1){
            binding.tvJoin.setText("统计活动签到");
        }else{
            binding.tvJoin.setText(TextUtils.equals("1",entity.getIsParticipate())?"已参与":"我要参与");
            binding.tvJoin.setBackgroundResource(TextUtils.equals("1",entity.getIsParticipate())?R.drawable.bg_tv_send_gray:R.drawable.bg_btn_join);
            binding.tvJoin.setClickable(!TextUtils.equals("1",entity.getIsParticipate()));
        }
        binding.ivZan.setImageResource(TextUtils.equals(entity.getIsDz(),"1")?R.drawable.icon_zan_c:R.drawable.icon_zan);
        binding.ivCollect.setImageResource(TextUtils.equals(entity.getIsCollection(),"1")?R.drawable.icon_collect_c:R.drawable.icon_collect);
    }


    private void initAdapter(){
        adapter = new CommonAdapter<CommentEntity>(this,R.layout.item_news_detail_comment,list){
            @Override
            protected void convert(ViewHolder holder,final CommentEntity entity, int position) {
                holder.setBackgroundRes(R.id.ll_parent,R.drawable.bg_null);
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
                        MyTools.showKeyboard(PartyActDetailsActivity.this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_zan://点赞
                zan();
                break;
            case R.id.iv_collect://收藏
                collect();
                break;
            case R.id.tv_join://我要参与
                if(TextUtils.equals(binding.tvJoin.getText().toString(),"我要参与")) {
                    LogUtil.showLog("签到");
                    if (ContextCompat.checkSelfPermission(PartyActDetailsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PartyActDetailsActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                    } else  startCaptureActivityForResult();
                }else {
                    startActivity(new Intent(PartyActDetailsActivity.this, MeetingJoinActivity.class).putExtra("id",id).putExtra("type",1));
                }
                break;
            case R.id.ll_user_open://展开/关闭
                open = !open;
                setUserNames(open);
                break;
            case R.id.tv_send://发送评论
                if(TextUtils.isEmpty(binding.etComment.getText().toString().replaceAll(" ",""))){
                    MyTools.showToast(this,"评论内容不能为空");
                    binding.etComment.setText("");
                    binding.etComment.clearFocus();
                    binding.tvSend.setClickable(false);
                }else submitComment();
                break;
        }
    }


    private void setUserNames(boolean open){
        if(open){
            binding.ivUserOpen.setRotation(180);
            binding.tvUserNames.setMaxLines(Integer.MAX_VALUE);
        }else {
            binding.ivUserOpen.setRotation(0);
            binding.tvUserNames.setMaxLines(2);
        }
    }


    private void setMultiColorText(TextView tv,String s,String name){
        s = "回复"+name+":"+s;
        SpannableString spannableString = new SpannableString(s);
        //设置颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3B3937")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//黑色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0F8DD6")), 2, 2+name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//蓝色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#8e8e8e")), 2+name.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//灰色
        tv.setText(spannableString);
    }

    private void getOrgDetails(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("id", id);
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.ORG_ACT_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<OrgActEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<OrgActEntity> response) {
                binding.nrl.onLoadFinished();
                binding.srl.setRefreshing(false);
                LogUtil.showLog("活动详情："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    initUI();
                }else{
                    MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
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
        map.put("type","4");
        OkHttpClientManager.postAsyn(URLS.COMMENT_GET, new OkHttpClientManager.ResultCallback<BaseList<CommentEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<CommentEntity> response) {
                Log.e("TAG","获取党组织详情评论 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    binding.tvComments.setText("评论（"+(list==null?0:list.size())+"）");
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
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
        map.put("type","4");
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
                MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
                if(response.isSuccess()){
                    parentId = "0";
                    repliedUserId = "0";
                    binding.etComment.setText("");
                    binding.etComment.clearFocus();
                    binding.tvSend.setClickable(false);
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
        map.put("type","4");
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
                MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
                if(response.isSuccess()){
                    getComment();
                }
            }
        },header,map);

    }

    /**
     * 参加活动
     */
    private void join(){
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("activityId",id);
        LogUtil.showLog("："+ map);
        OkHttpClientManager.postAsyn(URLS.ORG_ACT_JOIN, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("参加活动 response："+ MyApplication.gson.toJson(response));
                progressDialog.cancel();
                MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
                if(response.isSuccess()){
                    getOrgDetails();
                }
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
        map.put("type","3");
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
                }else MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
            }
        },header,map);

    }
    /**
     * 收藏
     */
    private void collect(){
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("type", "3");
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
                }else MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
            }
        },header,map);
    }


    /**
     * 签到
     */
    private void sign(String id){
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("type","1");
        LogUtil.showLog("活动签到："+map.toString());
        OkHttpClientManager.postAsyn(URLS.ACT_SIGN, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("签到 response："+ MyApplication.gson.toJson(response));
                MyTools.showToast(PartyActDetailsActivity.this,response.getMsg());
                if(response.isSuccess()){
                    getOrgDetails();
                }else MyTools.showToast(PartyActDetailsActivity.this, response.getMsg());
            }
        },header,map);
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

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                getOrgDetails();
                getComment();
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
            binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
        }
    };

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



    private void startCaptureActivityForResult() {
        Intent intent = new Intent(PartyActDetailsActivity.this, CaptureActivity.class);
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
                    MyTools.showToast(PartyActDetailsActivity.this,"请在设置中对本应用授权");
                }
            }
            break;
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CaptureActivity.REQ_CODE){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    String key = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                    LogUtil.showLog("扫描得到的活动："+key);
                    HashMap map = MyApplication.gson.fromJson(key,HashMap.class);
                    if(map == null) {
                        MyTools.showToast(PartyActDetailsActivity.this,"无效的二维码");
                        return;
                    }

                    if(TextUtils.isEmpty(String.valueOf(map.get("content")))) {
                        MyTools.showToast(PartyActDetailsActivity.this,"无效的二维码");
                        return;
                    }
                    sign(String.valueOf(map.get("content")));
                    break;
                case Activity.RESULT_CANCELED:
                    if (data != null) {
                        MyTools.showToast(PartyActDetailsActivity.this,"请在设置中对本应用授权");
                    }
                    break;
            }
        }
    }

}
