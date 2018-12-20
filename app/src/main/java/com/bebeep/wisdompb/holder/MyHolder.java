package com.bebeep.wisdompb.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.treeview.model.TreeNode;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.UserInfo;

public class MyHolder extends TreeNode.BaseNodeViewHolder<UserInfo> {
    private FrameLayout fl_img;
    private ImageView iv_left,iv_check;
    private TextView tv_content;
    private int padding;

    public MyHolder(Context context) {
        super(context);
        padding = MyTools.dip2px(context,8);
    }

    @Override
    public View createNodeView(final TreeNode node, UserInfo info) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_choose_joiner, null, false);
//        fl_img = view.findViewById(R.id.fl_img);
//        iv_left = view.findViewById(R.id.iv_left);
        iv_check = view.findViewById(R.id.iv_check);
        tv_content = view.findViewById(R.id.tv_content);

        tv_content.setText(info.getName());

        if(node.isLeaf()){
            iv_left.setImageResource(R.drawable.icon_head);
            iv_left.setClickable(false);
            iv_left.setPadding(0,0,0,0);
        }else {
            iv_left.setImageResource(R.drawable.icon_arrow_down);
            iv_left.setClickable(true);
            iv_left.setPadding(padding,padding,padding,padding);
        }

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tView.toggleNode(node);
            }
        });



        return view;
    }


    @Override
    public void toggle(boolean active) {
        iv_left.setRotation(active?0:-90);
    }

    @Override
    public void toggleSelectionMode(boolean editModeEnabled) {
        super.toggleSelectionMode(editModeEnabled);
    }
}
