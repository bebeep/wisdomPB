package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.views.ChoosePicPopWindow;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.databinding.ActivityOrderMeetingBinding;
import com.bebeep.wisdompb.util.PicassoImageLoader;

import java.util.ArrayList;
import java.util.List;

import cc.dagger.photopicker.PhotoPicker;
import cc.dagger.photopicker.picker.Load;
import cc.dagger.photopicker.picker.PhotoFilter;
import cc.dagger.photopicker.picker.PhotoSelectBuilder;


/**
 * 预约会议
 */
public class OrderMeetingActivity extends BaseEditActivity implements View.OnClickListener{
    private ActivityOrderMeetingBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;
    private ChoosePicPopWindow popWindow;
    private ArrayList<String> mSelectPath = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_meeting);
        init();
    }


    private void init(){
        initAdapter();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("预约会议");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_choose_date://选择会议日期

                break;
            case R.id.ll_choose_start_time://选择开始时间

                break;
            case R.id.ll_choose_end_time://选择结束时间

                break;
            case R.id.ll_choose_address://选择地点

                break;
            case R.id.fl_add_time_address://添加会议时间地点

                break;
            case R.id.iv_file://添加附件

                break;
            case R.id.iv_file_del://删除附件

                break;
            case R.id.tv_save://保存草稿
                MyTools.showToast(this,"保存成功");
                finish();
                break;
            case R.id.tv_release://发布
                MyTools.showToast(this,"发布成功");
                finish();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == PhotoPicker.REQUEST_SELECTED) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(PhotoPicker.EXTRA_RESULT);
                Log.e("TAG","mSelectPath:"+ MyApplication.gson.toJson(mSelectPath));
                if(mSelectPath!=null && mSelectPath.size()>0){
                    list.removeAll(list);
                    list.addAll(mSelectPath);
                    list.add("6");
                    adapter.refresh(list);
                }
            }
        }
    }



    private void initAdapter(){
        list.add("1");
        adapter = new CommonAdapter<String>(this,R.layout.item_choose_pic,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                if(position == list.size()-1)holder.setImageResource(R.id.iv,R.drawable.icon_pic_add);
                else{
                    holder.setImageUrl((ImageView) holder.getView(R.id.iv),"file://"+s,R.drawable.default_error,90,90);
                }
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if(position == list.size()-1){
                    pickImage();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }


    private void pickImage() {
//      调用相册中的相片，用Picasso展示
        PhotoPicker.init(new PicassoImageLoader(), null);
//      启用相机
        boolean showCamera = true;
//      最大选择5张
        int maxNum = 5;
//      显示3列
        int columns = 3;
        Load load = PhotoPicker.load()
                .showCamera(showCamera)
                .filter(PhotoFilter.build().showGif(false).minSize(2 * 1024))
                .gridColumns(columns);
//      传入最大选择的数量，和路径
        PhotoSelectBuilder builder = load.multi().maxPickSize(maxNum).selectedPaths(mSelectPath);

        //单选
//        PhotoSelectBuilder builder = load.single();
        builder.start(this);
    }
}
