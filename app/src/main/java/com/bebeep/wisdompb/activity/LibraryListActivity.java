package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.TitleFragmentAdapter;
import com.bebeep.wisdompb.databinding.ActivityLibraryListBinding;
import com.bebeep.wisdompb.fragment.Fragment_LibraryList;

import java.util.ArrayList;
import java.util.List;


/**
 * 图书馆列表
 */
public class LibraryListActivity extends FragmentActivity {
    private ActivityLibraryListBinding binding;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<String> listTitle = new ArrayList<>();
    private TitleFragmentAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_library_list);
        init();

    }

    private void init(){
         binding.title.ivBack.setVisibility(View.VISIBLE);
         binding.title.tvTitle.setText(getIntent().getStringExtra("title"));
         binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 finish();
             }
         });

         initFragment();

    }


    private void initFragment(){
        fragmentList.add(new Fragment_LibraryList().newInstance("1"));
        fragmentList.add(new Fragment_LibraryList().newInstance("2"));
        fragmentList.add(new Fragment_LibraryList().newInstance("3"));
        fragmentList.add(new Fragment_LibraryList().newInstance("4"));
        listTitle.add("学者专家");
        listTitle.add("历史人物");
        listTitle.add("古代帝王");
        listTitle.add("杰出人才");
        adapter = new TitleFragmentAdapter(getSupportFragmentManager(), fragmentList, listTitle);
        binding.vpFindFragmentPager.setAdapter(adapter);
        binding.tabFindFragmentTitle.setupWithViewPager(binding.vpFindFragmentPager);
    }
}

