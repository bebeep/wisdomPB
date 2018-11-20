package com.bebeep.commontools.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bebeep.commontools.R;
import com.bigkoo.convenientbanner.holder.Holder;
import com.squareup.picasso.Picasso;

/**
 * Created by Administrator on 2017/1/20.
 */
public class NetworkImageHolderView implements Holder<String> {
    private ImageView imageView;
    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, final int position, String data) {
        if(TextUtils.isEmpty(data)){
            Picasso.with(context).load(R.drawable.icon_error).into(imageView);
        }else{
            Picasso.with(context).load(data + "")
                    .placeholder(R.drawable.defaultpic)
                    .config(Bitmap.Config.RGB_565)
                    .error(R.drawable.icon_error)
                    .into(imageView);
        }
    }
}
