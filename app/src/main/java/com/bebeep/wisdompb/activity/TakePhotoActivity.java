package com.bebeep.wisdompb.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.ImageVideoEntity;
import com.bebeep.wisdompb.databinding.ActivityTakePhotoBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.cjt2325.cameralibrary.listener.ClickListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;

import java.io.File;
import java.io.Serializable;

public class TakePhotoActivity extends AppCompatActivity{
    private ActivityTakePhotoBinding binding;
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    private boolean granted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_take_photo);
        init();
    }


    private void init(){
        //设置视频保存路径
        binding.jcameraview.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");
        //JCameraView监听
        binding.jcameraview.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) { //拍照
                String filePath = MyTools.saveBitmapFile(TakePhotoActivity.this,bitmap);
                if(TextUtils.isEmpty(filePath)){
                    MyTools.showToast(TakePhotoActivity.this,"存储照片失败，请重试");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("entity",  new ImageVideoEntity(1,filePath));
                setResult(881,intent);
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame) {//视频
                String filePath = MyTools.saveBitmapFile(firstFrame);
                Intent intent = new Intent();
                intent.putExtra("entity",  new ImageVideoEntity(2,filePath,url));
                setResult(882,intent);
                finish();
            }
        });
        //6.0动态权限获取
        getPermissions();

        binding.jcameraview.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                LogUtil.showLog("left click");
                finish();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //具有权限
                granted = true;
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
                granted = false;
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    granted = true;
                    binding.jcameraview.onResume();
                }else{
                    MyTools.showToast(this,"请到设置-权限管理中开启" );
                    finish();
                }
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
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
