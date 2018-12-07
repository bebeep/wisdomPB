package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        new Handler().postDelayed(new Loading(), 1200);
    }

    private class Loading implements Runnable {
        @Override
        public void run() {
            if(TextUtils.isEmpty(MyApplication.getInstance().getAccessToken())||TextUtils.isEmpty(MyApplication.getInstance().getRefreshToken())){
                startActivity(new Intent().setClass(StartActivity.this,LoginActivity.class));
            }else startActivity(new Intent().setClass(StartActivity.this,MainActivity.class));
            finish();
        }
    }
}
