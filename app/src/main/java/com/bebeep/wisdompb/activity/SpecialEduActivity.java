package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.TitleFragmentAdapter;
import com.bebeep.wisdompb.databinding.ActivitySpecialEducationBinding;
import com.bebeep.wisdompb.fragment.Fragment_LibraryList;
import com.bebeep.wisdompb.fragment.Fragment_SpecialEdu;

import java.util.ArrayList;
import java.util.List;


/**
 * 专题教育
 */
public class SpecialEduActivity extends FragmentActivity {
    private ActivitySpecialEducationBinding binding;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();
    private TitleFragmentAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_special_education);
        init();
    }

    private void init(){
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("专题教育");
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initFragment();
    }


    private void initFragment(){
        fragmentList.add(new Fragment_SpecialEdu().newInstance("1"));
        fragmentList.add(new Fragment_SpecialEdu().newInstance("2"));
        fragmentList.add(new Fragment_SpecialEdu().newInstance("3"));
        fragmentList.add(new Fragment_SpecialEdu().newInstance("4"));
        listTitle.add("全部");
        listTitle.add("两学一做");
        listTitle.add("三会一课");
        listTitle.add("一带一路");
        adapter = new TitleFragmentAdapter(getSupportFragmentManager(), fragmentList, listTitle);
        binding.vpFindFragmentPager.setAdapter(adapter);
        binding.tabFindFragmentTitle.setupWithViewPager(binding.vpFindFragmentPager);
    }
}
