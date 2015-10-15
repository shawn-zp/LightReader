package com.shawnzip.lightreader.page;

import com.shawnzip.lightreader.PageActivity;

import android.view.View;
import android.widget.RelativeLayout;

public class PageToolsManage {
	protected PageActivity pageActivity;

	public RelativeLayout view;

	public PageToolsManage(PageActivity activity) {
		this.pageActivity = activity;
	}

	// 返回触发
	public void goBack() {
		view.setVisibility(View.GONE);
	}
}
