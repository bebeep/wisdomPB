package com.bebeep.wisdompb.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.MultiItemTypeAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.views.ChoosePicPopWindow;
import com.bebeep.commontools.views.PopWindow;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.databinding.ActivityTicklingBinding;
import com.bebeep.wisdompb.util.PicassoImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.dagger.photopicker.PhotoPicker;
import cc.dagger.photopicker.picker.Load;
import cc.dagger.photopicker.picker.PhotoFilter;
import cc.dagger.photopicker.picker.PhotoSelectBuilder;

/**
 * 意见反馈
 */
public class TicklingActivity extends BaseEditActivity implements View.OnClickListener{
    private ActivityTicklingBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;

    private boolean showName = false;
    private ChoosePicPopWindow popWindow;
    private String picPath = "",imagePath= "";
    private Uri outputUri;//裁剪完照片保存地址



    private ArrayList<String> mSelectPath;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tickling);
        init();
    }

    private void init(){
        initAdapter();
        initTakePhoto();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("意见反馈");

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initAdapter(){
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        adapter = new CommonAdapter<String>(this,R.layout.item_choose_pic,list){
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                if(position == list.size()-1)holder.setImageResource(R.id.iv,R.drawable.icon_pic_add);
                else{
                    holder.setImageUrl((ImageView) holder.getView(R.id.iv),"file://"+s,R.drawable.default_error,90,90);
                }
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        binding.recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if(position == list.size()-1){
//                    if(popWindow!=null) popWindow.show();
                    pickImage();
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_choose_type://选择类型

                break;
            case R.id.fl_pic_num://点击图片的数量

                break;
            case R.id.fl_anonymity://匿名
                showName = !showName;
                binding.ivShowName.setImageResource(showName?R.drawable.icon_choose_c:R.drawable.icon_choose_u);
                break;
        }
    }

    private void pickImage() {
//      调用相册中的相片，用Picasso展示
        PhotoPicker.init(new PicassoImageLoader(), null);
//      启用相机
        boolean showCamera = true;
//      最大选择5张
        int maxNum = 5;
//      显示3列
        int columns = 3;
        Load load = PhotoPicker.load()
                .showCamera(showCamera)
                .filter(PhotoFilter.build().showGif(false).minSize(2 * 1024))
                .gridColumns(columns);
//      传入最大选择的数量，和路径
        PhotoSelectBuilder builder = load.multi().maxPickSize(maxNum).selectedPaths(mSelectPath);

        //单选
//        PhotoSelectBuilder builder = load.single();
        builder.start(this);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == PhotoPicker.REQUEST_SELECTED) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(PhotoPicker.EXTRA_RESULT);
                Log.e("TAG","mSelectPath:"+ MyApplication.gson.toJson(mSelectPath));
                if(mSelectPath!=null && mSelectPath.size()>0){
                    list.removeAll(list);
                    list.addAll(mSelectPath);
                    list.add("6");
                    adapter.refresh(list);
                }
            }
        }
    }



    /**
     * 拍照/相册选择初始化
     */
    private void initTakePhoto(){
        popWindow = new ChoosePicPopWindow(this);
        popWindow.setOnClickListener(new ChoosePicPopWindow.OnClickListener() {
            @Override
            public void onAlbumClick(PopupWindow popupWindow) {
                MyTools.showToast(TicklingActivity.this,"相册");
                if (ContextCompat.checkSelfPermission(TicklingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(TicklingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    pickImage();
                }
                popupWindow.dismiss();
            }
            @Override
            public void onCameraClick(PopupWindow popupWindow) {
                MyTools.showToast(TicklingActivity.this,"拍照");

                popupWindow.dismiss();
            }
            @Override
            public void onCancelClick(PopupWindow popupWindow) {
                MyTools.showToast(TicklingActivity.this,"取消");
                popupWindow.dismiss();
            }
        });
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    MyTools.showToast(this,"You denied the permission");
                }
                break;
            default:
        }
    }





    /**
     * 裁剪图片
     */
    private void cropPhoto(String imagePath) {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
//        File file = new File(getExternalCacheDir(), "crop_image.jpg");
//        try {
//            if (file.exists()) {
//                file.delete();
//            }
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        outputUri = Uri.fromFile(file);
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        }
//        intent.setDataAndType(Uri.fromFile(file), "image/*");
//        //裁剪图片的宽高比例
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("crop", "true");//可裁剪
//        // 裁剪后输出图片的尺寸大小
//        intent.putExtra("outputX", 150);
//        intent.putExtra("outputY", 150);
//        intent.putExtra("scale", true);//支持缩放
//        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
//        intent.putExtra("noFaceDetection", true);//取消人脸识别
//        startActivityForResult(intent, 777);


        File file = new File(imagePath);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(this, "com.leon.crop.fileprovider", file);
            intent.setDataAndType(contentUri, "image/*");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/*");
        }
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0.1);
        intent.putExtra("aspectY", 0.1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        intent.putExtra("scale", true);
        startActivityForResult(intent, 777);
    }
}
