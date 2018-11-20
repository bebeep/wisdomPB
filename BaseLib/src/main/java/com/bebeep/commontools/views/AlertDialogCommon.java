package com.bebeep.commontools.views;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.bebeep.commontools.R;

/**
 * Created by Bebeep
 * Time 2018/3/16 17:48
 * Email 424468648@qq.com
 * Tips
 */

public class AlertDialogCommon extends Dialog {

    private View rootView;
    private Context context;
    private int rid;

    public AlertDialogCommon(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public AlertDialogCommon(Context context, int rid) {
        super(context, R.style.dialog);
        this.context = context;
        this.rid = rid;
    }

    public AlertDialogCommon(Context context, View view) {
        super(context, R.style.dialog);
        this.context = context;
        this.rootView = view;
    }

    public void init(){
        rootView = LayoutInflater.from(context).inflate(rid,null);
        setContentView(rootView);
    }

    public void showDialog(){
        this.show();
//        init();
        setContentView(rootView);
    }

    public View getView(){
        return rootView;
    }
}
