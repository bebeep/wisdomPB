package com.bebeep.wisdompb.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.activity.ReleaseDiscoverActivity;
import com.bebeep.wisdompb.bean.ImageVideoEntity;
import com.bebeep.wisdompb.util.LogUtil;

import java.util.List;


public class GridView_ChannelAdapter extends BaseAdapter {
    private List<ImageVideoEntity> list;
    private Context context;
    private int width;

    public GridView_ChannelAdapter(Context context, List<ImageVideoEntity> list) {
        this.list = list;
        this.context = context;
        width = (MyTools.getWidth(context) - MyTools.dip2px(context,30))/3;
    }


    public void update(List<ImageVideoEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_release_discover, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.fl_content = view.findViewById(R.id.fl_content);
            viewHolder.iv_add = view.findViewById(R.id.iv_add);
            viewHolder.iv = view.findViewById(R.id.iv);
            viewHolder.iv_play = view.findViewById(R.id.iv_play);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewHolder.fl_content.getLayoutParams();
            params.width = width;
            params.height = width;
            viewHolder.fl_content.setLayoutParams(params);

            LogUtil.showLog(" width:"+ width);

            view.setTag(viewHolder);
        } else viewHolder = (ViewHolder) view.getTag();

        ImageVideoEntity entity = list.get(position);

        switch (entity.getFlag()){
            case 1://普通图片地址
                viewHolder.iv_add.setVisibility(View.GONE);
                viewHolder.iv_play.setVisibility(View.GONE);
                viewHolder.iv.setVisibility(View.VISIBLE);
                PicassoUtil.setImageUrl(context,viewHolder.iv,"file://"+ entity.getUrl(),R.drawable.default_error,width,width);
                break;
            case 2://视频缩略图
                viewHolder.iv_add.setVisibility(View.GONE);
                viewHolder.iv_play.setVisibility(View.VISIBLE);
                viewHolder.iv.setVisibility(View.VISIBLE);
                PicassoUtil.setImageUrl(context,viewHolder.iv,"file://"+ entity.getUrl(),R.drawable.default_error,width,width);
                break;
            case 3://添加按钮
                viewHolder.iv_add.setVisibility(View.VISIBLE);
                viewHolder.iv_play.setVisibility(View.GONE);
                viewHolder.iv.setVisibility(View.GONE);
                break;
        }
        return view;
    }

    static class ViewHolder {
        FrameLayout fl_content;
        ImageView iv_add,iv,iv_play;

    }
}
