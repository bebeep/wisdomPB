package com.bebeep.commontools.showbigimage;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bebeep.commontools.R;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.views.CustomDialog;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * 全屏展示大图(单张图片)
 */
public class ShowSingleBigImageDialog extends Dialog{
    private Dialog dialog;
    private PhotoView photoView;
    private Context context;
    private PhotoViewAttacher mAttacher;

    public ShowSingleBigImageDialog(Context context) {
        super(context);
        init(context);
    }
    public ShowSingleBigImageDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    public void init(Context context){
        this.context = context;
        dialog = new CustomDialog(context, R.style.Dialog);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.layout_show_big_image, null);
        photoView = layout.findViewById(R.id.photoview);
        photoView.setMaximumScale(2);
        mAttacher = new PhotoViewAttacher(photoView);
        mAttacher.update();

        mAttacher.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                Log.e("TAG","click:setOnViewTapListener");
                dialog.dismiss();
            }
        });

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG","click:setOnClickListener");
                dialog.dismiss();
            }
        });
        dialog.addContentView(layout, new ViewGroup.LayoutParams(MyTools.getWidth(context), MyTools.getHight(context)));



    }

    public void show(String url,int defPic){
        if(context == null || photoView == null||dialog==null) return;
        setImageUrl(photoView,url,defPic);
        dialog.show();
    }


    public void setImageUrl(ImageView view, String url, int drawableId)
    {
        if(view!=null){
            if(TextUtils.isEmpty(url))view.setImageResource(drawableId);
            else{
                Picasso.with(context).load(url)
                        .placeholder(drawableId)
                        .error(drawableId)
                        .into(view);
            }
        }
    }
}
