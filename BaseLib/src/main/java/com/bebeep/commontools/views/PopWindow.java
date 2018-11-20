package com.bebeep.commontools.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.bebeep.commontools.R;

/**
 * Created by Bebeep
 * Time 2018/3/16 17:24
 * Email 424468648@qq.com
 * Tips 悬浮窗
 */

public class PopWindow extends PopupWindow {

    private View.OnClickListener listener;
    private View view;
    private boolean clickOutSideClose = true;

    public PopWindow(Context mContext, View view, int width,int height) {
        super(mContext);
        this.view = view;
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        this.setAnimationStyle(R.style.popwin_anim_style);
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        update();
        setClickDismiss();
    }

    public void setWidthAndHeight(int width,int height){
        setWidth(width);
        setHeight(height);
        update();
    }

    public void clickOutSideClose(boolean b){
        clickOutSideClose = b;
    }
    public void setClickDismiss(){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(clickOutSideClose)
                    dismiss();
            }
        });
    }

    public View getView(){
        return view;
    }

}
