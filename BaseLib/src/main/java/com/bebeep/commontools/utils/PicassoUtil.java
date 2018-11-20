package com.bebeep.commontools.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bebeep.commontools.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Bebeep
 * Time 2018/3/28 16:27
 * Email 424468648@qq.com
 * Tips
 */

public class PicassoUtil {
    private static int mDrawableId = R.drawable.defaultpic;
    private static int mWidth = 90;
    private static int mHeight = 60;

    public static void setImageUrl(Context mContext, ImageView view, String url, int drawableId,int width,int height){
        mDrawableId = drawableId;
        mWidth = width;
        mHeight = height;
        setImageUrl(mContext,view,url);
    }
    public static void setImageUrl(Context mContext, ImageView view, String url, int width,int height){
        mWidth = width;
        mHeight = height;
        setImageUrl(mContext,view,url);
    }

    public static void setImageUrl(Context mContext, ImageView view, String url, int drawableId){
        mDrawableId = drawableId;
        setImageUrl(mContext,view,url);
    }

    public static void setImageUrl(Context mContext, ImageView view, String url){
        if(view!=null){
            if(TextUtils.isEmpty(url))view.setImageResource(mDrawableId);
            else{
                Picasso.with(mContext).load(url)
                        .placeholder(mDrawableId)
                        .config(Bitmap.Config.RGB_565)
                        .resize(mWidth,  mHeight)
                        .centerCrop()
                        .error(mDrawableId)
                        .into(view);
            }
        }
    }



}
