package com.bebeep.wisdompb.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ItemViewDelegate;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.FragmentSpecialEduBinding;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

public class Fragment_SpecialEdu extends Fragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener{

    private FragmentSpecialEduBinding binding;

    public static Fragment_SpecialEdu newInstance(String uId) {
        Bundle args = new Bundle();
        args.putString("uId", uId);
        Fragment_SpecialEdu fragment = new Fragment_SpecialEdu();
        fragment.setArguments(args);
        return fragment;
    }


    private List<String> list = new ArrayList<>();
    private List<String> imgList = new ArrayList<>();
    private MultiItemTypeAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding ==null ){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_special_edu,container,false);
            init();
        }
        return binding.getRoot();
    }

    private void init(){
        initAdapter();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);
    }


    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");

        adapter = new MultiItemTypeAdapter(getActivity(),list);
        adapter.addItemViewDelegate(new ItemViewDelegate<String>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_special_edu_top;
            }
            @Override
            public boolean isForViewType(String s, int position) {
                return position == 0;
            }

            @Override
            public void convert(ViewHolder holder, String s, int position) {
                Banner banner = holder.getView(R.id.banner);
                initBanner(banner);
            }
        });
        adapter.addItemViewDelegate(new ItemViewDelegate<String>() {
            @Override
            public int getItemViewLayoutId() {
                return R.layout.item_special_edu_bottom;
            }
            @Override
            public boolean isForViewType(String s, int position) {
                return position != 0;
            }

            @Override
            public void convert(ViewHolder holder, String s, int position) {
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);

    }



    //初始化轮播图
    private void initBanner(Banner banner){
        imgList.add("http://b.hiphotos.baidu.com/image/h%3D300/sign=87021db3be1c8701c9b6b4e6177e9e6e/0d338744ebf81a4cf87e4f9eda2a6059252da61d.jpg");
        imgList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698304325&di=93085e2f3efb8f0b5a47b3eb8c1bde63&imgtype=0&src=http%3A%2F%2Fpic31.nipic.com%2F20130731%2F13345615_081322573195_2.jpg");
        imgList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698317839&di=bece7e5f8897859c4793ee723053216b&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201507%2F02%2F20150702160115_VFR2u.jpeg");
        imgList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698327640&di=be7b898f853940fd1adc09658c86d0fd&imgtype=0&src=http%3A%2F%2Fpic36.nipic.com%2F20131202%2F445991_145804524184_2.jpg");
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setImages(imgList)
                .setBannerAnimation(Transformer.Default)
                .setBannerTitles(imgList)
                .setDelayTime(2000)
                .isAutoPlay(true)
                .setIndicatorGravity(BannerConfig.RIGHT)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        if(TextUtils.isEmpty((CharSequence) path)){
                            Picasso.with(context).load(com.bebeep.commontools.R.drawable.icon_error).into(imageView);
                        }else{
                            Picasso.with(context).load(path + "")
                                    .placeholder(com.bebeep.commontools.R.drawable.defaultpic)
                                    .config(Bitmap.Config.RGB_565)
                                    .error(com.bebeep.commontools.R.drawable.icon_error)
                                    .into(imageView);
                        }
                    }
                })
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(int position) {
                        Log.e("TAG","position:"+position);
                    }
                })
                .start();

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
