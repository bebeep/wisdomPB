package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.treeview.model.TreeNode;
import com.bebeep.commontools.treeview.view.AndroidTreeView;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.bean.DiscoverEntity;
import com.bebeep.wisdompb.bean.JoinerEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityChooseJoinerBinding;
import com.bebeep.wisdompb.holder.MyHolder;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 选择参会人员
 */
public class ChooseJoinerAcivity extends BaseSlideActivity {
    private ActivityChooseJoinerBinding binding;
    private CommonAdapter adapter;
    private List<JoinerEntity> list = new ArrayList<>();
    private AndroidTreeView tView;
    private CustomProgressDialog customProgressDialog;

    private String ids = "";
    private String name = "";
    private int size = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_joiner);
        init();
    }

    private void init(){
        customProgressDialog = new CustomProgressDialog(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("选择参会人员");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initAdapter();
        getData();


        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ids = "";
                name = "";
                size = 0;
                setResultData(list);
                LogUtil.showLog("选中的ids："+ ids+"|name:"+name+"|size:"+size);

                if(!TextUtils.isEmpty(ids) && !TextUtils.equals(ids,",")){
                    String[] names = name.split(",");
                    Intent intent = new Intent();
                    intent.putExtra("ids",ids);
                    if(names!=null&&names.length>0)intent.putExtra("name",names[0] + "等"+size+"名人员");
                    setResult(RESULT_OK,intent);
                }
                finish();

            }
        });

    }




    /**
     * 获取评论
     */
    private void getData(){
        customProgressDialog.show();
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("","");
        LogUtil.showLog("获取参会人员："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.MEETING_GET_MEMBER, new OkHttpClientManager.ResultCallback<BaseList<JoinerEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                customProgressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseList<JoinerEntity> response) {
                LogUtil.showLog("获取参会人员 response："+MyApplication.gson.toJson(response));
                customProgressDialog.cancel();
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }
            }
        },header,map);
    }

    private void initAdapter(){
        adapter = new CommonAdapter<JoinerEntity>(this,R.layout.item_choose_joiner,list){
            @Override
            protected void convert(final ViewHolder holder, final JoinerEntity entity, int position) {
                holder.setVisible(R.id.recyclerView1,entity.isShowChild());
                holder.setVisible(R.id.recyclerView,entity.isShowChild());
                holder.setVisible(R.id.iv_check,entity.isShowChild());
                holder.setText(R.id.tv_title,entity.getTitle());

                holder.setImageResource(R.id.iv_check, entity.isChecked()?R.drawable.icon_checkall_c:R.drawable.icon_checkall_n);
                ImageView iv_arrow = holder.getView(R.id.iv_arrow);
                iv_arrow.setRotation(entity.isShowChild()?0:-90);

                setInnerAdapter((RecyclerView) holder.getView(R.id.recyclerView1),entity,holder);
                if(entity.getChildrens() != null || entity.getChildrens().size() >= 0){
                    setTopAdapter((RecyclerView) holder.getView(R.id.recyclerView),entity.getChildrens());
                }

                iv_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(entity.isShowChild()) setShowChild(entity,false);
                        else entity.setShowChild(true);
                        adapter.refresh(list);
                    }
                });

                holder.setOnClickListener(R.id.iv_check, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCheck(entity,!entity.isChecked());
                        adapter.refresh(list);
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }

    private void setTopAdapter(RecyclerView recyclerView, final List<JoinerEntity> joinerList){
        if(joinerList == null || joinerList.size()==0) return;
        final CommonAdapter innerAdapter = new CommonAdapter<JoinerEntity>(this,R.layout.item_choose_joiner, joinerList){
            @Override
            protected void convert(final ViewHolder holder, final JoinerEntity entity, final int position) {
                holder.setVisible(R.id.v_bottom, position!= joinerList.size()-1);
                holder.setVisible(R.id.recyclerView1,entity.isShowChild());
                holder.setVisible(R.id.iv_check,entity.isShowChild());
                holder.setText(R.id.tv_title,entity.getTitle());

                holder.setImageResource(R.id.iv_check, entity.isChecked()?R.drawable.icon_checkall_c:R.drawable.icon_checkall_n);
                ImageView iv_arrow = holder.getView(R.id.iv_arrow);
                iv_arrow.setRotation(entity.isShowChild()?0:-90);

                setInnerAdapter((RecyclerView) holder.getView(R.id.recyclerView1),entity,holder);
                if(entity.getChildrens() != null || entity.getChildrens().size() >= 0){
                    setTopAdapter((RecyclerView) holder.getView(R.id.recyclerView),entity.getChildrens());
                }

                iv_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(entity.isShowChild()) setShowChild(entity,false);
                        else entity.setShowChild(true);
                        adapter.refresh(list);
                    }
                });

                holder.setOnClickListener(R.id.iv_check, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setCheck(entity,!entity.isChecked());
                        adapter.refresh(list);
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(innerAdapter);
    }

    private void setInnerAdapter(RecyclerView recyclerView, final JoinerEntity entity,final ViewHolder parentHolder){
        final List<JoinerEntity> joinerList = entity.getUserList();
        if(joinerList == null || joinerList.size()==0) return;
        final CommonAdapter innerAdapter = new CommonAdapter<JoinerEntity>(this,R.layout.item_choose_joiner_inner, joinerList){
            @Override
            protected void convert(final ViewHolder holder, final JoinerEntity joinerEntity, final int position) {
                holder.setVisible(R.id.v_top_line, position==0);
                holder.setImageUrl((ImageView) holder.getView(R.id.iv_head), URLS.IMAGE_PRE+ joinerEntity.getPhoto(),R.drawable.icon_head,40,40);
                holder.setText(R.id.tv_name,joinerEntity.getName());
                holder.setImageResource(R.id.iv_check, joinerEntity.isChecked()?R.drawable.icon_checkall_c:R.drawable.icon_checkall_n);
                holder.setOnClickListener(R.id.fl_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinerEntity.setChecked(!joinerEntity.isChecked());
                        holder.setImageResource(R.id.iv_check, joinerEntity.isChecked()?R.drawable.icon_checkall_c:R.drawable.icon_checkall_n);
                        boolean isAllChecked = isAllChecked(joinerList);
                        entity.setChecked(isAllChecked);
                        parentHolder.setImageResource(R.id.iv_check, isAllChecked?R.drawable.icon_checkall_c:R.drawable.icon_checkall_n);
                    }
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(innerAdapter);
    }

    //判断是否全选
    private boolean isAllChecked(List<JoinerEntity> joinerList){
        if(joinerList == null) return false;
        boolean isChecked = true;
        for (JoinerEntity entity:joinerList){
            if(!entity.isChecked())isChecked = false;
            if(entity.getChildrens()!=null) isChecked = isAllChecked(entity.getChildrens());
            if(entity.getUserList()!=null) isChecked = isAllChecked(entity.getUserList());
        }
        return isChecked;
    }



    private void setShowChild(JoinerEntity entity, boolean showChild){
        entity.setShowChild(showChild);
        if(entity.getChildrens() ==null) return;
        for (JoinerEntity innerEntity : entity.getChildrens()) {
            setShowChild(innerEntity,showChild);
        }
    }

    //全选/取消全选
    private void setCheck(JoinerEntity entity, boolean checked){
        entity.setChecked(checked);
        if(entity.getChildrens() ==null) return;
        for (JoinerEntity innerEntity : entity.getChildrens()) {
            setCheck(innerEntity,checked);
        }

        for (JoinerEntity innerEntity : entity.getUserList()) {
            setCheck(innerEntity,checked);
        }
    }

    private void setResultData(List<JoinerEntity> list){
        if(list == null) return;
        for (JoinerEntity entity:list){
            if(entity.getChildrens()!=null) setResultData(entity.getChildrens());
            if(entity.getUserList()!=null) {
                for (JoinerEntity userEntity:entity.getUserList()){
                    if(userEntity.isChecked()){
                        ids += userEntity.getId() + ",";
                        name += userEntity.getName() + ",";
                        size ++;
                    }
                }

            }
        }
    }
}
