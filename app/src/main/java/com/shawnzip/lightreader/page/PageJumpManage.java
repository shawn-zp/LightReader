package com.shawnzip.lightreader.page;

import java.text.DecimalFormat;

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

public class PageJumpManage extends PageToolsManage{
	private View jumpView;
	private SeekBar seekBar;
	
	public PageJumpManage(PageActivity activity) {
		super(activity);
		
		LayoutInflater inflater = (LayoutInflater) pageActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
		view = (RelativeLayout)inflater.inflate(R.layout.layout_page_jump, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		jumpView = view.findViewById(R.id.menu_frame);
		seekBar = (SeekBar)view.findViewById(R.id.pageJump);
		seekBar.setProgress(pageActivity.pagefactory.getBookmarkLocation()*1000/pageActivity.pagefactory.getBookLength());
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			Toast toast;

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
				int location = (int)(arg0.getProgress()*0.001*pageActivity.pagefactory.getBookLength());
		  		pageActivity.jumpPage(location);	
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				float position = arg0.getProgress()*0.1f;
				showInfo(new DecimalFormat("#0.0").format(position)+"%");
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				float position = arg1*0.1f;
				showInfo(new DecimalFormat("#0.0").format(position)+"%");
			}
		});

		view.setOnClickListener(viewOnClickListener);
		jumpView.setOnClickListener(viewOnClickListener);
	}
	
	private OnClickListener viewOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == view) {
				view.setVisibility(View.GONE);
			}else if(v == jumpView) {
				
			}
		}
	};
}
