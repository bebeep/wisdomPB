package com.bebeep.commontools.views;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;

import com.bebeep.commontools.R;


public class CustomProgressDialog extends Dialog {
    public CustomProgressDialog(Context context) {
        this(context, R.style.CustomProgressDialog);
    }
  
    public CustomProgressDialog(Context context, int theme) {
        super(context, theme);  
        this.setContentView(R.layout.customprogressdialog);  
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
