package com.shawnzip.lightreader.page;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.units.ReaderTools;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class PageFontManage extends PageToolsManage{

	private Button fontSettingOption;		// 字体设置选项卡
	private Button fontColorOption;			// 字体颜色选项卡

	private View fontSettingView;			// 背景图选择视图
	private SeekBar seekBar;			// 字体大小
	private SeekBar linesizeSeekBar;	// 行间距

	private LinearLayout fontColorView;	// 背景色选择视图
	private PaletteView paletteView;	// 调色板
	private ColorView colorView;		// 背景色

	public PageFontManage(PageActivity activity) {
		super(activity);

		LayoutInflater inflater = (LayoutInflater) pageActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (RelativeLayout)inflater.inflate(R.layout.layout_page_font, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		fontSettingOption = (Button)view.findViewById(R.id.page_font_setting_option);
		fontColorOption = (Button)view.findViewById(R.id.page_font_color_option);
		fontSettingView = view.findViewById(R.id.page_font_setting);
		seekBar = (SeekBar)view.findViewById(R.id.page_font);
		seekBar.setProgress(pageActivity.pagePreferences.getTextSize() - PagePreferences.minTextSize);
		linesizeSeekBar = (SeekBar)view.findViewById(R.id.page_linesize);
		linesizeSeekBar.setProgress(pageActivity.pagePreferences.getLineSize());
		fontColorView = (LinearLayout)view.findViewById(R.id.page_font_color);

		paletteView = new PaletteView(pageActivity);
		colorView = new ColorView(pageActivity, pageActivity.pagePreferences.getBackColor());
		fontColorView.addView(paletteView);
		fontColorView.addView(colorView);

		paletteView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();

				if(x <= paletteView.mWidth && y <= paletteView.mHeight) {
					float unit = y/paletteView.mHeight;
					colorView.initView(paletteView.interpColor(unit));
					return true;
				}else
					return false;
			}
		});
		colorView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();

				if(x <= colorView.mWidth && y <= colorView.mHeight) {
					float unit = y/colorView.mHeight;
					pageActivity.updateTextColor(colorView.interpColor(unit));
					return true;
				}else
					return false;
			}
		});

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
				int fontSize = PagePreferences.minTextSize + arg0.getProgress();
				pageActivity.updateFont(fontSize, pageActivity.pagePreferences.getLineSize());
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				int fontSize = PagePreferences.minTextSize + arg0.getProgress();
				showInfo(fontSize+"P");
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				int fontSize = PagePreferences.minTextSize + arg1;
				showInfo(fontSize+"P");
			}
		});
		linesizeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
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
				int lineSize = arg0.getProgress();
				pageActivity.updateFont(pageActivity.pagePreferences.getTextSize(), lineSize);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				int lineSize = arg0.getProgress();
				showInfo(lineSize+"P");
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				int lineSize = arg1;
				showInfo(lineSize+"P");
			}
		});

		view.setOnClickListener(viewOnClickListener);
		fontSettingView.setOnClickListener(viewOnClickListener);
		fontColorView.setOnClickListener(viewOnClickListener);
		fontSettingOption.setOnClickListener(viewOnClickListener);
		fontColorOption.setOnClickListener(viewOnClickListener);
	}

	private OnClickListener viewOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == view) {
				view.setVisibility(View.GONE);
			}else if(v == fontSettingView || v == fontColorView) {

			}else if(v == fontSettingOption) {
				fontSettingOption.setBackgroundResource(R.drawable.menu_option_bg);
				fontColorOption.setBackgroundResource(R.drawable.menu_option_bg_false);
				fontColorView.setVisibility(View.INVISIBLE);
				fontSettingView.setVisibility(View.VISIBLE);
			}else if(v == fontColorOption) {
				fontColorOption.setBackgroundResource(R.drawable.menu_option_bg);
				fontSettingOption.setBackgroundResource(R.drawable.menu_option_bg_false);
				fontSettingView.setVisibility(View.INVISIBLE);
				fontColorView.setVisibility(View.VISIBLE);
			}
		}
	};
}
