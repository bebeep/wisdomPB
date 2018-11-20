package com.bebeep.commontools.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/3/9.
 */
public class DecoratorViewPager extends ViewPager {
    private float mDownX;
    private float mDownY;
    public DecoratorViewPager(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public DecoratorViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(ev.getX()-mDownX)>Math.abs(ev.getY()-mDownY)){
                    return false;
                }else{
                    return true;
                }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(Math.abs(ev.getX()-mDownX)>Math.abs(ev.getY()-mDownY)){
                    return false;
                }else{
                    return true;
                }
        }
        return super.dispatchTouchEvent(ev);
    }
}