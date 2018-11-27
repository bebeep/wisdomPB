package com.bebeep.commontools.views;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bebeep.commontools.R;


/**
 * 拍照/选择照片通用UI
 */
public class ChoosePicPopWindow {

    private Activity context;
    private OnClickListener onClickListener;
    private View popView;
    private  PopupWindow popupWindow;

    public interface OnClickListener{
        void onAlbumClick(PopupWindow popupWindow);
        void onCameraClick(PopupWindow popupWindow);
        void onCancelClick(PopupWindow popupWindow);
    }

    public void setOnClickListener(OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    public ChoosePicPopWindow(final Activity context){
        this.context = context;
        popView = View.inflate(context,R.layout.layout_choose_pic,null);
        TextView bt_album =  popView.findViewById(R.id.btn_pop_album);
        TextView bt_camera =  popView.findViewById(R.id.btn_pop_camera);
        TextView bt_cancle =  popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = context.getResources().getDisplayMetrics().widthPixels;
        int height = context.getResources().getDisplayMetrics().heightPixels*1/3;

        popupWindow = new PopupWindow(popView,weight,height);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);

        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null)onClickListener.onAlbumClick(popupWindow);
            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null)onClickListener.onCameraClick(popupWindow);
            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onClickListener!=null)onClickListener.onCancelClick(popupWindow);
            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1.0f;
                context.getWindow().setAttributes(lp);
            }
        });
    }


    public void show(){
        if(popupWindow!=null&& popView!=null){
            //popupWindow出现屏幕变为半透明
            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            lp.alpha = 0.5f;
            context.getWindow().setAttributes(lp);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,50);
        }
    }

}
