package com.bebeep.wisdompb.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.CommonTypeEntity;
import com.bebeep.wisdompb.databinding.FragmentTestingBinding;
import com.bebeep.wisdompb.base.CommonFragment;

import java.util.ArrayList;
import java.util.List;

public class Fragment_Testing extends CommonFragment {

    private FragmentTestingBinding binding;
    private CommonAdapter adapter;
    private String uId = "";

    private final String[] itemIndex = {"A", "B", "C", "D", "E", "F"};

    private List<CommonTypeEntity> list = new ArrayList<>();

    public static Fragment_Testing newInstance(String uId) {
        Bundle args = new Bundle();
        args.putString("uId", uId);
        Fragment_Testing fragment = new Fragment_Testing();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uId = getArguments().getString("uId");
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
        initAdapter();
    }


    private void initAdapter() {
        list.add(new CommonTypeEntity());
        list.add(new CommonTypeEntity());
        list.add(new CommonTypeEntity());
        list.add(new CommonTypeEntity());
        list.add(new CommonTypeEntity());
        adapter = new CommonAdapter<CommonTypeEntity>(getActivity(), R.layout.item_testing, list) {
            @Override
            protected void convert(ViewHolder holder, CommonTypeEntity commonTypeEntity, int position) {
                holder.setText(R.id.tv_index, itemIndex[position]);
                holder.setText(R.id.tv_content, commonTypeEntity.getTitle());
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);
    }
}
