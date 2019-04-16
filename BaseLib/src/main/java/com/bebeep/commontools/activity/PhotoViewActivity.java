package com.bebeep.commontools.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bebeep.commontools.R;
import com.komi.slider.SliderConfig;
import com.komi.slider.SliderUtils;
import com.komi.slider.position.SliderPosition;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends FragmentActivity {

    private PhotoViewActivity activity;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private SliderConfig mConfig;
    private ArrayList<ImageView> mList;
    private TextView textView;
    private List<String> imgList = new ArrayList<>();
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photoview);
        activity = this;

        imgList = getIntent().getStringArrayListExtra("imgList");
        position = getIntent().getIntExtra("position",0);

        if(imgList==null||imgList.size()==0){
            finish();
            return;
        }

        initSilder();
        initView();
        viewPager.setCurrentItem(position);
        textView.setText((position+1)+"/"+imgList.size());
    }


    //实现界面上下滑动退出界面效果
    private void initSilder() {
        mConfig = new SliderConfig.Builder()
                .secondaryColor(Color.TRANSPARENT)
                .position(SliderPosition.VERTICAL)  //设置上下滑动
                .edge(false)  //是否允许有滑动边界值,默认是有的true
                .build();
//        SliderUtils.attachActivity(this, mConfig);
    }


    private void initView() {
        viewPager=findViewById(R.id.ViewPager);
        textView=findViewById(R.id.text);

        mList = new ArrayList<>();

        pagerAdapter = new PagerAdapter() {

            // 获取要滑动的控件的数量，在这里我们以滑动的广告栏为例，那么这里就应该是展示的广告图片的ImageView数量
            @Override
            public int getCount() {
                return imgList.size();

                //return Integer.MAX_VALUE;    返回一个比较大的值，目的是为了实现无限轮播
            }

            // 来判断显示的是否是同一张图片，这里我们将两个参数相比较返回即可
            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            // 当要显示的图片可以进行缓存的时候，会调用这个方法进行显示图片的初始化，
            // 我们将要显示的ImageView加入到ViewGroup中，然后作为返回值返回即可
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView photoView = new PhotoView(activity);
                Picasso.with(activity)
                        .load(imgList.get(position))
                        .into(photoView);
                container.addView(photoView);
                photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        activity.finish();
                    }
                });

                return photoView;
            }

            //PagerAdapter只缓存三张要显示的图片，如果滑动的图片超出了缓存的范围，就会调用这个方法，将图片销毁
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mList.get(position));
            }


        };

        viewPager.setAdapter(pagerAdapter);

        //设置滑动监听事件
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            //滑动到第几张图片的调用的方法，position当前显示图片位置
            @Override
            public void onPageSelected(int i) {
                position = i;
                viewPager.setCurrentItem(position);
                textView.setText((position+1)+"/"+imgList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}
