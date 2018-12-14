package com.bebeep.wisdompb.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityPlayvideoBinding;

public class PlayVideoActivity extends Activity {
    private ActivityPlayvideoBinding binding;
    private String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_playvideo);
        initView();
    }


    private void initView() {
        url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            MyTools.showToast(this, "视频地址无效");
            finish();
        }
        binding.pvVideoView.setMediaController(binding.pvMediaController);
        setVideoAreaSize();

        binding.pvVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                MyTools.showToast(PlayVideoActivity.this, "视频加载失败");
                binding.pvVideoView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 300);
                return false;
            }
        });

        binding.pvVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                binding.pvVideoView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 300);
            }
        });

        binding.imgPvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    /**
     * 设置视频区域大小
     */
    private void setVideoAreaSize() {
        binding.pvVideoView.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams videoLayoutParams = binding.pvVideoView.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                binding.pvVideoView.setLayoutParams(videoLayoutParams);
                binding.pvVideoView.setVideoURI(Uri.parse(url));
                binding.pvVideoView.requestFocus();
                binding.pvVideoView.start();
            }
        });

    }


    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(com.bebeep.commontools.R.anim.in_right, com.bebeep.commontools.R.anim.out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(com.bebeep.commontools.R.anim.in_left, com.bebeep.commontools.R.anim.out_right);
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(com.bebeep.commontools.R.anim.in_left, com.bebeep.commontools.R.anim.out_right);
    }
}
