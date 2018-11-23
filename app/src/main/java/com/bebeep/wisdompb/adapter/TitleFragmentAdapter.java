package com.bebeep.wisdompb.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bebeep
 * Time 2018/2/24 9:34
 * Email 424468648@qq.com
 * Tips
 */

public class TitleFragmentAdapter extends FragmentPagerAdapter {
    /**
     * The m fragment list.
     */
    private List<Fragment> mFragmentList = null;

    private List<String> titles;

    public void update(List<Fragment> mFragmentList, List<String> titles){
        this.mFragmentList = mFragmentList;
        this.titles = titles;
        notifyDataSetChanged();
    }

    /**
     * Instantiates a new ab fragment pager adapter.
     *
     * @param mFragmentManager the m fragment manager
     * @param fragmentList     the fragment list
     */
    public TitleFragmentAdapter(FragmentManager mFragmentManager,
                                ArrayList<Fragment> fragmentList) {
        super(mFragmentManager);
        mFragmentList = fragmentList;
    }

    /**
     * titles是给TabLayout设置title用的
     *
     * @param mFragmentManager
     * @param fragmentList
     * @param titles
     */
    public TitleFragmentAdapter(FragmentManager mFragmentManager,
                                List<Fragment> fragmentList, List<String> titles) {
        super(mFragmentManager);
        mFragmentList = fragmentList;
        this.titles = titles;
    }

    /**
     * 描述：获取数量.
     *
     * @return the count
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    /**
     * 描述：获取索引位置的Fragment.
     *
     * @param position the position
     * @return the item
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position < mFragmentList.size()) {
            fragment = mFragmentList.get(position);
        } else {
            fragment = mFragmentList.get(0);
        }
//        return Fragment_News.newInstance(titles.get(position));
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && titles.size() > 0)
            return titles.get(position);
        return null;
    }
}
