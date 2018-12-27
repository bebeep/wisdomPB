package com.bebeep.wisdompb.fragment;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.TestingActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.bean.TestingEntity;
import com.bebeep.wisdompb.bean.TestingItemEntity;
import com.bebeep.wisdompb.databinding.FragmentTestingBinding;
import com.bebeep.wisdompb.base.CommonFragment;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment_Testing extends CommonFragment {
    private TestingActivity activity;
    private FragmentTestingBinding binding;
    private CommonAdapter adapter;
    private TestingItemEntity entity;

    private final String[] itemIndex = {"A", "B", "C", "D", "E", "F","G","H","I","J"};

    private List<CommonTypeEntity> list = new ArrayList<>();
    private String multiAnswersId = "";

    public static Fragment_Testing newInstance(TestingItemEntity entity) {
        Bundle args = new Bundle();
        args.putSerializable("entity", entity);
        Fragment_Testing fragment = new Fragment_Testing();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (TestingActivity) getActivity();
        entity = (TestingItemEntity) getArguments().getSerializable("entity");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_testing, container, false);
            init();
        }
        return binding.getRoot();
    }


    private void init() {
        if(entity == null) return;
        list = entity.getItemBankAnswerList();
        binding.tvTitle.setText(entity.getTitle());
        binding.tvSure.setVisibility(entity.getType() == 1?View.VISIBLE:View.GONE);
        binding.tvType.setText(getType());
        String pre = (entity.getIndex()+1)+"."+entity.getTitle();
        splitTextColor(binding.tvTitle,pre+"("+getType()+","+entity.getFractionNum()+"分)", pre.length());
        initAdapter();


        binding.tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entity.setHasChecked(true);
                entity.setShowAnwsers(true);
                adapter.refresh(list);
                checkAnswer(multiAnswersId);
                binding.tvSure.setVisibility(View.GONE);
                activity.setResult(checkResult());
                entity.setItemBankAnswerList(list);
            }
        });
    }




    private void initAdapter() {
        adapter = new CommonAdapter<CommonTypeEntity>(getActivity(), R.layout.item_testing, list) {
            @Override
            protected void convert(final ViewHolder holder, final CommonTypeEntity commonTypeEntity, int position) {
                holder.setText(R.id.tv_index, itemIndex[position]);
                holder.setText(R.id.tv_content, commonTypeEntity.getTitle());

                if(entity.isHasChecked()){//用户已经做过这道题了
                    holder.setEnable(R.id.ll_parent,false);
                    holder.setVisible(R.id.tv_index, false);
                    holder.setVisible(R.id.iv_result,true);
                    binding.tvSure.setVisibility(View.GONE);
                    if(commonTypeEntity.isChecked()){//用户选择了该选项-判断正误
                        holder.setImageResource(R.id.iv_result, commonTypeEntity.getIsCorrect()==1?R.drawable.icon_testing_right:R.drawable.icon_testing_wrong);
                        holder.setTextColor(R.id.tv_content, getResources().getColor(commonTypeEntity.getIsCorrect()==1?R.color.green:R.color.theme));
                        holder.setBackgroundColor(R.id.ll_parent, getResources().getColor(entity.getType() == 1 && commonTypeEntity.isChecked() ?R.color.c_line:R.color.c_white));

                    }else { //显示正确答案
                        if(commonTypeEntity.getIsCorrect()==1){
                            holder.setImageResource(R.id.iv_result, R.drawable.icon_testing_right);
                            holder.setTextColor(R.id.tv_content, getResources().getColor(R.color.green));
                        }else{
                            holder.setTextColor(R.id.tv_content, getResources().getColor(R.color.c_sblack));
                            holder.setVisible(R.id.tv_index, true);
                            holder.setVisible(R.id.iv_result,false);
                        }
                    }
                }else { //还没做过这道题
                    holder.setTextColor(R.id.tv_content, getResources().getColor(R.color.c_sblack));
                    holder.setEnable(R.id.ll_parent,true);
                    holder.setVisible(R.id.tv_index, true);
                    holder.setVisible(R.id.iv_result,false);
                    holder.setBackgroundColor(R.id.ll_parent, getResources().getColor(entity.getType() == 1 && commonTypeEntity.isChecked() ?R.color.c_line:R.color.c_white));
                }

                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(entity.getType() == 1){//多选
                            commonTypeEntity.setChecked(!commonTypeEntity.isChecked());
                            adapter.refresh(list);
                            setCheckable();
                        }else{ //单选
                            commonTypeEntity.setChecked(true);
                            entity.setHasChecked(true);
                            entity.setShowAnwsers(true);
                            adapter.refresh(list);
                            activity.setResult(commonTypeEntity.getIsCorrect()==1);
                            checkAnswer(commonTypeEntity.getId());
                            entity.setItemBankAnswerList(list);
                        }
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
    }


    private void setCheckable(){
        if(list == null) return;
        boolean canCheck = false;
        multiAnswersId = "";
        for (CommonTypeEntity commonTypeEntity:list){
            if(commonTypeEntity.isChecked()) {
                canCheck = true;
                multiAnswersId += commonTypeEntity.getId()+",";
            }

        }
        if(!TextUtils.isEmpty(multiAnswersId)&&multiAnswersId.length()>0) multiAnswersId = multiAnswersId.substring(0,multiAnswersId.length()-1);
        binding.tvSure.setBackgroundResource(canCheck?R.drawable.bg_rec_15dp_green:R.drawable.bg_rec_15dp_gray);
    }


    private boolean checkResult(){
        boolean result = true;
        for (CommonTypeEntity commonTypeEntity:list){
            if(commonTypeEntity.getIsCorrect()==1){
                if(!commonTypeEntity.isChecked()) result = false;
            }
        }

        return result;
    }


    private void checkAnswer(String ids){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("answersheetId", activity.getId());
        map.put("templateId", activity.getTestingEntity().getTemplateId());
        map.put("itemBankId", entity.getItemBankId());
        map.put("choiceIds", ids); //选择的选项的id
        map.put("currNum", String.valueOf(entity.getIndex() + 1));
        LogUtil.showLog("验证答案："+map.toString());
        OkHttpClientManager.postAsyn(URLS.EXAM_CHECK_ANSWER, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                activity.nextItem();
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("验证答案："+ response);
//                if(response.isSuccess()){
//
//                }else{
//                    MyTools.showToast(getActivity(), response.getMsg());
//                }
            }
        },header,map);
    }


    private void splitTextColor(TextView tv, String content, int splitIndex){
        SpannableString spannableString = new SpannableString(content);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3B3937")), 0, splitIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#8e8e8e")), splitIndex,spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
    }


    private String getType(){
        switch (entity.getType()){
            case 0:
                return "单选题";
            case 1:
                return "多选题";
            case 2:
                return "判断题";
            case 3:
                return "简答题";
        }
        return "";
    }
}
