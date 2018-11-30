package com.bebeep.commontools.showbigimage;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bebeep.commontools.R;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.PicassoUtil;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class ShowMultiBigImageDialog extends Dialog {

    private View mView ;
    private Context mContext;
    private ShowImagesViewPager mViewPager;
    private List<String> mImgUrls;
    private List<View> mViews;
    private ShowImagesAdapter mAdapter;

    public ShowMultiBigImageDialog(@NonNull Context context, List<String> imgUrls) {
        super(context, R.style.BigPicDialog);
        this.mContext = context;//传入上下文
        this.mImgUrls = imgUrls;//传入图片String数组
        initView();
        initData();
    }

    private void initView() {
        mView = View.inflate(mContext, R.layout.dialog_images_brower, null);//通过inflate()方法找到我们写好的包含ViewPager的布局文件
        mViewPager = (ShowImagesViewPager) mView.findViewById(R.id.vp_images);//找到ViewPager控件并且实例化
        mViews = new ArrayList<>();//创建一个控件的数组，我们可以在ViewPager中加入很多图片，滑动改变图片
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        Window window = getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = 0;
        wl.height =  DeviceConfig.EXACT_SCREEN_HEIGHT;
        wl.width = DeviceConfig.EXACT_SCREEN_WIDTH;
        wl.gravity = Gravity.CENTER;
        window.setAttributes(wl);
        //EXACT_SCREEN_HEIGHT,EXACT_SCREEN_WIDTH为自定义Config类中的静态变量
        //在MainActivity中会给这两个变量赋值，分别对应手机屏幕高度和宽度
        //在这里我们通过WindowManager.LayoutParams把当前dialog的大小设置为全屏
    }

    private void initData() {
        //当PhotoView被点击时，添加相应的点击事件
        PhotoViewAttacher.OnPhotoTapListener listener = new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                dismiss();//点击图片后，返回到原来的界面
            }
        };
        for (int i = 0; i < mImgUrls.size(); i++) {
            final PhotoView photoView = new uk.co.senab.photoview.PhotoView(mContext);
            //创建一个PhotoView对象
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            photoView.setLayoutParams(layoutParams);
            //我们通过ViewGroup.LayoutParams来设置子控件PhotoView的大小
            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            photoView.setOnPhotoTapListener(listener);//给PhotoView添加点击事件
            PicassoUtil.setImageUrl(mContext,photoView,mImgUrls.get(i), MyTools.getWidth(mContext),MyTools.getHight(mContext));
            mViews.add(photoView);//最后把我们加载的所有PhotoView传给View数组
        }

        mAdapter = new ShowImagesAdapter(mViews);//给适配器传入图片控件数组
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //给ViewPager添加监听事件
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
