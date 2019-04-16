package com.bebeep.commontools.showbigimage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.R;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.views.CustomDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 查看大图
 */
public class ShowMultiBigImageDialog extends Dialog {

    private Dialog dialog;
    private View mView ;
    private Activity mContext;
    private ShowImagesViewPager mViewPager;
    private List<String> mImgUrls;
    private List<View> mViews;
    private TextView tv;
    private FrameLayout fl_parent;

    public ShowMultiBigImageDialog(@NonNull Activity context, List<String> imgUrls) {
        super(context, R.style.showMultiImagesDialog);
        this.mContext = context;//传入上下文
        this.mImgUrls = imgUrls;//传入图片String数组
        initView();
        initData();
    }

    private void initView() {
        dialog = new CustomDialog(mContext, R.style.showMultiImagesDialog);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.dialog_images_brower, null);
        mViewPager =  mView.findViewById(R.id.vp_images);//找到ViewPager控件并且实例化
        fl_parent =  mView.findViewById(R.id.fl_parent);//找到ViewPager控件并且实例化
        mViews = new ArrayList<>();//创建一个控件的数组，我们可以在ViewPager中加入很多图片，滑动改变图片
        tv = mView.findViewById(R.id.tv);

        fl_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    private void initData() {
        mViewPager.setAdapter(pagerAdapter);
        //设置滑动监听事件
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            //滑动到第几张图片的调用的方法，position当前显示图片位置
            @Override
            public void onPageSelected(int position) {
                mViewPager.setCurrentItem(position);
                tv.setText((position+1)+"/"+mImgUrls.size());
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        dialog.addContentView(mView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public void show(int position){
        tv.setText((position+1)+"/"+mImgUrls.size());
        mViewPager.setCurrentItem(position);
        dialog.show();

        WindowManager windowManager = mContext.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);


        applyCompat();
    }


    PagerAdapter pagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return mImgUrls.size();
            //return Integer.MAX_VALUE;    返回一个比较大的值，目的是为了实现无限轮播
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(mContext);
            Picasso.with(mContext)
                    .load(mImgUrls.get(position))
                    .into(photoView);
            container.addView(photoView);
            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    dialog.dismiss();
                }
            });
            return photoView;
        }
        //PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            container.removeView(mViews.get(position));
        }
    };



    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

}
