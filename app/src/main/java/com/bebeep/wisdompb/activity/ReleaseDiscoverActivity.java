package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import com.bebeep.commontools.showbigimage.ShowSingleBigImageDialog;
import com.bebeep.commontools.utils.CompressImageUtils;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.ChoosePicPopWindow;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.commontools.views.DragGridView;
import com.bebeep.commontools.views.ProgressDialogWithNum;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.adapter.GridView_ChannelAdapter;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.FileUploadEntity;
import com.bebeep.wisdompb.bean.ImageVideoEntity;
import com.bebeep.wisdompb.databinding.ActivityReleaseDiscoverBinding;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import cc.dagger.photopicker.PhotoPicker;
import cc.dagger.photopicker.picker.Load;
import cc.dagger.photopicker.picker.PhotoFilter;
import cc.dagger.photopicker.picker.PhotoSelectBuilder;

import static com.bebeep.commontools.utils.MyTools.guessMimeType;
import static com.bebeep.wisdompb.MyApplication.FILEKEY;


/**
 * 发布发现
 */
public class ReleaseDiscoverActivity extends BaseEditActivity implements TextWatcher{
    private ActivityReleaseDiscoverBinding binding;

    private List<ImageVideoEntity> imgList = new ArrayList<>();

    private GridView_ChannelAdapter adapter;
    private ArrayList<String> mSelectPath = new ArrayList<>();

    private int maxPic = 9;
    private ChoosePicPopWindow popWindow;
    private boolean showCamera = false;
    private ShowSingleBigImageDialog showSingleBigImageDialog;

