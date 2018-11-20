package com.bebeep.commontools.views;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.bebeep.commontools.R;


/**
 * 带数字进度条的dialog
 */
public class ProgressDialogWithNum extends Dialog {
    public ProgressDialogWithNum(Context context) {
        this(context, R.style.CustomProgressDialog);
    }

    public ProgressDialogWithNum(Context context, int theme) {
        super(context, theme);  
        this.setContentView(R.layout.dialog_withnumber);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
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

}
