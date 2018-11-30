package com.bebeep.wisdompb.activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.view.View;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.showbigimage.DeviceConfig;
import com.bebeep.commontools.showbigimage.ShowMultiBigImageDialog;
import com.bebeep.commontools.showbigimage.ShowSingleBigImageDialog;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.CustomRoundAngleImageView;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityGalleryDetailBinding;
import java.util.ArrayList;
import java.util.List;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 党建相册
 */
public class GalleryActivity extends SlideBackActivity implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{
    private ActivityGalleryDetailBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery_detail);
        init();
    }

    private void init(){
        initDeviceDensity();
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
        list.add("http://b.hiphotos.baidu.com/image/h%3D300/sign=87021db3be1c8701c9b6b4e6177e9e6e/0d338744ebf81a4cf87e4f9eda2a6059252da61d.jpg");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698304325&di=93085e2f3efb8f0b5a47b3eb8c1bde63&imgtype=0&src=http%3A%2F%2Fpic31.nipic.com%2F20130731%2F13345615_081322573195_2.jpg");
        list.add("6");
        list.add("7");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698317839&di=bece7e5f8897859c4793ee723053216b&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201507%2F02%2F20150702160115_VFR2u.jpeg");
        list.add("");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698327640&di=be7b898f853940fd1adc09658c86d0fd&imgtype=0&src=http%3A%2F%2Fpic36.nipic.com%2F20131202%2F445991_145804524184_2.jpg");
        adapter = new CommonAdapter<String>(this,R.layout.item_gallery_detail,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                CustomRoundAngleImageView iv = holder.getView(R.id.iv);
                holder.setImageUrl(iv,s,R.drawable.bg_book);
            }
        };
        binding.recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                new ShowSingleBigImageDialog(GalleryActivity.this).show(list.get(position),R.drawable.bg_book); //单张图片
//                new ShowMultiBigImageDialog(GalleryActivity.this, list).show();
            }
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
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

    private void initDeviceDensity(){
        DisplayMetrics metrics = new DisplayMetrics();//通过DisplayMetrics类可以得到手机屏幕的参数
        getWindowManager().getDefaultDisplay().getMetrics(metrics);//然后将DisplayMetrics对象传入
        DeviceConfig.EXACT_SCREEN_HEIGHT = metrics.heightPixels;//得到手机屏幕的高的分辨率
        DeviceConfig.EXACT_SCREEN_WIDTH = metrics.widthPixels;//得到手机屏幕宽的分辨率
    }
}
