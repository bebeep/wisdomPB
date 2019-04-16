package com.bebeep.wisdompb.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.showbigimage.ShowMultiBigImageDialog;
import com.bebeep.commontools.utils.CompressImageUtils;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.ChoosePicPopWindow;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.FileUploadEntity;
import com.bebeep.wisdompb.bean.MeetingMinitesEntity;
import com.bebeep.wisdompb.databinding.ActivityTicklingBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PicassoImageLoader;
import com.bebeep.wisdompb.util.URLS;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.dagger.photopicker.PhotoPicker;
import cc.dagger.photopicker.picker.Load;
import cc.dagger.photopicker.picker.PhotoFilter;
import cc.dagger.photopicker.picker.PhotoSelectBuilder;

import static com.bebeep.commontools.utils.MyTools.guessMimeType;


/**
 * 发布会议纪要
 */
public class ReleaseMeetingMinitesActivity extends BaseEditActivity implements View.OnClickListener{

    private ActivityTicklingBinding binding;
    private List<String> list = new ArrayList<>();
    private CommonAdapter adapter;

    private boolean showName = false;
    private ChoosePicPopWindow popWindow;
    private String id="",picPath = "",imagePath= "",uploadPics = "";
    private Uri outputUri;//裁剪完照片保存地址


    private CustomDialog customDialog;
    private CustomProgressDialog customProgressDialog;

