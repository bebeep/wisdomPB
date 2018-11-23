package com.bebeep.wisdompb.fragment;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.NetworkImageHolderView;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.AddressBookActivity;
import com.bebeep.wisdompb.activity.ChargeActivity;
import com.bebeep.wisdompb.activity.LibraryTypeActivity;
import com.bebeep.wisdompb.activity.MainActivity;
import com.bebeep.wisdompb.activity.NewsDetailActivity;
import com.bebeep.wisdompb.activity.NoticeActivity;
import com.bebeep.wisdompb.activity.PartyActActivity;
import com.bebeep.wisdompb.activity.PublicShowActivity;
import com.bebeep.wisdompb.activity.UserInfoActivity;
import com.bebeep.wisdompb.databinding.Fragment1Binding;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.squareup.picasso.Picasso;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

import static android.content.ContentValues.TAG;

public class Fragment1 extends Fragment implements OnPullListener,SwipeRefreshLayout.OnRefreshListener,View.OnClickListener{
    private MainActivity mainActivity;
    private Fragment1Binding binding;
    private LinearLayout[] menus ;


    private List<String> imgList = new ArrayList<>();
    private CommonAdapter adapter;
    private List<String> list = new ArrayList<>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(binding == null){
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment1,container,false);
            init();
        }
        return binding.getRoot();
    }


    private void init(){
        initAdapter();
        initBanner();
        initHead("111");
        initMenus();

        mainActivity = (MainActivity) getActivity();
        mainActivity.addIgnoredView(binding.banner);
        mainActivity.addIgnoredView(binding.hs);

        binding.setVariable(BR.onClickListener,this);
        binding.title.flHead.setOnClickListener(this);
        binding.title.flHead.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setVisibility(View.VISIBLE);
        binding.title.ivTitleRight.setImageResource(R.drawable.icon_search);
        binding.title.tvTitle.setText("智慧党建");
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setOnLoadingListener(this);

    }




    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //主界面
            case R.id.fl_head://点击头像
                mainActivity.showMenu();
                break;
            case R.id.iv_title_right:
                MyTools.showToast(getActivity(),"search!");
                break;
            case R.id.ll_f1_t1://图书馆
                startActivity(new Intent(getActivity(), LibraryTypeActivity.class));
                break;
            case R.id.ll_f1_t2://党组织活动
                startActivity(new Intent(getActivity(), PartyActActivity.class));
                break;
            case R.id.ll_f1_t3://党内公示
                startActivity(new Intent(getActivity(), PublicShowActivity.class));
                break;
            case R.id.ll_f1_t4://党费缴纳
                startActivity(new Intent(getActivity(), ChargeActivity.class));
                break;
            case R.id.ll_f1_t5://党建通讯录
                startActivity(new Intent(getActivity(), AddressBookActivity.class));
                break;
            case R.id.ll_f1_t6://党建相册

                break;
            case R.id.ll_f1_t7://通知公告
                startActivity(new Intent(getActivity(), NoticeActivity.class));
                break;
        }
    }

    private void initMenus(){
        menus = new LinearLayout[]{binding.llF1T1,binding.llF1T2,binding.llF1T3,binding.llF1T4,binding.llF1T5,binding.llF1T6,binding.llF1T7};
        for (LinearLayout linearLayout:menus){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            params.width = MyTools.getWidth(getActivity()) / 4;
            linearLayout.setLayoutParams(params);
        }
    }

    private void initHead(String path){
        Picasso.with(getActivity()).load(path + "")
                .placeholder(R.drawable.icon_head)
                .config(Bitmap.Config.RGB_565)
                .error(R.drawable.icon_head)
                .into(binding.title.rimgHead);
    }

    private void initAdapter(){
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        list.add("");
        adapter = new CommonAdapter<String>(getActivity(),R.layout.item_f1,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {

            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                startActivity(new Intent(getActivity(),NewsDetailActivity.class).putExtra("title","党建要闻"));
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }



    //初始化轮播图
    private void initBanner(){
        imgList.add("http://b.hiphotos.baidu.com/image/h%3D300/sign=87021db3be1c8701c9b6b4e6177e9e6e/0d338744ebf81a4cf87e4f9eda2a6059252da61d.jpg");
        imgList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698304325&di=93085e2f3efb8f0b5a47b3eb8c1bde63&imgtype=0&src=http%3A%2F%2Fpic31.nipic.com%2F20130731%2F13345615_081322573195_2.jpg");
        imgList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698317839&di=bece7e5f8897859c4793ee723053216b&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201507%2F02%2F20150702160115_VFR2u.jpeg");
        imgList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1542698327640&di=be7b898f853940fd1adc09658c86d0fd&imgtype=0&src=http%3A%2F%2Fpic36.nipic.com%2F20131202%2F445991_145804524184_2.jpg");
        binding.banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
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

    public void startPlay(){
        if(binding.banner!=null) {
            binding.banner.startAutoPlay();
        }
    }

    public void stopPlay(){
        if(binding.banner!=null) {
            binding.banner.stopAutoPlay();
        }
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
                list.add("");
                adapter.refresh(list);
                binding.nrl.onLoadFinished();
            }
        },300);
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.srl.setRefreshing(false);
            }
        },300);
    }
}
