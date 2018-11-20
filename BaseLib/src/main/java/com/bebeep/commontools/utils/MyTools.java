package com.bebeep.commontools.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bebeep.commontools.bean.WXPayInfo;
import com.bebeep.commontools.views.ToastUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.FileNameMap;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Decoder.BASE64Encoder;

/**
 * Created by Administrator on 2016/9/18.
 */
public class MyTools {
    private static final double EARTH_RADIUS = 6378137.0;
    private static long lastClickTime = 0;


    /**
     * 保留小数点后两位
     * @param numStr
     * @return
     */
    public static String formatDecimal(String numStr){
        String str;
        try {
            double num = Double.parseDouble(numStr);
            str = new DecimalFormat("#.00").format(num);
        }catch (Exception e){
            str = "0.00";
        }
        return str;
    }

    /**
     * 获取导航栏高度
     * @param context
     * @return
     */
    public static int getDaoHangHeight(Context context) {
        int result = 0;
        int resourceId=0;
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid!=0){
            resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        }else
            return 0;
    }


    /**
     * 调换集合中两个指定位置的元素, 若两个元素位置中间还有其他元素，需要实现中间元素的前移或后移的操作。
     * @param list 集合
     * @param oldPosition 需要调换的元素
     * @param newPosition 被调换的元素
     * @param <T>
     */
    public static <T> void swapList(List<T> list, int oldPosition, int newPosition){
        if(null == list){
            return;
        }
        T tempElement = list.get(oldPosition);

        // 向前移动，前面的元素需要向后移动
        if(oldPosition < newPosition){
            for(int i = oldPosition; i < newPosition; i++){
                list.set(i, list.get(i + 1));
            }
            list.set(newPosition, tempElement);
        }
        // 向后移动，后面的元素需要向前移动
        if(oldPosition > newPosition){
            for(int i = oldPosition; i > newPosition; i--){
                list.set(i, list.get(i - 1));
            }
            list.set(newPosition, tempElement);
        }
    }

    /**
     * 计算中文字符
     *
     * @param sequence
     * @return
     */
    public static int countChineseChar(CharSequence sequence) {

        if (TextUtils.isEmpty(sequence)) {
            return 0;
        }
        int charNum = 0;
        for (int i = 0; i < sequence.length(); i++) {
            char word = sequence.charAt(i);
            if (isChineseChar(word)) {//中文
                charNum++;
            }
        }
        return charNum;
    }

    /**

     * 判断是否是中文
     * @param c
     * @return
     */
    public static boolean isChineseChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    //文件转换为base64字符串
    public static String encodeBase64File(String path) {
        File file = new File(path.replace("file://",""));
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return new BASE64Encoder().encode(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    /**
     * 获取网络状态
     * @param context
     * @return
     */
    public static boolean getNetStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        return activeInfo!=null;
    }


    /**
     * 是否快速连点2次
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= 1000) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }

    public static String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    // 解析微信支付返回xml
    public static WXPayInfo parseXml(String xml, Context context) {
        ByteArrayInputStream tInputStringStream = null;
        WXPayInfo wxPayInfo = null;
        try {
            if (xml != null && !xml.trim().equals("")) {
                tInputStringStream = new ByteArrayInputStream(xml.getBytes());
            }
        } catch (Exception exception) {
            // TODO: handle exception
            Toast.makeText(context, "获取数据失败", Toast.LENGTH_SHORT).show();
        }
        XmlPullParser parser = Xml.newPullParser();
        try {
            parser.setInput(tInputStringStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:// 文档开始事件,可以进行数据初始化处理
                        wxPayInfo = new WXPayInfo();
                        break;
                    case XmlPullParser.START_TAG:// 开始元素事件
                        if (parser.getName().equals("appid")) {
                            eventType = parser.next();
                            wxPayInfo.setAppId(parser.getText());
                        } else if (parser.getName().equals("partnerid")) {
                            eventType = parser.next();
                            wxPayInfo.setPartnerId(parser.getText());
                        } else if (parser.getName().equals("prepayid")) {
                            eventType = parser.next();
                            wxPayInfo.setPrepayId(parser.getText());
                        } else if (parser.getName().equals("noncestr")) {
                            eventType = parser.next();
                            wxPayInfo.setNonceStr(parser.getText());
                        } else if (parser.getName().equals("sign")) {
                            eventType = parser.next();
                            wxPayInfo.setSign(parser.getText());
                        } else if (parser.getName().equals("timestamp")) {
                            eventType = parser.next();
                            wxPayInfo.setTimeStamp(parser.getText());
                        } else if (parser.getName().equals("package")) {
                            eventType = parser.next();
                            wxPayInfo.setPackageValue(parser.getText());
                        }
                        break;
                    case XmlPullParser.END_TAG:

                        break;
                }
                eventType = parser.next();
            }
            tInputStringStream.close();
            return wxPayInfo;
        } catch (XmlPullParserException exception) {
            // TODO Auto-generated catch block
            exception.printStackTrace();
        } catch (IOException exception) {
            // TODO Auto-generated catch block
            exception.printStackTrace();
        }
        return null;
    }

    public static String getSLT(String url) {
        if (url.endsWith(".mp4")) {
            return url.replace(".mp4", ".png");
        } else if (url.endsWith("MP4")) {
            return url.replace(".MP4", ".png");
        } else if (url.endsWith(".rmvb")) {
            return url.replace(".rmvb", ".png");
        } else if (url.endsWith(".RMVB")) {
            return url.replace(".RMVB", ".png");
        } else if (url.endsWith(".MOV")) {
            return url.replace(".MOV", ".png");
        } else if (url.endsWith(".mov")) {
            return url.replace(".mov", ".png");
        } else if (url.endsWith(".3gp")) {
            return url.replace(".3gp", ".png");
        } else if (url.endsWith(".3GP")) {
            return url.replace(".3GP", ".png");
        } else if (url.endsWith(".avi")) {
            return url.replace(".avi", ".png");
        } else if (url.endsWith(".AVI")) {
            return url.replace(".AVI", ".png");
        } else {
            return "nmmp";
        }
    }
    public static double change(double a){
        return a * Math.PI  / 180;
    }

    public static double changeAngle(double a){
        return a * 180 / Math.PI;
    }

    //获取网络视频的缩略图
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static Bitmap createVideoThumbnail(String url, int width, int height) {

        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

    //软键盘出现；
    public static void showKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    //隐藏软键盘
    public static void hideKeyboard(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //四舍五入
    public static int floatToInt(float f){
        int i = 0;
        if(f>0) //正数
            i = (int) ((f*10 + 5)/10);
        else if(f<0) //负数
            i = (int) ((f*10 - 5)/10);
        else i = 0;
        return i;
    }


    //优化时间格式
    public static String formateTimeString(String time){
        long time1 = getTimeStamp(time, "yyyy-MM-dd HH:mm:ss");
        long time2 = System.currentTimeMillis()/1000;
        long dTime = (time2 - time1);
        if(dTime<5 * 60){ //5分钟
            return "刚刚";
        }else if(dTime>= 5*60 && dTime< 60 * 60){//5分钟-1小时
            return dTime/60+"分钟前";
        }else if(dTime>=60 *60 && dTime<60 * 60 *24){//1天以内
            return dTime/3600+"小时前";
        }else if(dTime>=3600 * 24 && dTime< 3600 * 24 * 7){//1-7天
            if(dTime%86400<43200){
                return dTime/86400+"天前";
            }else{
                return (dTime/86400+1)+"天前";
            }
        }else{
            return ""+time.substring(0,10);
        }
    }

    //优化时间格式
    public static String formateTimeLong(long time){
        long time1 = time;
        long time2 = System.currentTimeMillis()/1000;
        long dTime = (time2 - time1);
        try {
            if(dTime<5 * 60){ //5分钟
                return "刚刚";
            }else if(dTime>= 5*60 && dTime< 60 * 60){//5分钟-1小时
                return dTime/60+"分钟前";
            }else if(dTime>=60 *60 && dTime<60 * 60 *24){//1天以内
                return dTime/3600+"小时前";
            }else if(dTime>=3600 * 24 && dTime< 3600 * 24 * 7){//1-7天
                if(dTime%86400<43200){
                    return dTime/86400+"天前";
                }else{
                    return (dTime/86400+1)+"天前";
                }
            }else{
                return ""+getDateToString1(time1*1000).replaceAll("-","·");
            }
        }catch (Exception e){
            return ""+getDateToString1(time1*1000).replaceAll("-","·");
        }
    }


    /*
 * Java文件操作 获取文件扩展名
 *
 */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return "."+filename.substring(dot + 1);
            }
        }
        return filename;
    }


    /**
     * 正则表达式验证邮箱格式
     * */
    public static boolean checkEmail(String email){
        Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 获得可用存储空间
     * @return 可用存储空间（单位b）
     */
    public static long getFreeSpace() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;//区块的大小
        long totalBlocks;//区块总数
        long availableBlocks;//可用区块的数量
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
            availableBlocks = stat.getAvailableBlocks();
        }
        //String totalSpace = Formatter.formatFileSize(MyApplication.getInstance().getApplicationContext(), blockSize * totalBlocks);
        //String availableSpace = Formatter.formatFileSize(MyApplication.getInstance().getApplicationContext(), blockSize * availableBlocks);
        //Log.e(LOG_TAG, "totalSpace：" + totalSpace + "...availableSpace：" + availableSpace);
        Log.e("LOG", "totalSpace：" + blockSize * totalBlocks + "...availableSpace：" + blockSize * availableBlocks);
        return blockSize * availableBlocks;
    }



    //获取文件路径
    public static String getRealFilePath(Context context,Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 获取视频的缩略图
     * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * @param videoPath 视频的路径
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 196, 196,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    //获取手机操作系统版本
    public static int getOSVersion() {
        String a = Build.VERSION.RELEASE.substring(0, 1);
        int version = Integer.parseInt(a);
        return version;
    }


    //字符串补0
    public static String putZero(int num) {
        if (num < 10) {
            return "0" + num;
        }
        return num + "";
    }


    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider

            if (isExternalStorageDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);

                final String[] split = docId.split(":");

                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {

                    return Environment.getExternalStorageDirectory() + "/"

                            + split[1];

                }

                // TODO handle non-primary volumes

            }

            // DownloadsProvider

            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(

                        Uri.parse("content://downloads/public_downloads"),

                        Long.valueOf(id));

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

                return getDataColumn(context, contentUri, selection,

                        selectionArgs);

            }

        }

        // MediaStore (and general)

        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address

            if (isGooglePhotosUri(uri))

                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);

        }

        // File

        else if ("file".equalsIgnoreCase(uri.getScheme())) {

            return uri.getPath();

        }

        return null;

    }


    public static String getDataColumn(Context context, Uri uri,

                                       String selection, String[] selectionArgs) {

        Cursor cursor = null;

        final String column = "_data";

        final String[] projection = {column};

        try {

            cursor = context.getContentResolver().query(uri, projection,

                    selection, selectionArgs, null);

            if (cursor != null && cursor.moveToFirst()) {

                final int index = cursor.getColumnIndexOrThrow(column);

                return cursor.getString(index);

            }

        } finally {

            if (cursor != null)

                cursor.close();

        }

        return null;

    }


    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }


    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }


    //获取今天的日期
    public static String day_today() {
        Date date = new Date();//取时间
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
        String dateString = formatter.format(date);

        return dateString;
    }

    //获取明天的日期
    public static String day_tomorrow() {
        Date date = new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
        String dateString = formatter.format(date);

        return dateString;
    }


    //计算最近的12个月
    public static String[] getLast12Months() {
        String[] last12Months = new String[12];
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1); //要先+1,才能把本月的算进去</span>
        for (int i = 0; i < 12; i++) {
            if (cal.get(Calendar.MONTH) + i > 12) {
                last12Months[i] = cal.get(Calendar.YEAR) + 1 + "-" + fillZero((cal.get(Calendar.MONTH) + i - 12 * 1));
            } else {
                last12Months[i] = cal.get(Calendar.YEAR) + "-" + fillZero((cal.get(Calendar.MONTH) + i));
            }
        }

        return last12Months;
    }

    //补0
    public static String fillZero(int i) {
        String str = "";
        if (i > 0 && i < 10) {
            str = "0" + i;
        } else {
            str = "" + i;
        }
        return str;

    }

    //根据日期换算星期
    public static List<String> getMonthAllDate() {
        List<String> list = new ArrayList<String>();
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = getMonthStart(d);
        Date monthEnd = getMonthEnd(d);
        while (!date.after(monthEnd)) {
            list.add(sdf.format(date));
            date = getNext(date);
        }
        return list;
    }


    /**
     * 取得当月天数
     * */
    public static int getCurrentMonthLastDay()
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 得到指定月的天数
     * */
    public static int getMonthLastDay(int year, int month)
    {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    //当月月初的时间
    private static Date getMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (1 - index));
        return calendar.getTime();
    }

    //当月月末的时间
    private static Date getMonthEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        int index = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DATE, (-index));
        return calendar.getTime();
    }

    private static Date getNext(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    /**
     *
     * 获取制定日期所属月份的每一天
     * @param strdate
     * @return
     */
    public static List<String> dayReport(String strdate) {
        List<String> list = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Date date = null;
        try {
            date = formatter.parse(strdate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);//month 为指定月份任意日期
        int year = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int dayNumOfMonth = getDaysByYearMonth(year, m);
        cal.set(Calendar.DAY_OF_MONTH, 1);// 从一号开始
        for (int i = 0; i < dayNumOfMonth; i++, cal.add(Calendar.DATE, 1)) {
            Date d = cal.getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String df = simpleDateFormat.format(d);
            list.add(df);
        }

        return list;
    }

    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    // 时间戳转换成字符
    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sf.format(d);
    }

    // 时间戳转换成字符
    public static String getDateToString(long time, String formateType) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(formateType);
        return sf.format(d);
    }

    // 时间戳转换成字符
    public static String getDateToString1(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }

    //字符串转时间戳
    public static long getTimeStamp(String user_time, String style) {
        long l = 0;
        SimpleDateFormat sdf = new SimpleDateFormat(style);
        Date d;
        try {
            d = sdf.parse(user_time);
            l = d.getTime();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return l;
    }


    /**
     * 根据日期 找到对应日期的 星期
     */
    public static String getDayOfWeekByDate(String date) {
        String dayOfweek = "-1";
        try {
            SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
            Date myDate = myFormatter.parse(date);
            SimpleDateFormat formatter = new SimpleDateFormat("E");
            String str = formatter.format(myDate);
            dayOfweek = str;

        } catch (Exception e) {
        }
        return dayOfweek;
    }

    // 获取以今天开始的一个月的时间和星期
    public static List<String> getMonthDate() {
        List<String> list = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 31; i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM/dd E");
            list.add(dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }
        return list;
    }

    // 获取当前经纬度
    public static float getLat(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "LatAndLng", Activity.MODE_PRIVATE);
        return preferences.getFloat("lat", 0);
    }

    // 获取当前经纬度
    public static float getLng(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "LatAndLng", Activity.MODE_PRIVATE);
        return preferences.getFloat("lng", 0);
    }

    // 获取当前城市
    public static String getCity(Context context,String key) {
        SharedPreferences preferences = context.getSharedPreferences("City",
                Activity.MODE_PRIVATE);
        return preferences.getString(key, "");
    }


    // 保存频道
    public static void saveChannal(Context context, String channal,String key) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(key, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("channel", channal);
        editor.commit();
    }

    // 获取保存的频道
    public static String getChannal(Context context,String key) {
        SharedPreferences preferences = context.getSharedPreferences(key,Activity.MODE_PRIVATE);
        return preferences.getString("channel", "");
    }

    // 保存当前经纬度
    public static void saveLatandLng(Context context, double lat, double lng) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences("LatAndLng", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putFloat("lat", (float) lat);
        editor.putFloat("lng", (float) lng);
        editor.commit();
    }

    // 保存当前城市
    public static void saveCity(Context context, String city,String key) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "City", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(key, city);
        editor.commit();
    }



    // 文本复制功能
    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        showToast(context, "复制成功！");
    }

    // 粘贴
    public static String paste(Context context) {
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        return cmb.getText().toString().trim();
    }

    // 获取设备的高度
    public static int getHight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    // 获取设备的宽度
    public static int getWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    // 字体加粗
    public static void jiaCu(TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }



    // 版本名
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    // 版本号
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 公用Toast，显示在屏幕上方
     */
    public static void showToast(Context context, String content) {
        ToastUtil.showShortToast(context, content);
    }



    /**
     * 调用接口失败时，
     * 先判断是否网络断开，否则给出其他提示
     */
    public static void showErrorToast(Context context, String errorStr,String defStr) {
        if(!getNetStatus(context))ToastUtil.showShortToast(context, errorStr);
        else {
            if(!TextUtils.isEmpty(defStr))ToastUtil.showShortToast(context, defStr);
        }
    }

    // 同一个text中设置不同字体
    public static SpannableStringBuilder setCustomText(Context context,
                                                       String text, int start, int end) {

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int testsize = (int) (15 * dm.density);

        SpannableStringBuilder spanBuilder = new SpannableStringBuilder(text);
        spanBuilder.setSpan(new TextAppearanceSpan(null, 0, testsize, null,
                null), start, end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spanBuilder;
    }

    // 验证座机号码
    public static boolean isFixedPhone(String fixedPhone) {
        String reg = "(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|"
                + "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";
        return Pattern.matches(reg, fixedPhone);
    }




    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm",
                Locale.getDefault());
        String currenttime = sdf.format(new Date());
        currenttime = currenttime.substring(2, currenttime.length());
        currenttime = currenttime.replaceAll("-", "");
        currenttime = currenttime.replaceAll(" ", "");
        currenttime = currenttime.replaceAll(":", "");
        return currenttime;
    }

    // 获取用户ip
    public static String getIP(Context context) {
        String ip;
        // 获取wifi服务
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 判断wifi是否开启
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return intToIp(ipAddress);
        } else {
            //			return getLocalIpAddress();
            return "0.0.0.0";
        }
    }

    //物理地址转化为ip
    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    //获取本地物理地址
    @SuppressLint("LongLogTag")
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }


    // 清除用户信息
    public static void clearInfo(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.clear().commit();
    }


    // 获取用户id
    @SuppressLint("ShowToast")
    public static String getOpenId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        return preferences.getString("openid", "");
    }
    // 获取用户id
    public static String getRefreshToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "RefreshToken", Activity.MODE_PRIVATE);
        return preferences.getString("refreshtoken", "");
    }
    // 获取用户money
    @SuppressLint("ShowToast")
    public static String getMoney(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        return preferences.getString("money", "0");
    }
    // 获取用户userid
    @SuppressLint("ShowToast")
    public static String getIdCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        return preferences.getString("idCode", "");
    }
    // 获取头像
    @SuppressLint("ShowToast")
    public static String getHead(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        return preferences.getString("headimgurl", "");
    }

    // 获取昵称
    @SuppressLint("ShowToast")
    public static String getNickName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        return preferences.getString("nickname", "未登陆");
    }

    // 获取邀请人idCode
    public static String getBindCode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "bindCode", Activity.MODE_PRIVATE);
        return preferences.getString("bindcode", "");
    }

    //保存邀请人idCode
    public static void saveBindCode(Context context,String bindCode){
        SharedPreferences mySharedPreferences = context.getSharedPreferences("bindCode", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("bindcode", bindCode);
        editor.commit();
    }

    // 清除邀请人idCode
    public static void clearBindCode(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "bindCode", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.clear().commit();
    }

    //保存新闻页面字体大小
    public static void saveFontSize(Context context,int size){
        SharedPreferences mySharedPreferences = context.getSharedPreferences("FontSize", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt("FontSize", size);
        editor.commit();
    }

    // 获取新闻页面字体大小
    public static int getFontSize(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "FontSize", Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt("FontSize", 3);
    }

    // 判断是否登陆
    @SuppressLint("ShowToast")
    public static boolean isLogined(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        if (TextUtils.isEmpty(preferences.getString("openid", ""))) {
            return false;
        } else {
            return true;
        }
    }


    // 保存用户信息
    public static void saveUserInfo(Context context, String openid, String headimgurl,String nickname,String idCode,String money) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("openid", openid);
        editor.putString("headimgurl", headimgurl);
        editor.putString("nickname", nickname);
        editor.putString("idCode", idCode);
        editor.putString("money", money);
        editor.commit();
    }

    //保存cookie
    public static void saveRefreshToken(Context context,String refreshtoken){
        SharedPreferences mySharedPreferences = context.getSharedPreferences("RefreshToken", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("refreshtoken", refreshtoken);
        editor.commit();
    }

    //保存是否接收推送
    public static void saveIsPush(Context context,boolean isPush){
        SharedPreferences mySharedPreferences = context.getSharedPreferences("Push", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean("ispush", isPush);
        editor.commit();
    }
    //保存是否接收推送
    public static boolean getIsPush(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "Push", Activity.MODE_PRIVATE);
        return preferences.getBoolean("ispush",true);
    }

    // 计算两个经纬度之间的距离
    public static String getDistance(double longitude1, double latitude1,
                                     double longitude2, double latitude2) {
        double Lat1 = rad(latitude1);
        double Lat2 = rad(latitude2);
        double a = Lat1 - Lat2;
        double b = rad(longitude1) - rad(longitude2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(Lat1) * Math.cos(Lat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;

        if (s < 1000) {
            return s + "m";
        } else if (s >= 1000) {
            return new DecimalFormat("#.00").format(s / 1000) + "km"; // 保留小数点后2位
        } else {
            return "";
        }
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * progressbar
     */
    public static void createProgressBar(Context context, FrameLayout layout) {
        // FrameLayout layout = (FrameLayout)
        // this.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ProgressBar mProBar = new ProgressBar(context);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.VISIBLE);
        layout.addView(mProBar);
    }


    //是否连接WIFI
    public static boolean isWifiConnected(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(wifiNetworkInfo.isConnected())
        {
            return true ;
        }
        return false ;
    }

    // 是否联网
    public static boolean NetWorkStatus(Context context) {
        boolean netSataus = false;
        ConnectivityManager cwjManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        cwjManager.getActiveNetworkInfo();
        if (cwjManager.getActiveNetworkInfo() != null) {
            netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
        }
        return netSataus;
    }

    // 根据日期获取星期
    public static String getWeek(String pTime) {
        String Week = "周";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += "日";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += "一";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "二";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += "三";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += "四";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += "五";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += "六";
        }
        return Week;
    }

    // 获取当前时间
    public static String getCurrentTime() {
        SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dff.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String ee = dff.format(new Date());
        return ee;
    }

    /**
     * 只返回年月
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String currenttime = sdf.format(new Date());
//        String date = currenttime.substring(0,4)+"年"+currenttime.substring(5,7)+"月";
        return currenttime;
    }


    // 当前时间转换成订单号
    public static String getMenuNum(Context context, int num) {
        SharedPreferences preferences = context.getSharedPreferences(
                "userInfo", Activity.MODE_PRIVATE);
        String id = "" + preferences.getInt("id", -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
        String s = sdf.format(new Date());
        int in = id.length();
        s = id + s;
        for (int i = 6; i > in; in++) {
            s = "0" + s;
        }
        return s + num;
    }

    // 充值订单号
    public static String rechargeNum() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
        return sdf.format(new Date());
    }

    // 判断手机号码是否正确
    public static boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
            return true;
        }
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式 - 正则表达式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * 对List对象按照某个成员变量进行排序
     *
     * @param list      List对象
     * @param sortField 排序的属性名称
     * @param sortMode  排序方式：ASC(升序)，DESC(降序) 任选其一
     */
    public static <T> void sortList(List<T> list, final String sortField,
                                    final String sortMode) {
        if (list == null || list.size() < 2) {
            return;
        }
        Collections.sort(list, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                try {
                    Class clazz = o1.getClass();
                    Field field = clazz.getDeclaredField(sortField); // 获取成员变量
                    field.setAccessible(true); // 设置成可访问状态
                    String typeName = field.getType().getName().toLowerCase(); // 转换成小写

                    Object v1 = field.get(o1); // 获取field的值
                    Object v2 = field.get(o2); // 获取field的值

                    boolean ASC_order = (sortMode == null || "ASC"
                            .equalsIgnoreCase(sortMode));

                    // 判断字段数据类型，并比较大小
                    if (typeName.endsWith("string")) {
                        String value1 = v1.toString();
                        String value2 = v2.toString();
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("short")) {
                        Short value1 = Short.parseShort(v1.toString());
                        Short value2 = Short.parseShort(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("byte")) {
                        Byte value1 = Byte.parseByte(v1.toString());
                        Byte value2 = Byte.parseByte(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("char")) {
                        Integer value1 = (int) (v1.toString().charAt(0));
                        Integer value2 = (int) (v2.toString().charAt(0));
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("int")
                            || typeName.endsWith("integer")) {
                        Integer value1 = Integer.parseInt(v1.toString());
                        Integer value2 = Integer.parseInt(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("long")) {
                        Long value1 = Long.parseLong(v1.toString());
                        Long value2 = Long.parseLong(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("float")) {
                        Float value1 = Float.parseFloat(v1.toString());
                        Float value2 = Float.parseFloat(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("double")) {
                        Double value1 = Double.parseDouble(v1.toString());
                        Double value2 = Double.parseDouble(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("boolean")) {
                        Boolean value1 = Boolean.parseBoolean(v1.toString());
                        Boolean value2 = Boolean.parseBoolean(v2.toString());
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("date")) {
                        Date value1 = (Date) (v1);
                        Date value2 = (Date) (v2);
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else if (typeName.endsWith("timestamp")) {
                        Timestamp value1 = (Timestamp) (v1);
                        Timestamp value2 = (Timestamp) (v2);
                        return ASC_order ? value1.compareTo(value2) : value2
                                .compareTo(value1);
                    } else {
                        // 调用对象的compareTo()方法比较大小
                        Method method = field.getType().getDeclaredMethod(
                                "compareTo", new Class[]{field.getType()});
                        method.setAccessible(true); // 设置可访问权限
                        int result = (Integer) method.invoke(v1,
                                new Object[]{v2});
                        return ASC_order ? result : result * (-1);
                    }
                } catch (Exception e) {
                    String err = e.getLocalizedMessage();
                    System.out.println(err);
                    e.printStackTrace();
                }

                return 0; // 未知类型，无法比较大小
            }
        });
    }
}
