package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityBookContentBinding;


/**
 * 文章内容
 *
 */
public class BookContentActivity extends Activity implements View.OnClickListener{
    private ActivityBookContentBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_content);
        init();
    }

    private void init(){
        binding.setVariable(BR.onClickListener,this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_note:
                startActivity(new Intent(this,MyNoteActivity.class));
                break;
        }
    }
}
