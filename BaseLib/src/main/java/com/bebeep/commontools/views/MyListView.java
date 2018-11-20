package com.bebeep.commontools.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListView extends ListView {
	public MyListView(Context context) {
		super(context);
	}

	public MyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyListView(Context context, AttributeSet attrs,
					  int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	/**
	 * 重写该方法，达到使ListView适应ScrollView的效果
	 * 需要手动将外部嵌套的scrollview移至顶层
	 * sv = (ScrollView) findViewById(R.id.act_solution_4_sv);
	 * sv.smoothScrollTo(0, 0);
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
