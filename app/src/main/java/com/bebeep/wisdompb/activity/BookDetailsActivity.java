package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.databinding.ActivityBookDetailsBinding;
import java.util.ArrayList;
import java.util.List;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 书籍详情
 */
public class BookDetailsActivity extends BaseEditActivity implements View.OnClickListener,OnPullListener,
        SwipeRefreshLayout.OnRefreshListener,TextWatcher{

    private ActivityBookDetailsBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_details);
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener,this);
        initAdapter();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("书籍详情");
        binding.etComment.addTextChangedListener(this);
        binding.etComment.setOnFocusChangeListener(onFocusChangeListener);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_send://发送评论
                binding.etComment.setText("");
                binding.etComment.clearFocus();
                binding.tvSend.setClickable(false);
                break;
        }
    }

    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(this,R.layout.item_partyact_details,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }


    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.srl.setRefreshing(false);
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
                list.add("");
                list.add("");
                adapter.refresh(list);
                binding.nrl.onLoadFinished();
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
            binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
        }
    };

}
