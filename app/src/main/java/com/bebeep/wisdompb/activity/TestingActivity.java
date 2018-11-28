package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.AnswerEntity;
import com.bebeep.wisdompb.databinding.ActivityTestingBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 考试进行中
 */
public class TestingActivity extends Activity implements View.OnClickListener{
    private ActivityTestingBinding binding;
    private CommonAdapter adapter1,adapter2;
    private List<String> list1 = new ArrayList<>();
    private List<AnswerEntity> list2 = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_testing);
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener, this);
        binding.title.tvTitle.setText("在线考试");
        initAdapter1();
        initAdapter2();
    }


    private void initAdapter1(){
        for (int i=0;i<10;i++) list1.add("");
        adapter1 = new CommonAdapter<String>(this,R.layout.item_exam_top,list1){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.setText(R.id.num,String.valueOf(position+1));
                holder.setBackgroundRes(R.id.num, position == 0?R.drawable.bg_rec_dashline_2dp:R.drawable.bg_rec_2dp_red);
                holder.setTextColor(R.id.num,getResources().getColor(position == 0? R.color.c_gray:R.color.c_white));

            }
        };

        binding.recyclerView1.setLayoutManager(new GridLayoutManager(this,8));
        binding.recyclerView1.setAdapter(adapter1);
    }


    private void initAdapter2(){
        list2.add(new AnswerEntity());
        list2.add(new AnswerEntity());
        list2.add(new AnswerEntity());
        list2.add(new AnswerEntity());
        adapter2 = new CommonAdapter<AnswerEntity>(this,R.layout.item_exam_bottom,list2){
            @Override
            protected void convert(ViewHolder holder, AnswerEntity entity, int position) {
                holder.setImageResource(R.id.iv_check, entity.isChecked()?R.drawable.bg_ring_exam_c:R.drawable.bg_ring_exam_u);
                holder.setBackgroundRes(R.id.fl_parent,entity.isChecked()?R.drawable.bg_exam_anwser_c:R.drawable.bg_exam_anwser);
                holder.setTextColor(R.id.tv_anwser,getResources().getColor(entity.isChecked()?R.color.c_orange:R.color.c_sblack));

            }
        };
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView2.setAdapter(adapter2);
        adapter2.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                AnswerEntity entity = list2.get(position);
                entity.setChecked(!entity.isChecked());
                adapter2.refresh(list2);
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
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_last://上一题

                break;
            case R.id.tv_next://下一题
                startActivityForResult(new Intent(this,TestResultActivity.class), MyApplication.ACTIVITY_BACK_CODE);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == MyApplication.ACTIVITY_BACK_CODE){
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
