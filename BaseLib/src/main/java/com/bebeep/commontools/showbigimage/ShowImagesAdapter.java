package com.bebeep.commontools.showbigimage;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ShowImagesAdapter extends PagerAdapter {

    private List<View> views;

    public ShowImagesAdapter(List<View> views) {
        this.views = views;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
        //确定一个页面视图是否关联到一个特定的对象。
    }

    @Override
    public int getCount() {
        return views.size();
        //得到控件数组个数
    }

    //一般来说，destroyitem在viewpager移除一个item时调用。
    // viewpage一般都会缓冲3个item，即一开始就会调用3次instantiateItem,
    // 当向右滑动，到第3页时，第1页的item会被调用到destroyitem
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(views.get(position));
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(views.get(position));
        return views.get(position);
    }
}
