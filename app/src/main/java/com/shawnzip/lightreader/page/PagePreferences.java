package com.shawnzip.lightreader.page;

import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.units.Global;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PagePreferences {

	private Context context;

	public static int minTextSize = 20;		// 最小字体大小
	public static int minBottomMargin = 10;	// 阅读界面下方间隔
	public static int systemInfoSize = 20;	// 系统信息字体大小 电量 时间 进度
	public static boolean pageRolling = false;	// 是否正在自动翻页
	public static int minPageRollSpeed = 3;	// 最小翻页速度 3秒
	public static int menuTouchSize = 100;	// 触发菜单的点击区域高度，高度为 menuTouchSize * 2

	private int textSize;		// 字体大小
	private int lineSize;		// 行间距
	private int textColor;		// 文本颜色
	private int backColor;		// 背景颜色
	private int marginWidth;	// 左右与边缘距离
	private int marginHeight;	// 上下与边缘距离
	private int background;		// 背景图片资源id
	private boolean isTranslation;	// 页面切换是否为滑动模式(翻页模式)
	private int pageRollSpeed;	// 自动翻页速度
	private int requestedOrientation;	// 是否自动方向

	private Bitmap bgBitmap;		// 背景图片

	// 加载阅读喜好
	public PagePreferences(Context context) {
		this.context = context;
		SharedPreferences pagePreferences = context.getSharedPreferences(Global.PREFERENCES_BOOK_PAGE, Context.MODE_PRIVATE);
		textSize = pagePreferences.getInt("TextSize", 30);
		lineSize = pagePreferences.getInt("LineSize", 10);
		textColor = pagePreferences.getInt("TextColor", 0xFF3D3D33);
		backColor = pagePreferences.getInt("BackColor", 0xffff9e85);
		marginWidth = pagePreferences.getInt("MarginWidth", 15);
		marginHeight = pagePreferences.getInt("MarginHeight", 20);
		background = pagePreferences.getInt("Background", R.drawable.page_bg_1);
		isTranslation = pagePreferences.getBoolean("IsTranslation", true);
		pageRollSpeed = pagePreferences.getInt("RollSpeed", 5000);
		requestedOrientation = pagePreferences.getInt("RequestedOrientation", ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		bgBitmap = BitmapFactory.decodeResource(context.getResources(), background);
	}

	// 保存阅读喜好
	public void save() {
		SharedPreferences pagePreferences = context.getSharedPreferences(Global.PREFERENCES_BOOK_PAGE, Context.MODE_PRIVATE);
		Editor editor = pagePreferences.edit();
		editor.putInt("TextSize", textSize);
		editor.putInt("LineSize", lineSize);
		editor.putInt("TextColor", textColor);
		editor.putInt("BackColor", backColor);
		editor.putInt("MarginWidth", marginWidth);
		editor.putInt("MarginHeight", marginHeight);
		editor.putInt("Background", background);
		editor.putBoolean("IsTranslation", isTranslation);
		editor.putInt("RollSpeed", pageRollSpeed);
		editor.putInt("RequestedOrientation", requestedOrientation);
		editor.commit();
	}

	// 设置背景图片
	public void setBackground(int background) {
		this.background = background;
		if(null != bgBitmap) {
			bgBitmap.recycle();
		}
		bgBitmap = null;
		bgBitmap = BitmapFactory.decodeResource(context.getResources(), background);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public int getLineSize() {
		return lineSize;
	}

	public void setLineSize(int lineSize) {
		this.lineSize = lineSize;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getBackColor() {
		return backColor;
	}

	public void setBackColor(int backColor) {
		this.backColor = backColor;
	}

	public int getMarginWidth() {
		return marginWidth;
	}

	public void setMarginWidth(int marginWidth) {
		this.marginWidth = marginWidth;
	}

	public int getMarginHeight() {
		return marginHeight;
	}

	public void setMarginHeight(int marginHeight) {
		this.marginHeight = marginHeight;
	}

	public boolean isTranslation() {
		return isTranslation;
	}

	public void setTranslation(boolean isTranslation) {
		this.isTranslation = isTranslation;
	}

	public Bitmap getBgBitmap() {
		return bgBitmap;
	}

	public void setPageRollSpeed(int pageRollSpeed) {
		this.pageRollSpeed = pageRollSpeed;
	}

	public int getPageRollSpeed() {
		return pageRollSpeed;
	}

	public int getRequestedOrientation() {
		return requestedOrientation;
	}

	public void setRequestedOrientation(int requestedOrientation) {
		this.requestedOrientation = requestedOrientation;
	}

}
