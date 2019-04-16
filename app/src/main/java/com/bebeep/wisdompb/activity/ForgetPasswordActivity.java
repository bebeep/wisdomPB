package com.bebeep.wisdompb.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseActivity;
import com.bebeep.wisdompb.base.BaseSlideActivity;

public class ForgetPasswordActivity extends BaseSlideActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
    }

    private void init(){
        ImageView back = findViewById(R.id.iv_back);
        back.setVisibility(View.VISIBLE);

        TextView tv = findViewById(R.id.tv_title);
        tv.setText("找回密码");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