    private long currentUploadData = 0, totalFileSize =0;
    private ProgressDialogWithNum progressDialogWithNum;
    private String uploadPics = "",uploadVideoUrl="";
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_release_discover);
        init();
    }

    private void init(){
        showSingleBigImageDialog = new ShowSingleBigImageDialog(ReleaseDiscoverActivity.this);
        progressDialogWithNum = new ProgressDialogWithNum(this);
        customProgressDialog = new CustomProgressDialog(this);
        initAdapter();
        initTakePhoto();
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("编辑");
        binding.title.tvRight.setText("发表");
        binding.etContent.addTextChangedListener(this);

        binding.title.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        binding.title.tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发布
                uploadFiles();
            }
        });

    }


    private void initAdapter(){
        imgList.add(new ImageVideoEntity(3));
        adapter = new GridView_ChannelAdapter(this, imgList);
        binding.draggv.setAdapter(adapter);

        binding.draggv.setEnableIndex(0);
        final int bottomY = MyTools.getHight(this) - MyTools.dip2px(this, 50);
        binding.draggv.setOnItemChangeListener(new DragGridView.OnItemChangeListener() {
            @Override
            public void onChange(int from, int to, int x, int y, boolean dragging) {
                y = y + firstY;
                binding.flDel.setVisibility(dragging?View.VISIBLE:View.GONE);
                binding.tvDel.setText(y>= bottomY?"松开即可删除":"拖动到此处删除");
                if(y >=bottomY && !dragging){//删除
                    if(9 == mSelectPath.size()) imgList.add(new ImageVideoEntity(3));
                    imgList.remove(from);
                    if(mSelectPath.size()>from)mSelectPath.remove(from);
                    if(imgList.size() == 0) imgList.add(new ImageVideoEntity(3));
                    binding.draggv.setEnableIndex(imgList.size()-1);
                    adapter.update(imgList);
                }

                if(dragging && from!=-1 && to!=-1){
                    Collections.swap(imgList,from,to);
                    Collections.swap(mSelectPath,from,to);
                    adapter.update(imgList);
                }
            }
        });

        binding.draggv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == imgList.size()-1){
                    if(position == 0){
                        if(imgList.size() == 1 && imgList.get(0).getFlag() == 2){ //视频
                            startActivity(new Intent(ReleaseDiscoverActivity.this, PlayVideoActivity.class).putExtra("url", "file://" + imgList.get(0).getVideoPath()));
                        }else{
                            showCamera = false;
                            if(popWindow!=null) popWindow.show();
                        }
                    }else {
                        showCamera = true;
                        pickImage();
                    }
                }else {//预览大图/视频
                    if(imgList.size() == 1 && imgList.get(0).getFlag() == 2){ //视频
                        startActivity(new Intent(ReleaseDiscoverActivity.this, PlayVideoActivity.class).putExtra("url", "file://"  + imgList.get(0).getVideoPath()));
                    }else{//图片
                        if(imgList.get(0).getFlag() == 1){
                            showSingleBigImageDialog.show("file://" +imgList.get(position).getUrl(),R.drawable.default_error); //单张图片
                        }
                    }
                }
            }
        });
    }


    //选择相册
    private void pickImage() {
        PhotoPicker.init(new PicassoImageLoader(), null);
        int columns = 3;
        Load load = PhotoPicker.load()
                .showCamera(showCamera)
                .filter(PhotoFilter.build().showGif(false).minSize(2 * 1024))
                .gridColumns(columns);
        PhotoSelectBuilder builder = load.multi().maxPickSize(9).selectedPaths(mSelectPath);
        builder.start(this);
    }

    /**
     * 拍照/相册选择初始化
     */
    private void initTakePhoto(){
        popWindow = new ChoosePicPopWindow(this);
        popWindow.setOnClickListener(new ChoosePicPopWindow.OnClickListener() {
            @Override
            public void onAlbumClick(PopupWindow popupWindow) { //相册
                pickImage();
                popupWindow.dismiss();
            }
            @Override
            public void onCameraClick(PopupWindow popupWindow) {//相机
                startActivityForResult(new Intent(ReleaseDiscoverActivity.this, TakePhotoActivity.class), 88);
                popupWindow.dismiss();
            }
            @Override
            public void onCancelClick(PopupWindow popupWindow) {
                popupWindow.dismiss();
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == PhotoPicker.REQUEST_SELECTED) {//相册
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(PhotoPicker.EXTRA_RESULT);
                Log.e("TAG","mSelectPath:"+ MyApplication.gson.toJson(mSelectPath));
                if(mSelectPath!=null && mSelectPath.size()>0){
                    imgList.removeAll(imgList);
                    for (String s : mSelectPath) {
                        //压缩相册里的图片 避免OOM
                        String path = CompressImageUtils.compressImage(s, MyApplication.FILE_PATH+System.currentTimeMillis() + ".jpg",40);
                        imgList.add(new ImageVideoEntity(1,path));
                    }
                    if(imgList.size()<9){
                        maxPic = maxPic - imgList.size();
                        imgList.add(new ImageVideoEntity(3));
                        binding.draggv.setEnableIndex(imgList.size()-1);
                    }else {
                        binding.draggv.setEnableIndex(-1);
                        maxPic = 0;
                    }
                    adapter.update(imgList);
                }
            }
        }else if(requestCode == 88 && resultCode == 881){ //拍照回调
            ImageVideoEntity entity = (ImageVideoEntity) data.getSerializableExtra("entity");
            LogUtil.showLog("拍照回调："+MyApplication.gson.toJson(entity));

            imgList.clear();
            mSelectPath.add(entity.getUrl());
            imgList.add(entity);
            maxPic = maxPic - imgList.size();
            imgList.add(new ImageVideoEntity(3));
            binding.draggv.setEnableIndex(imgList.size()-1);

            adapter.update(imgList);
        }else if(requestCode == 88 && resultCode == 882){//录视频回调
            ImageVideoEntity entity = (ImageVideoEntity) data.getSerializableExtra("entity");
            LogUtil.showLog("录视频回调："+MyApplication.gson.toJson(entity));
            imgList.clear();
            imgList.add(entity);
            binding.draggv.setEnableIndex(-1);
            adapter.update(imgList);
        }
    }

    public void uploadFiles(){
        if(imgList ==null|| imgList.size()==0)return;
        List<File> fileList = new ArrayList<>();
        currentUploadData = 0;
        totalFileSize = 0;
        for (ImageVideoEntity entity:imgList){
            if(entity.getFlag() == 1 && !TextUtils.isEmpty(entity.getUrl())){
                File file = new File(entity.getUrl());
                totalFileSize+= file.length();
                fileList.add(file);
            }else if(entity.getFlag() == 2&& !TextUtils.isEmpty(entity.getVideoPath())){
                File file = new File(entity.getVideoPath());
                totalFileSize+= file.length();
                fileList.add(file);
                file = new File(entity.getUrl());
                totalFileSize+= file.length();
                fileList.add(file);
            }
        }

        if(fileList!=null && fileList.size()!=0){
            int size = fileList.size();
//            progressDialogWithNum.setProgress(0,"已上传:0/"+size);
//            progressDialogWithNum.show();
            customProgressDialog.show();
            for (int i=0;i<fileList.size();i++) upload(fileList.get(i),i,size);
        }
    }


    private void submit(){
        HashMap header = new HashMap(),map = getMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());

        LogUtil.showLog("提交："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.DISCOVER_RELEASE, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                uploadPics = "";
                uploadVideoUrl = "";
                statusMsg(e,code);
                customProgressDialog.cancel();
            }
            @Override
            public void onResponse(BaseObject response) {
                customProgressDialog.cancel();
                MyTools.showToast(ReleaseDiscoverActivity.this, response.getMsg());
                if(response.isSuccess()){
                    uploadPics = "";
                    uploadVideoUrl = "";
                    setResult(RESULT_OK);
                    finish();
                }
            }
        },header,map);
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
                 MyTools.showToast(ReleaseDiscoverActivity.this,"上传失败");
             }
             @Override
             public void onResponse(String json) {
                 LogUtil.showLog("上传文件 json："+ json);
                 BaseObject<FileUploadEntity> baseObject = MyApplication.gson.fromJson(json, new TypeToken<BaseObject<FileUploadEntity>>(){}.getType());
                 FileUploadEntity entity = baseObject.getData();
                 LogUtil.showLog("上传文件："+ entity.getPath());
                 if(entity.getPath().endsWith(".mp4")||entity.getPath().endsWith(".MP4")||
                         entity.getPath().endsWith(".avi")||entity.getPath().endsWith(".AVI")||
                         entity.getPath().endsWith(".mkv")||entity.getPath().endsWith(".MKV")||
                         entity.getPath().endsWith(".rmvb")||entity.getPath().endsWith(".RMVB")){
                     uploadVideoUrl = entity.getPath();
                 }else uploadPics += entity.getPath() + ",";
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


    private HashMap getMap(){
        HashMap map = new HashMap();
        map.put("title",binding.etContent.getText().toString());
        if(imgList==null||imgList.size()==0){
            map.put("type","2");
            return map;
        }else if(imgList.size()==1 && imgList.get(0).getFlag() == 2){
            map.put("type","0");
            map.put("videoUrl",uploadVideoUrl);
            map.put("imgsrcs",uploadPics.replace(",",""));
            return map;
        }else{
            map.put("type","1");
            map.put("imgsrcs", uploadPics);
            return map;
        }
    }





    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        binding.title.tvRight.setVisibility(TextUtils.isEmpty(s.toString().replaceAll(" ",""))?View.GONE:View.VISIBLE);
    }
    @Override
    public void afterTextChanged(Editable s) {
    }



    int firstX = 0;
    int firstY = 0;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        super.dispatchTouchEvent(ev);

        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            firstX = (int) ev.getX();
            firstY = (int) ev.getY();
        }
        return false;
    }
}
