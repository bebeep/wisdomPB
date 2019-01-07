package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.commontools.views.PopWindow;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.TabFragmentPagerAdapter;
import com.bebeep.wisdompb.base.BaseFragmentActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.TestingEntity;
import com.bebeep.wisdompb.bean.TestingItemEntity;
import com.bebeep.wisdompb.databinding.ActivityTestingBinding;
import com.bebeep.wisdompb.fragment.Fragment_Testing;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 考试进行中
 */
public class TestingActivity extends BaseFragmentActivity implements View.OnClickListener{
    private ActivityTestingBinding binding;
    private List<Fragment> fragments = new ArrayList<>();

    private List<TestingItemEntity> list = new ArrayList<>();

    private String id = "",title ="";
    private TabFragmentPagerAdapter adapter;
    private TestingEntity entity;
    private int index = 0;
    private CustomDialog customDialog;

    private PopWindow popWindow;
    private TextView tv_right ,tv_wrong,tv_num ;
    private CommonAdapter bottomAdapter;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    setTime(msg);
                    break;
                case 2:
                    MyTools.showToast(TestingActivity.this,"考试时间到");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing);
        init();
    }

    private void init(){
        initDialog();
        binding.setVariable(BR.onClickListener, this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("在线考试");
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        getData();



    }


    private void initUI(){
        Message msg = new Message();
        setTime(msg);
        binding.tvTopNum.setText("1/"+entity.getBizList().size());
        binding.tvNum.setText("1/"+entity.getBizList().size());
        if(tv_num!=null)tv_num.setText("1/"+entity.getBizList().size());
        list = entity.getBizList();
        String listJson = PreferenceUtils.getPrefString("testingItemList"+id,"");
        if(!TextUtils.isEmpty(listJson)){
            List<TestingItemEntity> savedList = MyApplication.gson.fromJson(listJson,new TypeToken<List<TestingItemEntity>>(){}.getType());
            LogUtil.showLog("savedList:"+MyApplication.gson.toJson(savedList));
            if(savedList!=null){
                index = PreferenceUtils.getPrefInt("testingItemIndex"+id,0);
                list = savedList;
                setNum();
            }
        }
        initFrgament();
        initPopWindow();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.iv_back:
                finish();
                break;
            case  R.id.tv_submit: //交卷
                customDialog.show();
                break;
            case  R.id.fl_num: //预览所有题目
                if(popWindow!=null){
                    popWindow.showAtLocation(binding.llBottom, Gravity.BOTTOM,0,0);
                    bottomAdapter.refresh(list);
                    setNum();
                }
                break;
        }
    }



    public String getId(){
        return id;
    }

    public TestingEntity getTestingEntity(){
        return entity;
    }

    public void nextItem(){
        int currentIndex = binding.viewpager.getCurrentItem();
        if(currentIndex==fragments.size())return;
        binding.viewpager.setCurrentItem(currentIndex+1);
    }

    public void setResult(boolean right){
        int position = binding.viewpager.getCurrentItem();
        TestingItemEntity entity = list.get(position);
        entity.setRight(right);
        LogUtil.showLog("setResult entity:"+MyApplication.gson.toJson(entity));
        list.set(position,entity);
        PreferenceUtils.setPrefString("testingItemList"+id, MyApplication.gson.toJson(list));
        setNum();
        binding.viewpager.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextItem();
            }
        },300);

        //

    }



    private void setNum(){
        int rightNum = 0;
        int wrongNum = 0;
        for (TestingItemEntity testingItemEntity:list){
            if(testingItemEntity.isHasChecked()){
                if(testingItemEntity.isRight()) rightNum ++;
                else wrongNum ++;
            }
        }
        binding.tvRight.setText(String.valueOf(rightNum));
        binding.tvWrong.setText(String.valueOf(wrongNum));
        if(tv_right!=null)tv_right.setText(String.valueOf(rightNum));
        if(tv_wrong!=null)tv_wrong.setText(String.valueOf(wrongNum));
    }

    /**
     * 初始化fragment
     */
    private void initFrgament() {
        if (list == null)return;
        for (int i=0;i<list.size(); i++) {
            TestingItemEntity entity = list.get(i);
            entity.setIndex(i);
            Fragment fragment = new Fragment_Testing().newInstance(entity);
            fragments.add(fragment);
        }
        adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setCurrentItem(index);
        binding.tvTopNum.setText((index+1)+"/"+entity.getBizList().size());
        binding.tvNum.setText((index+1)+"/"+entity.getBizList().size());
        if(tv_num!=null)tv_num.setText((index+1)+"/"+entity.getBizList().size());
        binding.viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }
            @Override
            public void onPageSelected(int i) {
                index = i;
                binding.tvTopNum.setText((i+1)+"/"+entity.getBizList().size());
                binding.tvNum.setText((i+1)+"/"+entity.getBizList().size());
                if(tv_num!=null)tv_num.setText((i+1)+"/"+entity.getBizList().size());
                PreferenceUtils.setPrefInt("testingItemIndex"+id, list.get(i).getIndex());
            }
            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
    }





    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("answersheetId",id);
        LogUtil.showLog("header:"+header);
        LogUtil.showLog("map:"+map);
        OkHttpClientManager.postAsyn(URLS.EXAM_TESTING, new OkHttpClientManager.ResultCallback<BaseObject<TestingEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject<TestingEntity> response) {
                LogUtil.showLog("开始考试："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity =  response.getData();
                    if(entity == null) {
                        MyTools.showToast(TestingActivity.this,"题目信息有误");
                        finish();
                    }
                    initUI();
                }else{
                    MyTools.showToast(TestingActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    private void setTime(Message msg){
        if(entity == null) return;
        long endTime = entity.getEndTime();
        long currentTime = System.currentTimeMillis();
        String time ;
        long deltaTime = (endTime - currentTime)/1000; //秒
        int day = (int) (deltaTime / (3600 * 24));
        int hour = (int) ((deltaTime % (3600 * 24)) / 3600);
        int min = (int) (deltaTime%3600/ 60);
        int mill = (int) (deltaTime % 60);
        if(hour == 0 && min == 0 && mill ==0){//考试结束
            msg.what = 2;
            binding.tvTime.setText("考试结束");
        }else{
            msg.what = 1;
            time = (day<10?"0"+day:String.valueOf(day)) + "天" +(hour<10?"0"+hour:String.valueOf(hour)) + "小时"+ (min<10?"0"+min:String.valueOf(min)) + "分" +(mill<10?"0"+mill:String.valueOf(mill))+"秒";
            binding.tvTime.setText(time);
        }
        handler.sendEmptyMessageDelayed(msg.what,1000);
    }


    private void initDialog(){
        customDialog = new CustomDialog.Builder(this)
                .setMessage("您确定要交卷吗")
                .setNegativeButton("检查一下", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                    }
                }).setPositiveButton("确定交卷", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                        PreferenceUtils.setPrefString("testingItemList"+id,"");
                        PreferenceUtils.setPrefString("testingItemIndex"+id,"");
                        Intent intent = new Intent();
                        intent.setClass(TestingActivity.this, TestResultActivity.class);
                        intent.putExtra("templateId",entity.getTemplateId());
                        intent.putExtra("title",title);
                        startActivityForResult(intent,MyApplication.ACTIVITY_BACK_CODE);

                    }
                })
                .createTwoButtonDialog();
    }


    private void initPopWindow(){
        View view = LayoutInflater.from(this).inflate(R.layout.pop_exam,null);
        popWindow = new PopWindow(this, view, MyTools.getWidth(this),MyTools.getHight(this) /2);
        TextView tvSure = view.findViewById(R.id.tv_submit);
        tv_right = view.findViewById(R.id.tv_right);
        tv_wrong = view.findViewById(R.id.tv_wrong);
        tv_num = view.findViewById(R.id.tv_num);
        tv_num.setText((index+1)+"/"+entity.getBizList().size());

        FrameLayout fl_num = view.findViewById(R.id.fl_num);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        initAdapter(recyclerView);
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
                customDialog.show();
            }
        });

        fl_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popWindow.dismiss();
            }
        });


    }

    private void initAdapter(RecyclerView recyclerView){
        bottomAdapter = new CommonAdapter<TestingItemEntity>(this,R.layout.item_testing_bottom,list){
            @Override
            protected void convert(ViewHolder holder, TestingItemEntity entity, final int position) {
                holder.setText(R.id.tv_index,String.valueOf(entity.getIndex()+1));

                if(entity.isHasChecked()){
                    holder.setTextColor(R.id.tv_index,  getResources().getColor(entity.isRight()?R.color.green:R.color.theme));
                    if(position == index)holder.setBackgroundRes(R.id.tv_index, entity.isRight()?R.drawable.bg_ring_exam_current_r:R.drawable.bg_ring_exam_current_w);
                    else holder.setBackgroundRes(R.id.tv_index, entity.isRight()? R.drawable.bg_ring_exam_right:R.drawable.bg_ring_exam_wrong);
                }
                else {
                    holder.setBackgroundRes(R.id.tv_index, position == index?R.drawable.bg_ring_exam_current_u:R.drawable.bg_ring_exam_u);
                    holder.setTextColor(R.id.tv_index,  getResources().getColor(R.color.c_gray));
                }

                holder.setOnClickListener(R.id.tv_index, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.viewpager.setCurrentItem(position);
                        popWindow.dismiss();
                    }
                });
            }
        };
        GridLayoutManager manager = new GridLayoutManager(this,6);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(bottomAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.showLog("testingActivity resultCode:"+resultCode+"|requestCode"+requestCode);
        if(resultCode == RESULT_OK){
            if(requestCode == MyApplication.ACTIVITY_BACK_CODE){
                setResult(RESULT_OK);
                finish();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
