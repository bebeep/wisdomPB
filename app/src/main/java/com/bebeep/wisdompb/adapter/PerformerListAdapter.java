package com.bebeep.wisdompb.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.CatalogEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cpf on 2018/1/16.
 */

public class PerformerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * context
     */
    public Context mContext;

    /**
     * 集合
     */
    public List<CatalogEntity> mPerformers = new ArrayList<>();

    public OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void OnTitleClick(int position);
        void OnContentClick(int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }


    public PerformerListAdapter(Context context, List<CatalogEntity> performers) {
        this.mContext = context;
        this.mPerformers = performers;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 1) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_catalog, parent, false);
            return new ContentVH(view);
        }

        return new TitleVH(LayoutInflater.from(mContext).inflate(R.layout.item_catalog_title, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        CatalogEntity performer = mPerformers.get(position);

        if (holder instanceof ContentVH) {
            ((ContentVH) holder).bindData(performer);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null)onItemClickListener.OnContentClick(position);
                }
            });
        }
        if (holder instanceof TitleVH) {
            ((TitleVH) holder).bindData(performer);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null)onItemClickListener.OnTitleClick(position);
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return mPerformers.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPerformers.get(position).getItemType();
    }


    static class TitleVH extends RecyclerView.ViewHolder {

        TextView mTv;

        public TitleVH(View itemView) {
            super(itemView);
            mTv = (TextView) itemView.findViewById(R.id.tv_top);
            itemView.setTag(true);
        }

        public void bindData(CatalogEntity performer) {
            mTv.setText(performer.getName());
        }

    }

    static class ContentVH extends RecyclerView.ViewHolder {

        TextView mTv;

        public ContentVH(View itemView) {
            super(itemView);
            mTv = (TextView) itemView.findViewById(R.id.tv_top);
            itemView.setTag(false);
        }

        public void bindData(CatalogEntity performer) {
            mTv.setText(performer.getName());
        }

    }
}
