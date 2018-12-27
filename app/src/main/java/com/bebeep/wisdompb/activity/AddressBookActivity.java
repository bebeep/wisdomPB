package com.bebeep.wisdompb.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.TestingEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityAddressBookBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 党员通讯录
 */
public class AddressBookActivity extends BaseEditActivity implements View.OnClickListener,OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityAddressBookBinding binding;

    private CommonAdapter adapter;
    private List<UserInfo> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_book);
        init();
    }

    private void init(){
        initAdapter();
        getData();
        binding.setOnClickListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("党员通讯录");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
    }


    private void initAdapter(){
        adapter = new CommonAdapter<UserInfo>(this,R.layout.item_address_book,list){
            @Override
            protected void convert(ViewHolder holder, UserInfo userInfo, int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.img_head),URLS.IMAGE_PRE + userInfo.getPhoto(),R.drawable.icon_head,40,40);
                holder.setText(R.id.tv_name, userInfo.getName());
                holder.setText(R.id.tv_position, userInfo.getPartyPosts());
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(AddressBookActivity.this,MemberInfoActivity.class).putExtra("id",list.get(position).getId()));
            }
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.iv_search://搜索

                break;
        }
    }



    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("","");
        OkHttpClientManager.postAsyn(URLS.ADDRESSBOOK_LIST, new OkHttpClientManager.ResultCallback<BaseList<UserInfo>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.srl.setRefreshing(false);
                binding.tvEmpty.setVisibility(list==null||list.size()==0?View.VISIBLE:View.GONE);
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<UserInfo> response) {
                binding.srl.setRefreshing(false);
                LogUtil.showLog("通讯录列表："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(AddressBookActivity.this, response.getMsg());
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
