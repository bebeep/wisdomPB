package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

import com.bebeep.commontools.file.FileUtil;
import com.bebeep.commontools.utils.CompressImageUtils;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.commontools.views.ChoosePicPopWindow;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseSlideActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.FileUploadEntity;
import com.bebeep.wisdompb.bean.ImageVideoEntity;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityUserinfoBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PicassoImageLoader;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.google.gson.reflect.TypeToken;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.dagger.photopicker.PhotoPicker;
import cc.dagger.photopicker.picker.Load;
import cc.dagger.photopicker.picker.PhotoFilter;
import cc.dagger.photopicker.picker.PhotoSelectBuilder;

import static com.bebeep.commontools.utils.MyTools.guessMimeType;

/**
 * 用户信息
 */
public class UserInfoActivity extends BaseSlideActivity implements View.OnClickListener{

    private ActivityUserinfoBinding binding;

    private CustomProgressDialog customProgressDialog;
    private String outputUri;

    private UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_userinfo);
        init();
    }

    private void init(){
        customProgressDialog = new CustomProgressDialog(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.setVariable(BR.onClickListener,this);
        userInfo = MyApplication.getInstance().getUserInfo();
        PicassoUtil.setImageUrl(this,binding.ivHead, URLS.IMAGE_PRE+userInfo.getPhoto(),R.drawable.icon_head,60,60);
        binding.tvName.setText(userInfo.getName());
        binding.tvBranchName.setText(userInfo.getOffice());
        binding.tvSex.setText(userInfo.getSex()==1?"男":"女");
        binding.tvBirthday.setText(userInfo.getBirthday());
        binding.tvNation.setText(userInfo.getNation());
        binding.tvEdu.setText(userInfo.getEducation());
        binding.tvJoinTime.setText(userInfo.getJoiningPartyOrganizationDate());
        binding.tvOrgPosition.setText(userInfo.getPartyPosts());
        binding.tvOrgType.setText(userInfo.getTypeName());
        binding.tvOrgComment.setText(userInfo.getDemocraticAppraisal());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_head://修改头像
//                if(popWindow!=null) popWindow.show();
                pickImage();
                break;
            case R.id.tv_logout://退出登录
                deleteAlias(userInfo.getUserId());
                PreferenceUtils.setPrefString("access_token","");
                PreferenceUtils.setPrefString("refresh_token", "");
                PreferenceUtils.setPrefString("userInfo", "");

                setResult(RESULT_OK);
                finish();
                break;
        }
    }




    private void deleteAlias(String userId){
        PushAgent.getInstance(this).deleteAlias(userId, MyApplication.ALIAS_NAME, new UTrack.ICallBack(){
            @Override
            public void onMessage(boolean isSuccess, String message) {
                LogUtil.showLog("deleteAlias:"+message);
            }
        });
    }



    //选择相册
    private void pickImage() {
        PhotoPicker.init(new PicassoImageLoader(), null);
        int columns = 3;
        Load load = PhotoPicker.load()
                .showCamera(true)
                .filter(PhotoFilter.build().showGif(false))
                .gridColumns(columns);
        PhotoSelectBuilder builder = load.single();
        builder.start(this);
    }


    private void upload(File file){
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
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
                MyTools.showToast(UserInfoActivity.this,"上传失败");
            }
            @Override
            public void onResponse(String json) {
                LogUtil.showLog("上传文件 json："+ json);
                BaseObject<FileUploadEntity> baseObject = MyApplication.gson.fromJson(json, new TypeToken<BaseObject<FileUploadEntity>>(){}.getType());
                FileUploadEntity entity = baseObject.getData();
                LogUtil.showLog("上传文件："+ entity.getPath());
                FileUtil.deleteFile(outputUri);
                submit(entity.getPath());
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



    private void submit(final String path){
        HashMap header = new HashMap(),map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("photo",path);
        LogUtil.showLog("提交："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.DISCOVER_RELEASE, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                customProgressDialog.cancel();
                MyTools.showToast(UserInfoActivity.this,"修改头像失败，请重试");
            }
            @Override
            public void onResponse(BaseObject response) {
                customProgressDialog.cancel();
                MyTools.showToast(UserInfoActivity.this, response.getMsg());
                if(response.isSuccess()){
                    PicassoUtil.setImageUrl(UserInfoActivity.this,binding.ivHead, URLS.IMAGE_PRE +path,R.drawable.icon_head,60,60);
                    userInfo.setPhoto(path);
                    PreferenceUtils.setPrefString("userInfo",MyApplication.gson.toJson(userInfo));
                }else {
                    MyTools.showToast(UserInfoActivity.this,response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
            }
        },header,map);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == PhotoPicker.REQUEST_SELECTED) {//相册
            if (resultCode == RESULT_OK) {
                List<String> mSelectPath = data.getStringArrayListExtra(PhotoPicker.EXTRA_RESULT);
                Log.e("TAG","mSelectPath:"+ MyApplication.gson.toJson(mSelectPath));
                if(mSelectPath!=null && mSelectPath.size()>0){
                    String path = mSelectPath.get(0);
                    outputUri = MyApplication.FILE_TEMP_PATH + "userHead.jpg";

                    Uri destination = Uri.fromFile(new File(outputUri));
                    Crop.of(Uri.fromFile(new File(path)), destination).asSquare().start(this);
                }
            }
        }else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            LogUtil.showLog("裁剪成功："+Crop.getOutput(data));
            customProgressDialog.show();
            upload(new File(outputUri));
        }
    }
}
