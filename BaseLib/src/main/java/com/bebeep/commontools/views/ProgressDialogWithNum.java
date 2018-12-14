package com.bebeep.commontools.views;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.bebeep.commontools.R;
import com.github.lzyzsd.circleprogress.ArcProgress;


/**
 * 带数字进度条的dialog
 */
public class ProgressDialogWithNum extends Dialog {
    private ArcProgress arcProgress;

    public ProgressDialogWithNum(Context context) {
        this(context, R.style.CustomProgressDialog);
    }

    public ProgressDialogWithNum(Context context, int theme) {
        super(context, theme);  
        this.setContentView(R.layout.dialog_withnumber);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        arcProgress = findViewById(R.id.arc_progress);
        arcProgress.setMax(100);
        setCancelable(false);
    }

    @Override  
    public void onWindowFocusChanged(boolean hasFocus) {  
        if (!hasFocus) {
            dismiss();  
        }  
    }

    public void cancel(){
        if(isShowing()){
            dismiss();
        }
    }

    public void setProgress(int progress,String text){
        arcProgress.setProgress(progress);
        arcProgress.setBottomText(text);
    }

}
