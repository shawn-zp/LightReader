package com.shawnzip.lightreader.page;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.R;

public class PageBackgroundManage extends PageToolsManage {

	private Button bgImageOption;		// 背景图选项卡
	private Button bgColorOption;		// 背景色选项卡

	private View bgImageView;			// 背景图选择视图

	private LinearLayout bgColorView;	// 背景色选择视图
	private PaletteView paletteView;	// 调色板
	private ColorView colorView;		// 背景色

	private final int[] bgResIds = new int[]{R.drawable.page_bg_1, R.drawable.page_bg_2, R.drawable.page_bg_3,
			R.drawable.page_bg_4, R.drawable.page_bg_5, R.drawable.page_bg_6};	// 图片按钮背景资源id
	private final int[] textColors = new int[]{0xFF3D3D33, 0xFFC7C7C7, 0xFF002E38,			// 文本颜色
			0xFF343434, 0xFF442C00, 0xFF445469};

	public PageBackgroundManage(PageActivity activity) {
		super(activity);

		LayoutInflater inflater = (LayoutInflater) pageActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (RelativeLayout)inflater.inflate(R.layout.layout_page_background, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		bgImageOption = (Button)view.findViewById(R.id.page_bg_image_option);
		bgColorOption = (Button)view.findViewById(R.id.page_bg_color_option);
		bgImageView = view.findViewById(R.id.page_bg_image);
		bgColorView = (LinearLayout)view.findViewById(R.id.page_bg_color);

		paletteView = new PaletteView(pageActivity);
		colorView = new ColorView(pageActivity, pageActivity.pagePreferences.getBackColor());
		bgColorView.addView(paletteView);
		bgColorView.addView(colorView);

		initBgImages();

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
					pageActivity.updateBackground(-1, colorView.interpColor(unit));
					return true;
				}else
					return false;
			}
		});

		view.setOnClickListener(viewOnClickListener);
		bgImageView.setOnClickListener(viewOnClickListener);
		bgColorView.setOnClickListener(viewOnClickListener);
		bgImageOption.setOnClickListener(viewOnClickListener);
		bgColorOption.setOnClickListener(viewOnClickListener);
	}

	private OnClickListener viewOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == view) {
				view.setVisibility(View.GONE);
			}else if(v == bgImageView || v == bgColorView) {

			}else if(v == bgImageOption) {
				bgImageOption.setBackgroundResource(R.drawable.menu_option_bg);
				bgColorOption.setBackgroundResource(R.drawable.menu_option_bg_false);
				bgColorView.setVisibility(View.INVISIBLE);
				bgImageView.setVisibility(View.VISIBLE);
			}else if(v == bgColorOption) {
				bgColorOption.setBackgroundResource(R.drawable.menu_option_bg);
				bgImageOption.setBackgroundResource(R.drawable.menu_option_bg_false);
				bgImageView.setVisibility(View.INVISIBLE);
				bgColorView.setVisibility(View.VISIBLE);
			}
		}
	};

	private OnClickListener bgOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int index = Integer.parseInt(v.getTag().toString());
			if(index < bgResIds.length)
				pageActivity.updateBackground(bgResIds[index], pageActivity.pagePreferences.getBackColor());
			if(index < textColors.length)
				pageActivity.updateTextColor(textColors[index]);
		}
	};

	private void initBgImages() {
		int[] bgIds = new int[]{R.id.page_bg_1, R.id.page_bg_2, R.id.page_bg_3,
				R.id.page_bg_4, R.id.page_bg_5, R.id.page_bg_6};			// 图片按钮id

		for(int i=0,size=bgIds.length;i<size;i++) {
			ImageButton bgButton = (ImageButton)view.findViewById(bgIds[i]);
			bgButton.setTag(""+i);
			bgButton.setOnClickListener(bgOnClickListener);
		}
	}
}

