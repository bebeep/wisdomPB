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
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.NoteEntity;
import com.bebeep.wisdompb.databinding.ActivityMynoteBinding;
import java.util.ArrayList;
import java.util.List;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 笔记
 */
public class MyNoteActivity extends SlideBackActivity implements View.OnClickListener,OnPullListener,
        SwipeRefreshLayout.OnRefreshListener{

    private ActivityMynoteBinding binding;
    private CommonAdapter adapter;
    private List<NoteEntity> list = new ArrayList<>();

    private boolean showDelLayout = false,hasChooseAll = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_mynote);
        init();
    }

    private void init(){
        initAdapter();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("我的笔记");
        binding.title.tvRight.setVisibility(View.VISIBLE);
        binding.title.tvRight.setText("编辑");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
    }

    private void initAdapter(){
        list.add(new NoteEntity());
        list.add(new NoteEntity());
        list.add(new NoteEntity());
        list.add(new NoteEntity());
        adapter = new CommonAdapter<NoteEntity>(this, R.layout.item_mynote,list){
            @Override
            protected void convert(ViewHolder holder, final NoteEntity entity, int position) {
                holder.getConvertView().setClickable(showDelLayout);
                holder.setVisible(R.id.iv_choose, showDelLayout);
                holder.setImageResource(R.id.iv_choose, entity.isChoosed()?R.drawable.icon_choose_c:R.drawable.icon_choose_u);

                holder.setOnClickListener(R.id.iv_del, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!showDelLayout)MyTools.showToast(MyNoteActivity.this,"删除");

                    }
                });
                holder.setOnClickListener(R.id.iv_edit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!showDelLayout)MyTools.showToast(MyNoteActivity.this,"编辑");
                    }
                });

                holder.setOnClickListener(R.id.iv_choose, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        entity.setChoosed(!entity.isChoosed());
                        adapter.refresh(list);
                        binding.ivCheckAll.setImageResource(isCheckAll()?R.drawable.icon_checkall_c :R.drawable.icon_checkall_n);
                    }
                });



            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
               if(!showDelLayout)startActivity(new Intent(MyNoteActivity.this,BookContentActivity.class));
               else {
                   NoteEntity entity = list.get(position);
                   entity.setChoosed(!entity.isChoosed());
                   adapter.refresh(list);
               }
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
            case R.id.tv_right://编辑
                showDelLayout = !showDelLayout;
                binding.llDelAll.setVisibility(showDelLayout?View.VISIBLE:View.GONE);
                if(adapter!=null&&list!=null)adapter.refresh(list);
                break;
            case R.id.fl_del_all:
                hasChooseAll = !hasChooseAll;
                chooseAll(hasChooseAll);
                break;
            case R.id.tv_del:
                MyTools.showToast(this,"删除成功");
                showDelLayout = !showDelLayout;
                binding.llDelAll.setVisibility(showDelLayout?View.VISIBLE:View.GONE);
                adapter.refresh(list);
                break;
        }
    }


    private void chooseAll(boolean choose){
        if(list==null||list.size()==0)return;
        for (NoteEntity entity:list){
            entity.setChoosed(choose);
        }
        adapter.refresh(list);
        binding.ivCheckAll.setImageResource(choose?R.drawable.icon_checkall_c :R.drawable.icon_checkall_n);
    }

    /**
     * 是否全选
     * @return
     */
    private boolean isCheckAll(){
        boolean ischeckAll = false;
        if(list==null||list.size()==0)return ischeckAll;
        for (NoteEntity entity:list){
            if(!entity.isChoosed())return false;
        }
        return true;
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
                binding.nrl.onLoadFinished();
            }
        },500);
    }




    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件

                return false;
            } else {
                //使EditText触发一次失去焦点事件
                v.setFocusable(false);
                v.setFocusableInTouchMode(true);
                return true;
            }
        }
        return false;
    }
}
