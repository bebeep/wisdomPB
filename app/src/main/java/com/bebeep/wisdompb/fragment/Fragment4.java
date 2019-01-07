package com.bebeep.wisdompb.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.listener.SoftKeyBoardListener;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemDivider;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.showbigimage.ShowSingleBigImageDialog;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.NewsDetailActivity;
import com.bebeep.wisdompb.activity.PlayVideoActivity;
import com.bebeep.wisdompb.activity.ReleaseDiscoverActivity;
import com.bebeep.wisdompb.base.BaseFragment;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.DiscoverEntity;
import com.bebeep.wisdompb.databinding.Fragment4Binding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.dagger.photopicker.bean.Image;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

import static android.app.Activity.RESULT_OK;

public class Fragment4 extends BaseFragment implements OnPullListener,TextWatcher,View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener{

    private Fragment4Binding binding;
    private CommonAdapter adapter;
    private List<DiscoverEntity> list = new ArrayList<>();
    private int pageNo = 1,delType = 0;
    private ShowSingleBigImageDialog showSingleBigImageDialog;
    private CustomDialog customDialog1,customDialog2;
    private boolean showKeyBoard = false;


    //软键盘的状态
    public boolean isShowKeyBoard() {
        return showKeyBoard;
    }

    private String crcleFriendsId = "",parentId="0",repliedUserId="0",delCommentId="",delDiscoverId="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(binding == null){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment4,container,false);
            init();
        }
        view = binding.getRoot();
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mainActivity.onTouchEvent(event);
                return false;
            }
        });
        return view;
    }

    @SuppressLint("NewApi")
    private void init(){
        showSingleBigImageDialog = new ShowSingleBigImageDialog(getActivity());
        initAdapter();
        initDialog();
        getData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.tvTitle.setText("发现");
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_f3_edit);
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.etComment.setOnFocusChangeListener(onFocusChangeListener);
        binding.etComment.addTextChangedListener(this);


        binding.title.ivTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), ReleaseDiscoverActivity.class), 88);
            }
        });

        binding.nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(showKeyBoard)setEditEnable(false,v);
            }
        });

        //监听键盘的弹出与隐藏
        SoftKeyBoardListener.setListener(mainActivity, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
            }
            @Override
            public void keyBoardHide(int height) {
                setEditEnable(false,getView());
            }
        });
    }

    private void initDialog(){
        customDialog1 = new CustomDialog.Builder(getActivity())
                .setMessage("您确定要删除该条评论吗")
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog1.cancel();
                    }
                }).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog1.cancel();
                       if(delType==0) delComment(delCommentId);
                       else if(delType == 1) delDiscover(delCommentId);
                    }
                })
                .createTwoButtonDialog();
        customDialog2 = new CustomDialog.Builder(getActivity())
                .setMessage("您确定要删除该条发现吗")
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog2.cancel();
                    }
                }).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog2.cancel();
                        delDiscover(delDiscoverId);
                    }
                })
                .createTwoButtonDialog();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_send: //发表评论
                LogUtil.showLog("发表评论");
                submitComment();
                break;
        }
    }

    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        map.put("pageNo",String.valueOf(pageNo));
        map.put("pageSize", MyApplication.pageSize);
        OkHttpClientManager.postAsyn(URLS.DISCOVER_LIST, new OkHttpClientManager.ResultCallback<BaseList<DiscoverEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<DiscoverEntity> response) {
                binding.srl.setRefreshing(false);
                binding.nrl.onLoadFinished();
                if(response.isSuccess()){
                    List<DiscoverEntity> tempList = response.getData();
                    if(tempList!=null && tempList.size()>0){
                        for (int i =0;i<tempList.size();i++)getComment(i,tempList.get(i));
                    }
                    if(pageNo == 1){
                        list = response.getData();
                        adapter.refresh(list);
                    } else{
                        if(tempList == null || tempList.size() ==0 ) MyTools.showToast(getActivity(),"没有更多数据了");
                        else {
                            list.addAll(tempList);
                            adapter.refresh(list);
                        }
                    }
                }else {
                    if(response.getErrorCode() ==1) refreshToken();
                    else MyTools.showToast(getActivity(),response.getMsg());
                }
                LogUtil.showLog("发现列表 = response： "+MyApplication.gson.toJson(response));
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
        map.put("themeId",crcleFriendsId);
        map.put("parentId",parentId);
        map.put("repliedUserId",repliedUserId);
        map.put("content",binding.etComment.getText().toString());
        map.put("type","4");
        LogUtil.showLog("提交评论："+ map.toString());
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
                MyTools.showToast(mainActivity, response.getMsg());
                if(response.isSuccess()){
                    crcleFriendsId = "";
                    parentId = "0";
                    repliedUserId = "0";
                    setEditEnable(false,getView());
                    getData();
                }
            }
        },header,map);

    }

    /**
     * 获取评论
     */
    private void getComment(final int position,final DiscoverEntity entity){
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",entity.getId());
        map.put("type","4");
        LogUtil.showLog("获取评论："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.COMMENT_GET, new OkHttpClientManager.ResultCallback<BaseList<CommentEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<CommentEntity> response) {
                LogUtil.showLog("获取评论 response："+ MyApplication.gson.toJson(response));
                progressDialog.cancel();
                if(response.isSuccess()){
                    entity.setCommentList(response.getData());
                    list.set(position,entity);
                    adapter.refresh(list);
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
        map.put("id", commentId);
        map.put("type","4");
        LogUtil.showLog("提交评论："+ map.toString());
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
                MyTools.showToast(getActivity(), response.getMsg());
                if(response.isSuccess()){
                    getData();
                }
            }
        },header,map);

    }
    /**
     * 删除发现
     */
    private void delDiscover(String id){
        progressDialog.show();
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("id", id);
        LogUtil.showLog("删除发现："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.DISCOVER_DELETE, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("删除发现 response："+ MyApplication.gson.toJson(response));
                progressDialog.cancel();
                MyTools.showToast(getActivity(), response.getMsg());
                if(response.isSuccess()){
                    getData();
                }
            }
        },header,map);

    }

    //软键盘显示与隐藏
    public void setEditEnable(boolean enable, View v){
        LogUtil.showLog("enable:"+enable);
        showKeyBoard = enable;
        if(enable){
            binding.llEdit.setVisibility(View.VISIBLE);
            binding.etComment.requestFocus();
            mainActivity.showBottom(false);
            MyTools.showKeyboard(getActivity());
        }else {
            binding.etComment.setText("");
            binding.etComment.clearFocus();
            binding.tvSend.setClickable(false);
            mainActivity.showBottom(true);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    private void initAdapter(){
        adapter = new CommonAdapter<DiscoverEntity>(getActivity(),R.layout.item_f4,list){
            @Override
            protected void convert(final ViewHolder holder, final DiscoverEntity entity, int position) {
                holder.setOnClickListener(R.id.iv_comment, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        crcleFriendsId = entity.getId();
                        parentId = "0";
                        repliedUserId = "0";
                        setEditEnable(true,v);
                        binding.etComment.setHint("说点什么吧...");
                    }
                });

                holder.setOnClickListener(R.id.fl_video, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), PlayVideoActivity.class).putExtra("url", URLS.IMAGE_PRE + entity.getVideoUrl()));
                    }
                });

                holder.setImageUrl((ImageView) holder.getView(R.id.rimg_head), URLS.IMAGE_PRE +entity.getPhoto(),R.drawable.icon_head,40,40);
                holder.setText(R.id.tv_name, entity.getName());
                holder.setText(R.id.tv_time, entity.getCreateDate());
                holder.setText(R.id.tv_content,entity.getTitle());
                holder.setVisible(R.id.tv_del, true);
                holder.setVisible(R.id.tv_del, TextUtils.equals(entity.getUserId(),MyApplication.getInstance().getUserInfo().getUserId()));
                if(TextUtils.equals(entity.getType(),"0")){//视频
                    holder.setVisible(R.id.fl_video,true);
                    holder.setVisible(R.id.recyclerView1,false);
                    holder.setImageUrl((ImageView) holder.getView(R.id.iv_video), URLS.IMAGE_PRE +entity.getImgsrcs(),R.drawable.default_error);
                }else if(TextUtils.equals(entity.getType(),"1")){//图片
                    holder.setVisible(R.id.fl_video,false);
                    holder.setVisible(R.id.recyclerView1,true);
                    setInnerAdapter1((RecyclerView)holder.getView(R.id.recyclerView1),entity.getImgsrcs());
                }else if(TextUtils.equals(entity.getType(),"2")){ //普通文本、
                    holder.setVisible(R.id.fl_video,false);
                    holder.setVisible(R.id.recyclerView1,false);
                }
                setInnerAdapter2((RecyclerView)holder.getView(R.id.recyclerView),position);


                holder.setOnClickListener(R.id.tv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delDiscoverId = entity.getId();
                        if(customDialog2!=null){
                            customDialog2.show();
                        }
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MultiItemDivider itemDivider = new MultiItemDivider(getActivity(), MultiItemDivider.VERTICAL_LIST, R.drawable.v_divider_line);
        itemDivider.setDividerMode(MultiItemDivider.INSIDE);
        binding.recyclerView.addItemDecoration(itemDivider);
        binding.recyclerView.setAdapter(adapter);

    }

    //图
    private void setInnerAdapter1(RecyclerView recyclerView, String pics){
        if(TextUtils.isEmpty(pics) || TextUtils.equals(pics,",")) return;
        List<String> picList = new ArrayList<>();
        String[] picsArr = pics.split(",");
        if(picsArr==null||picsArr.length==0)return;
        for (String s:picsArr){
            if(!TextUtils.isEmpty(s))picList.add(s);
        }
        CommonAdapter adapter = new CommonAdapter<String>(getActivity(), R.layout.item_f4_inner1,picList){
            @Override
            protected void convert(ViewHolder holder, final String s, final int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.iv), URLS.IMAGE_PRE + s,R.drawable.default_error,60,90);
                holder.setOnClickListener(R.id.iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //查看大图
                        showSingleBigImageDialog.show(URLS.IMAGE_PRE + s,R.drawable.default_error); //单张图片
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        recyclerView.setAdapter(adapter);
    }

    //评论
    private void setInnerAdapter2(RecyclerView recyclerView,final int listPos){
        final List<CommentEntity> commentList = list.get(listPos).getCommentList();
        if(commentList ==null ||commentList.size() ==0) return;
        CommonAdapter adapter = new CommonAdapter<CommentEntity>(getActivity(), R.layout.item_f4_inner2,commentList){
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
                        crcleFriendsId = list.get(listPos).getId();
                        parentId = entity.getId();
                        repliedUserId = entity.getUserId();
                        binding.etComment.setHint("回复"+entity.getName()+":");
                        setEditEnable(true,v);
                    }
                });
                holder.setOnLongClickListener(R.id.ll_parent, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(TextUtils.equals(entity.getUserId(),MyApplication.getInstance().getUserInfo().getId())) {//表示是用户自己发布的评论
                            delCommentId = entity.getId();
                            if(customDialog1!=null){
                                customDialog1.show();
                            }
                            return true;
                        }return false;
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
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
        binding.nrl.postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNo ++;
                getData();
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
            boolean hasContent = !TextUtils.isEmpty(binding.etComment.getText().toString());
            binding.tvSend.setClickable(hasContent);
            if(hasFocus){
                binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
            }else{
                binding.llEdit.setVisibility(View.GONE);
                mainActivity.showBottom(true);
                binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == 88) {
                pageNo =1 ;
                binding.srl.setRefreshing(true);
                getData();
            }
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
}
