package com.bebeep.wisdompb.activity;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bebeep.commontools.file.FileUtil;
import com.bebeep.commontools.timepicker.CustomDatePicker;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.FileUploadEntity;
import com.bebeep.wisdompb.bean.OrderMeetingEntity;
import com.bebeep.wisdompb.databinding.ActivityOrderMeetingBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.bebeep.commontools.utils.MyTools.guessMimeType;


/**
 * 预约会议
 */
public class OrderMeetingActivity extends BaseEditActivity implements View.OnClickListener{
    private ActivityOrderMeetingBinding binding;
    private CustomProgressDialog customProgressDialog;

    private CustomDatePicker customDatePicker;
    private int chooseText = 0,fraction = 0;
    private String filePath = "",fileName = "", uploadFilePath = "",userIds = "",userName = "";
    private File uploadFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_meeting);
        init();
    }


    private void init(){
        customProgressDialog = new CustomProgressDialog(this);
        initDataPicker();
        setLocalData();
        binding.setVariable(BR.onClickListener,this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("预约会议");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_choose_date://选择会议日期

                break;
            case R.id.ll_choose_start_time://选择开始时间
                chooseText = 1;
                if(customDatePicker!=null)customDatePicker.show(binding.tvStartTime.getText().toString());
                break;
            case R.id.ll_choose_end_time://选择结束时间
                chooseText = 2;
                if(customDatePicker!=null) customDatePicker.show(binding.tvEndTime.getText().toString());
                break;
            case R.id.ll_choose_joiner://选择参会人员
                startActivityForResult(new Intent(this,ChooseJoinerAcivity.class),89);
                break;
            case R.id.iv_file://添加附件
                if(uploadFile == null){ //没有文件-选择文件
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/*");//设置类型
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,88);
                }else {//有文件-预览文件

                }
                break;
            case R.id.iv_file_del://删除附件
                uploadFile = null;
                fileName = "";
                filePath = "";
                setFileIcon(fileName);
                break;
            case R.id.iv_min://减少积分
                fraction = fraction==0?0:fraction-1;
                binding.tvJifenNum.setText(String.valueOf(fraction));
                break;
            case R.id.iv_plus://增加积分
                fraction ++;
                binding.tvJifenNum.setText(String.valueOf(fraction));
                break;
            case R.id.tv_save://保存草稿
                saveLocal();
                break;
            case R.id.tv_release://发布
                if(checkInput()){
                    upload(uploadFile);
                }
                break;
        }
    }

    private void submit(){
        HashMap header = new HashMap(),map = getMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        LogUtil.showLog("预定会议 提交："+ map.toString());
        OkHttpClientManager.postAsyn(URLS.MEETING_ORDER, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                customProgressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("预定会议 提交response："+MyApplication.gson.toJson(response));
                customProgressDialog.cancel();
                MyTools.showToast(OrderMeetingActivity.this, response.getMsg());
                if(response.isSuccess()){
                    finish();
                }
            }
        },header,map);
    }



    private void upload(File file){
        if(!file.exists()){
            MyTools.showToast(this,"文件不存在，请重新选择文件");
            uploadFile = null;
            fileName = "";
            filePath = "";
            setFileIcon(fileName);
            return;
        }
        customProgressDialog.show();
        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\""+ MyApplication.AUTHORIZATION+ "\""), RequestBody.create(null, MyApplication.getInstance().getAccessToken()));
        RequestBody fileBody;
        String fileName1 = file.getName();
        fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName1)), file);
        // TODO 根据文件名设置contentType
        builder.addPart(Headers.of("Content-Disposition", "form-data; name=\""+ MyApplication.FILEKEY + "\"; filename=\"" + fileName1+ "\""), fileBody);
        final long fileSize = file.length();
        RequestBody requestBody = builder.build();
        OkHttpClientManager.getInstance().uploadPost2ServerProgress(this, URLS.UPLOAD, requestBody, new OkHttpClientManager.MyCallBack() {
            @Override
            public void onFailure(Request request, IOException e) {
                customProgressDialog.cancel();
                MyTools.showToast(OrderMeetingActivity.this,"上传失败，请重试");
                uploadFile = null;
                fileName = "";
                filePath = "";
                setFileIcon(fileName);
            }
            @Override
            public void onResponse(String json) {
                LogUtil.showLog("上传文件 json："+ json);
                BaseObject<FileUploadEntity> baseObject = MyApplication.gson.fromJson(json, new TypeToken<BaseObject<FileUploadEntity>>(){}.getType());
                if(baseObject.isSuccess()){
                    FileUploadEntity entity = baseObject.getData();
                    LogUtil.showLog("上传文件："+ entity.getPath());
                    uploadFilePath = entity.getPath();
                    submit();
                }else {
                    MyTools.showToast(OrderMeetingActivity.this,baseObject.getMsg());
                    uploadFile = null;
                    fileName = "";
                    filePath = "";
                    setFileIcon(fileName);
                    customProgressDialog.cancel();
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





    private boolean checkInput(){
        if(TextUtils.isEmpty(binding.tvStartTime.getText().toString())){
            MyTools.showToast(this,"请选择开始时间");
            return false;
        }else if(TextUtils.isEmpty(binding.tvEndTime.getText().toString())){
            MyTools.showToast(this,"请选择结束时间");
            return false;
        }else if(TextUtils.isEmpty(binding.etAddr.getText().toString())){
            MyTools.showToast(this,"请填写会议地点");
            return false;
        }
        else if(TextUtils.isEmpty(userIds)){
            MyTools.showToast(this,"请选择参会人员");
            return false;
        }
        else if(TextUtils.isEmpty(binding.etTitle.getText().toString())){
            MyTools.showToast(this,"请填写会议主题");
            return false;
        }else if(TextUtils.isEmpty(binding.etContent.getText().toString())){
            MyTools.showToast(this,"请填写会议议题");
            return false;
        }
        return true;
    }

    private HashMap getMap(){
        HashMap map = new HashMap();
        map.put("startTime", binding.tvStartTime.getText().toString());
        map.put("endTime",binding.tvEndTime.getText().toString());
        map.put("address", binding.etAddr.getText().toString());
        map.put("theme", binding.etTitle.getText().toString());
        map.put("issue",binding.etContent.getText().toString());
        map.put("conferenceRoom","");
        map.put("requirement", binding.etRequest.getText().toString());
        map.put("enclosureNmae", fileName);
        map.put("enclosureUrl", uploadFilePath);
        map.put("userIds",userIds);
        map.put("whFb","1");
        map.put("fraction",String.valueOf(fraction));//积分
        return map;
    }

    private void saveLocal(){
        OrderMeetingEntity entity = new OrderMeetingEntity();
        entity.setStartTime(binding.tvStartTime.getText().toString());
        entity.setEndTime(binding.tvEndTime.getText().toString());
        entity.setAddr(binding.etAddr.getText().toString());
        entity.setTheme(binding.etTitle.getText().toString());
        entity.setContent(binding.etContent.getText().toString());
        entity.setRequires(binding.etRequest.getText().toString());
        entity.setFilePath(filePath);
        entity.setFileName(fileName);
        entity.setUserIds(userIds);
        entity.setUserName(userName);
        entity.setFraction(fraction);
        PreferenceUtils.setPrefString("OrderMeetingEntity", MyApplication.gson.toJson(entity));
        MyTools.showToast(this,"保存成功");
    }

    private void clearLocal(){
        PreferenceUtils.setPrefString("OrderMeetingEntity", "");
    }

    private void setLocalData(){
        String localJson = PreferenceUtils.getPrefString("OrderMeetingEntity", "");
        if(TextUtils.isEmpty(localJson)) {
            setFileIcon("");
            return;
        }
        OrderMeetingEntity entity = MyApplication.gson.fromJson(localJson, OrderMeetingEntity.class);
        if(entity == null) {
            setFileIcon("");
            return;
        }
        binding.tvStartTime.setText(entity.getStartTime());
        binding.tvEndTime.setText(entity.getEndTime());
        binding.etAddr.setText(entity.getAddr());
        binding.etTitle.setText(entity.getTheme());
        binding.etContent.setText(entity.getContent());
        binding.etRequest.setText(entity.getRequires());


        userIds = entity.getUserIds();
        filePath = entity.getFilePath();
        fileName = entity.getFileName();
        userName = entity.getUserName();
        fraction = entity.getFraction();
        binding.tvJifenNum.setText(String.valueOf(fraction));
        binding.tvJoiner.setText(TextUtils.isEmpty(userName)?"请选择参会人员": userName);
        if(!TextUtils.isEmpty(filePath))uploadFile = new File(filePath.toString());
        setFileIcon(fileName);

    }



    private void initDataPicker(){
        String now = MyTools.getDateToString(System.currentTimeMillis());
        String nextYearNow = getNextYearToday(now);
        binding.tvStartTime.setText(MyTools.getDateToString(System.currentTimeMillis()));
        binding.tvEndTime.setText(MyTools.getDateToString(System.currentTimeMillis() + 2 * 3600 * 1000));
        LogUtil.showLog("initDataPicker:"+ now + "|"+nextYearNow);
        customDatePicker = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                if(chooseText == 1) binding.tvStartTime.setText(time);
                else if(chooseText ==2)binding.tvEndTime.setText(time);
            }
        }, now, nextYearNow);
        customDatePicker.setIsLoop(true);
        customDatePicker.showSpecificTime(true);
    }



    private String getNextYearToday(String now){
        int year = Integer.parseInt(now.substring(0,4));
        String nextYearToday = (year+1) + now.substring(4,now.length());
        return nextYearToday;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 88 && resultCode ==RESULT_OK){ //选择文件
            Uri uri = data.getData();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                filePath = getPath(this, uri);
            } else {//4.4以下下系统调用方法
                filePath = getRealPathFromURI(uri);
            }
            fileName = MyTools.getFileName(filePath);
            LogUtil.showLog("fileName:"+fileName);
            if(fileName.endsWith(".doc")||fileName.endsWith(".docx")||fileName.endsWith(".pdf")||fileName.endsWith(".xls")||fileName.endsWith(".xlsx")){
                uploadFile = new File(filePath.toString());
                setFileIcon(fileName);
            }else {
                MyTools.showToast(this,"不支持的文件类型！");
                uploadFile = null;
                fileName = "";
                filePath = "";
                setFileIcon(fileName);

            }
        }else if(requestCode == 89 && resultCode ==RESULT_OK){ //选择参会人员
            userIds = data.getStringExtra("ids");
            userName = data.getStringExtra("name");
            binding.tvJoiner.setText(userName);
        }
    }


    private void setFileIcon(String fileName){
        if(TextUtils.isEmpty(fileName)) {
            binding.ivFile.setImageResource(R.drawable.icon_add);
            binding.ivFile.setPadding(15,15,15,15);
            binding.ivFileDel.setVisibility(View.GONE);
            binding.tvFileName.setText("");
            return;
        }
        if(fileName.endsWith(".doc")||fileName.endsWith(".docx")){
           binding.ivFile.setImageResource(R.drawable.icon_doc);
        }else if(fileName.endsWith(".pdf")){
            binding.ivFile.setImageResource(R.drawable.icon_file_pdf);
        }else if(fileName.endsWith(".xls")||fileName.endsWith(".xlsx")){
            binding.ivFile.setImageResource(R.drawable.icon_file_excel);
        }
        binding.ivFile.setPadding(0,0,0,0);
        binding.ivFileDel.setVisibility(View.VISIBLE);
        binding.tvFileName.setText("("+fileName+")");
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(null!=cursor&&cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
