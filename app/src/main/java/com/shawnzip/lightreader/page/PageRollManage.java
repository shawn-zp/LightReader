package com.shawnzip.lightreader.page;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.units.ReaderTools;

public class PageRollManage extends PageToolsManage {
	Thread rollThread;

	public PageRollManage(PageActivity activity) {
		super(activity);
		view = new RelativeLayout(activity);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopRoll();
			}
		});
	}
	
	public void startRoll() {
		PagePreferences.pageRolling = true;
		if(null == rollThread || !rollThread.isAlive()) {
			rollThread = new Thread() {
				public void run() {
					while(PagePreferences.pageRolling) {
						try {
							Thread.sleep(pageActivity.pagePreferences.getPageRollSpeed());
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(PagePreferences.pageRolling)
							pageActivity.pageRool();
					}
				}
			};
			rollThread.start();
		}
		ReaderTools.showToast(pageActivity, pageActivity.getResources().getString(R.string.page_auto_roll_star));
	}
	
	public void stopRoll() {
		PagePreferences.pageRolling = false;
		view.setVisibility(View.GONE);
		ReaderTools.showToast(pageActivity, pageActivity.getResources().getString(R.string.page_auto_roll_stop));
	}

}
