package com.shawnzip.lightreader.page;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.units.ReaderTools;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PageRollSpeedManage extends PageToolsManage{
	private View menuView;
	private SeekBar seekBar;
	
	public PageRollSpeedManage(PageActivity activity) {
		super(activity);
		
		LayoutInflater inflater = (LayoutInflater) pageActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
		view = (RelativeLayout)inflater.inflate(R.layout.layout_page_roll_speed, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		menuView = view.findViewById(R.id.menu_frame);
		seekBar = (SeekBar)view.findViewById(R.id.page_roll_speed);
		int progress = (int)(pageActivity.pagePreferences.getPageRollSpeed() * 0.001);
		seekBar.setProgress(progress);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			Toast toast;
			String speedUnit = pageActivity.getResources().getString(R.string.page_roll_speed_unit);

			private void showInfo(String info) {
				if(null == toast) {
					toast = ReaderTools.getToast(pageActivity, info);
			    	toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP, 0, 10);
				}
				toast.setText(info);
		    	toast.show();
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				int speed = (arg0.getProgress() + PagePreferences.minPageRollSpeed) * 1000;
		  		pageActivity.updatePageRollSpeed(speed);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				int position = arg0.getProgress() + PagePreferences.minPageRollSpeed;
				showInfo(position+speedUnit);
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				int position = arg1 + PagePreferences.minPageRollSpeed;
				showInfo(position+speedUnit);
			}
		});

		view.setOnClickListener(viewOnClickListener);
		menuView.setOnClickListener(viewOnClickListener);
	}
	
	private OnClickListener viewOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == view) {
				view.setVisibility(View.GONE);
			}else if(v == menuView) {
				
			}
		}
	};
}