    private ArrayList<String> mSelectPath = new ArrayList<>();
    private int delPosition = -1;
    private MeetingMinitesEntity entity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tickling);
        init();
    }

    private void init(){
        id = getIntent().getStringExtra("id");
        if(TextUtils.isEmpty(id)){
            finish();
            return;
        }
        initAdapter();
        initTakePhoto();
        initDialog();
        getData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("编辑会议纪要");
        customProgressDialog = new CustomProgressDialog(this);
    }


    private void initAdapter(){
        list.add("");
        adapter = new CommonAdapter<String>(this,R.layout.item_choose_pic,list){
            @Override
            protected void convert(ViewHolder holder, final String s, final int position) {
                ImageView iv = holder.getView(R.id.iv);
                if(position == list.size()-1 && TextUtils.isEmpty(s)){
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                    holder.setImageResource(R.id.iv,R.drawable.icon_pic_add);
                }else{
                    iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.setImageUrl((ImageView) holder.getView(R.id.iv),s.startsWith("file://")?s:URLS.IMAGE_PRE + s,R.drawable.default_error,90,90);
                }

                holder.setOnLongClickListener(R.id.iv, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(!TextUtils.isEmpty(s)){
                            delPosition = position;
                            customDialog.show();
                        }
                        return true;
                    }
                });

                holder.setOnClickListener(R.id.iv, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(position == list.size()-1 && TextUtils.isEmpty(list.get(position))){
                            pickImage();
                        }else{
                            new ShowMultiBigImageDialog(ReleaseMeetingMinitesActivity.this,getImgList()).show(position);
                        }
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new GridLayoutManager(this,5));
        binding.recyclerView.setAdapter(adapter);
    }


    private List<String> getImgList(){
        List<String> imgList = new ArrayList<>();
        for (String s :list){
            if(!TextUtils.isEmpty(s))imgList.add(URLS.IMAGE_PRE + s);
        }
        return imgList;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_anonymity://匿名
                showName = !showName;
                binding.ivShowName.setImageResource(showName?R.drawable.icon_choose_c:R.drawable.icon_choose_u);
                break;
            case R.id.tv_submit://提交
                if(TextUtils.isEmpty(binding.etContent.getText().toString())){
                    MyTools.showToast(this,"请填写内容");
                    return;
                }
                if(list==null||list.size()==0)return;
                List<File> fileList = new ArrayList<>();
                uploadPics = "";
                for (String s:list) {
                    if(!TextUtils.isEmpty(s)){
                        if(s.startsWith("file://")) fileList.add(new File(s.substring(7,s.length())));
                        else uploadPics += s + ",";
                    }
                }
                if(getPicNum()==0)return;
                binding.tvSubmit.setEnabled(false);
                customProgressDialog.show();
                if((fileList==null||fileList.size()==0)) submit();
                else {
                    for (int i=0;i<fileList.size();i++) upload(fileList.get(i),i,fileList.size());
                }

                break;
        }
    }


    private void getData(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("id",id);
        LogUtil.showLog("获取会议纪要："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.MEETING_MINITES_EDIT, new OkHttpClientManager.ResultCallback<BaseObject<MeetingMinitesEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                uploadPics = "";
                statusMsg(e,code);
                customProgressDialog.cancel();
            }
            @Override
            public void onResponse(BaseObject<MeetingMinitesEntity> response) {
                customProgressDialog.cancel();
                LogUtil.showLog("获取会议纪要："+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    entity = response.getData();
                    initUI();
                }else {
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    private void initUI(){
        if(entity == null) return;
        binding.etTitle.setText(entity.getRemarks());
        binding.etContent.setText(entity.getContents());
        String img = entity.getImgsrcs();
        String[] imgs = img.split(",");
        if(imgs!=null || imgs.length >0){
            list.clear();
            for (String s: imgs){
                if(!TextUtils.isEmpty(s)) {
                    list.add(s);
                }
            }
            if(getPicNum()<5)list.add("");
            adapter.refresh(list);
            binding.tvPicNum.setText(String.valueOf(getPicNum()));
        }
    }


    private int getPicNum(){
        if(list == null || list.size() ==0) return 0;
        int num = 0;
        for (String s : list){
            if(!TextUtils.isEmpty(s)) num ++;
        }
        return num;
    }


    private void upload(File file,final int position,final int size){
        MultipartBuilder builder = new MultipartBuilder()
                .type(MultipartBuilder.FORM);

        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\""+ MyApplication.AUTHORIZATION+ "\""), RequestBody.create(null, MyApplication.getInstance().getAccessToken()));
        RequestBody fileBody;
        String fileName = file.getName();
        fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
        // TODO 根据文件名设置contentType
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\""+ MyApplication.FILEKEY + "\"; filename=\"" + fileName+ "\""), fileBody);

        final long fileSize = file.length();

        RequestBody requestBody = builder.build();
        OkHttpClientManager.getInstance().uploadPost2ServerProgress(this, URLS.UPLOAD, requestBody, new OkHttpClientManager.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                binding.tvSubmit.setEnabled(false);
                MyTools.showToast(ReleaseMeetingMinitesActivity.this,"上传失败");
                customProgressDialog.cancel();
            }
            @Override
            public void onResponse(String json) {
                LogUtil.showLog("上传文件 json："+ json);
                BaseObject<FileUploadEntity> baseObject = MyApplication.gson.fromJson(json, new TypeToken<BaseObject<FileUploadEntity>>(){}.getType());
                FileUploadEntity entity = baseObject.getData();
                LogUtil.showLog("上传文件："+ entity.getPath());
                uploadPics += entity.getPath() + ",";
                if(position == size-1 ){
                    submit();
                }
            }
        }, new OkHttpClientManager.UIchangeListener() {
            @Override
            public void progressUpdate(long bytesWrite, long contentLength, boolean done) {
//                 int per = (int) (bytesWrite * 100 / fileSize);
//                 progressDialogWithNum.setProgress(per,"已上传:"+(position+1)+"/"+size);
//                 if(position == size-1) progressDialogWithNum.cancel();
            }
        });
    }

    private void submit(){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());

        if(!TextUtils.isEmpty(uploadPics)&& uploadPics.endsWith(",")) uploadPics = uploadPics.substring(0,uploadPics.length()-1);

        map.put("id",id);
        map.put("remarks",binding.etTitle.getText().toString());
        map.put("contents",binding.etContent.getText().toString());
        map.put("imgsrcs",uploadPics);

        LogUtil.showLog("提交会议纪要："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.MEETING_MINITES_RELEASE, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                binding.tvSubmit.setEnabled(true);
                uploadPics = "";
                statusMsg(e,code);
                customProgressDialog.cancel();
            }
            @Override
            public void onResponse(BaseObject response) {
                binding.tvSubmit.setEnabled(true);
                customProgressDialog.cancel();
                MyTools.showToast(ReleaseMeetingMinitesActivity.this, response.getMsg());
                if(response.isSuccess()){
                    uploadPics = "";
                    finish();
                }else {
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }




    private void pickImage() {
//      调用相册中的相片，用Picasso展示
        PhotoPicker.init(new PicassoImageLoader(), null);
//      启用相机
        boolean showCamera = true;
//      最大选择5张
        int maxNum = 5 - getPicNum();
//      显示3列
        int columns = 3;
        Load load = PhotoPicker.load()
                .showCamera(showCamera)
                .filter(PhotoFilter.build().showGif(false).minHeight(300).minWidth(300))
                .gridColumns(columns);
//      传入最大选择的数量，和路径
        PhotoSelectBuilder builder = load.multi().maxPickSize(maxNum).selectedPaths(null);

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
                    list.remove(list.size()-1);
                    for (String s : mSelectPath) {
                        //压缩相册里的图片 避免OOM
                        String path = CompressImageUtils.compressImage(s, MyApplication.FILE_PATH+System.currentTimeMillis() + ".jpg",40);
                        if(!list.contains("file://"+path))list.add("file://"+path);
                    }
                    if(getPicNum()<5)list.add("");
                    adapter.refresh(list);
                    binding.tvPicNum.setText(String.valueOf(getPicNum()));
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
                if (ContextCompat.checkSelfPermission(ReleaseMeetingMinitesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ReleaseMeetingMinitesActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    pickImage();
                }
                popupWindow.dismiss();
            }
            @Override
            public void onCameraClick(PopupWindow popupWindow) {
                popupWindow.dismiss();
            }
            @Override
            public void onCancelClick(PopupWindow popupWindow) {
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

    private void initDialog(){
        customDialog = new CustomDialog.Builder(this)
                .setMessage("是否移除该照片")
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                    }
                }).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                        if(getPicNum() == 0||getPicNum()==5) list.add("");
                        mSelectPath.remove(list.get(delPosition));
                        list.remove(delPosition);

                        adapter.refresh(list);
                        binding.tvPicNum.setText(String.valueOf(getPicNum()));
                    }
                })
                .createTwoButtonDialog();
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
