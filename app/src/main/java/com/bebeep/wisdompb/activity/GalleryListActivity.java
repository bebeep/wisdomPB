package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.BitmapUtils;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomRoundAngleImageView;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityGalleryBinding;
import com.zhouwei.blurlibrary.EasyBlur;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;


/**
 * 党建相册列表
 */
public class GalleryListActivity extends SlideBackActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private ActivityGalleryBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        init();
    }

    private void init(){

        initAdapter();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.tvTitle.setText("党建相册");
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(this,R.layout.item_gallery,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                CustomRoundAngleImageView iv = holder.getView(R.id.iv3);
                initBlurImage("http://b.hiphotos.baidu.com/image/h%3D300/sign=87021db3be1c8701c9b6b4e6177e9e6e/0d338744ebf81a4cf87e4f9eda2a6059252da61d.jpg",iv);
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(GalleryListActivity.this,GalleryActivity.class));
            }
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }


    /**
     * 将网络图片模糊处理
     */
    private void initBlurImage(final String url, final ImageView iv){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap sourceImg = BitmapUtils.returnBitMap(url);
                if(sourceImg!=null){
                    final Bitmap finalBitmap = EasyBlur.with(GalleryListActivity.this)
                            .bitmap(sourceImg) //要模糊的图片
                            .radius(2)//模糊半径
                            .scale(4)//指定模糊前缩小的倍数
//                .policy(EasyBlur.BlurPolicy.FAST_BLUR)//使用fastBlur
                            .policy(EasyBlur.BlurPolicy.RS_BLUR)//使用fastBlur
                            .blur();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalBitmap!=null)iv.setImageBitmap(finalBitmap);
                        }
                    });
                }
            }
        }).start();

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
}
