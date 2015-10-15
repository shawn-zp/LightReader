package com.shawnzip.lightreader.menu;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.database.BookmarkAdapter;
import com.shawnzip.lightreader.database.DatabaseAdapter;
import com.shawnzip.lightreader.page.BookmarkManage;
import com.shawnzip.lightreader.page.PageBackgroundManage;
import com.shawnzip.lightreader.page.PageFontManage;
import com.shawnzip.lightreader.page.PageJumpManage;
import com.shawnzip.lightreader.page.PageRollManage;
import com.shawnzip.lightreader.page.PageRollSpeedManage;
import com.shawnzip.lightreader.page.PageSeachManage;
import com.shawnzip.lightreader.units.ReaderTools;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout.LayoutParams;

public class PageMenu {
	public View view;

	private Button toolsOption;		// 工具选项卡
	private Button settingOption;		// 设置选项卡

	private View toolsView;					// 工具视图
	private ImageButton addBookMark;		// 添加书签
	private ImageButton manageBookMark;		// 管理书签
	private ImageButton pageJump;			// 进度跳转
	private ImageButton pageSeach;			// 内容查找
	private ImageButton pageRoll;			// 自动翻页
	private ImageButton pageType;			// 翻页模式
	private ImageButton pageBackToShelf;	// 返回书架
	private ImageButton pageOrientation;	// 阅读方向

	private View settingView;				// 设置视图
	private ImageButton pageBackground;		// 图书背景
	private ImageButton	pageFont;			// 图书字体
	private ImageButton pageRollSpeed;		// 自动翻页速度

	private BookmarkManage bookmarkManage;	// 书签管理界面
	private PageJumpManage pageJumpManage;	// 进度跳转界面
	private PageSeachManage pageSeachManage;// 全文查找界面
	private PageFontManage pageFontManage;	// 图书字体界面
	private PageBackgroundManage pageBackgroundManage;	// 图书背景界面
	public  PageRollManage pageRollManage;	// 自动翻页界面
	private PageRollSpeedManage pageRollSpeedManage;	// 自动翻页速度界面

	private PageActivity pageActivity;

	public PageMenu(PageActivity pageActivity) {
		this.pageActivity = pageActivity;
		LayoutInflater inflater = (LayoutInflater) pageActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.layout_page_menu, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		toolsOption = (Button)view.findViewById(R.id.page_tools_option);
		settingOption = (Button)view.findViewById(R.id.page_setting_option);

		toolsView = view.findViewById(R.id.page_tools);
		addBookMark = (ImageButton)view.findViewById(R.id.add_bookmark);
		manageBookMark = (ImageButton)view.findViewById(R.id.manage_bookmark);
		pageJump = (ImageButton)view.findViewById(R.id.pageJump);
		pageSeach = (ImageButton)view.findViewById(R.id.page_pageSeach);
		pageRoll = (ImageButton)view.findViewById(R.id.page_pageRoll);
		pageType = (ImageButton)view.findViewById(R.id.page_pageType);
		pageType.setImageResource(pageActivity.pagePreferences.isTranslation()?R.drawable.page_type_slide:R.drawable.page_type_flip);
		pageBackToShelf = (ImageButton)view.findViewById(R.id.page_backToShelf);
		pageOrientation = (ImageButton)view.findViewById(R.id.page_orientationType);
		if(pageActivity.pagePreferences.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			pageOrientation.setImageResource(R.drawable.page_orientation_landscape);
		}else if(pageActivity.pagePreferences.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			pageOrientation.setImageResource(R.drawable.page_orientation_unspecified);
		}else {
			pageOrientation.setImageResource(R.drawable.page_orientation_portait);
		}

		settingView = view.findViewById(R.id.page_setting);
		pageBackground = (ImageButton)view.findViewById(R.id.page_background);
		pageFont = (ImageButton)view.findViewById(R.id.page_font);
		pageRollSpeed = (ImageButton)view.findViewById(R.id.page_roll_speed);

		view.setOnClickListener(pageMenuOnClickListener);
		toolsView.setOnClickListener(pageMenuOnClickListener);
		settingView.setOnClickListener(pageMenuOnClickListener);
		toolsOption.setOnClickListener(pageMenuOnClickListener);
		settingOption.setOnClickListener(pageMenuOnClickListener);
		addBookMark.setOnClickListener(pageMenuOnClickListener);
		manageBookMark.setOnClickListener(pageMenuOnClickListener);
		pageJump.setOnClickListener(pageMenuOnClickListener);
		pageBackToShelf.setOnClickListener(pageMenuOnClickListener);
		pageType.setOnClickListener(pageMenuOnClickListener);
		pageBackground.setOnClickListener(pageMenuOnClickListener);
		pageFont.setOnClickListener(pageMenuOnClickListener);
		pageSeach.setOnClickListener(pageMenuOnClickListener);
		pageRoll.setOnClickListener(pageMenuOnClickListener);
		pageRollSpeed.setOnClickListener(pageMenuOnClickListener);
		pageOrientation.setOnClickListener(pageMenuOnClickListener);
	}

	// 添加书签
	private void addBookmark() {
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(pageActivity);
		databaseAdapter.open();
		BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(databaseAdapter);
		boolean result = bookmarkAdapter.insertBookmark(
				pageActivity.pagefactory.getBookmarkLocation(),
				pageActivity.book.getId(),
				pageActivity.pagefactory.getBookmarkPreview());
		databaseAdapter.close();

		if(result) {
			ReaderTools.showToast(pageActivity,
					pageActivity.getResources().getString(R.string.bookmark_add_sucess));
		}else {
			ReaderTools.showToast(pageActivity,
					pageActivity.getResources().getString(R.string.bookmark_add_error));
		}
	}

	// 管理书签
	private void manageBookmark() {
		view.setVisibility(View.GONE);
		if(null == bookmarkManage) {
			bookmarkManage = new BookmarkManage(pageActivity);
			pageActivity.pageLayout.addView(bookmarkManage.view);
		}
		bookmarkManage.setBookId(pageActivity.book.getId());
		bookmarkManage.view.bringToFront();
		bookmarkManage.view.setVisibility(View.VISIBLE);
	}

	// 进度跳转
	private void pageJump() {
		view.setVisibility(View.GONE);
		if(null == pageJumpManage) {
			pageJumpManage = new PageJumpManage(pageActivity);
			pageActivity.pageLayout.addView(pageJumpManage.view);
		}
		pageJumpManage.view.bringToFront();
		pageJumpManage.view.setVisibility(View.VISIBLE);
	}

	// 搜索查询
	private void pageSeach() {
		view.setVisibility(View.GONE);
		if(null == pageSeachManage) {
			pageSeachManage = new PageSeachManage(pageActivity);
			pageActivity.pageLayout.addView(pageSeachManage.view);
		}
		pageSeachManage.setBeginLocation(pageActivity.pagefactory.getBookmarkLocation());
		pageSeachManage.view.bringToFront();
		pageSeachManage.view.setVisibility(View.VISIBLE);
	}

	// 修改翻页模式
	private void changePageType() {
		if(pageActivity.pagePreferences.isTranslation()) {
			pageActivity.pagePreferences.setTranslation(false);
			pageType.setImageResource(R.drawable.page_type_flip);
		}else {
			pageActivity.pagePreferences.setTranslation(true);
			pageType.setImageResource(R.drawable.page_type_slide);
		}
		pageActivity.updateReadType();
	}

	// 图书字体
	private void pageFont() {
		view.setVisibility(View.GONE);
		if(null == pageFontManage) {
			pageFontManage = new PageFontManage(pageActivity);
			pageActivity.pageLayout.addView(pageFontManage.view);
		}
		pageFontManage.view.bringToFront();
		pageFontManage.view.setVisibility(View.VISIBLE);
	}

	// 图书背景
	private void pageBackground() {
		view.setVisibility(View.GONE);
		if(null == pageBackgroundManage) {
			pageBackgroundManage = new PageBackgroundManage(pageActivity);
			pageActivity.pageLayout.addView(pageBackgroundManage.view);
		}
		pageBackgroundManage.view.bringToFront();
		pageBackgroundManage.view.setVisibility(View.VISIBLE);
	}

	// 自动翻页
	private void pageRoll() {
		view.setVisibility(View.GONE);
		if(null == pageRollManage) {
			pageRollManage = new PageRollManage(pageActivity);
			pageActivity.pageLayout.addView(pageRollManage.view);
		}
		pageRollManage.startRoll();
		pageRollManage.view.bringToFront();
		pageRollManage.view.setVisibility(View.VISIBLE);
	}

	// 自动翻页速度
	private void pageRollSpeed() {
		view.setVisibility(View.GONE);
		if(null == pageRollSpeedManage) {
			pageRollSpeedManage = new PageRollSpeedManage(pageActivity);
			pageActivity.pageLayout.addView(pageRollSpeedManage.view);
		}
		pageRollSpeedManage.view.bringToFront();
		pageRollSpeedManage.view.setVisibility(View.VISIBLE);
	}

	// 修改屏幕方向
	private void changePageOrientation() {
		int requestedOrientation = pageActivity.pagePreferences.getRequestedOrientation();
		int goOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			goOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
			pageOrientation.setImageResource(R.drawable.page_orientation_unspecified);
		}else if(requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			goOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
			pageOrientation.setImageResource(R.drawable.page_orientation_portait);
		}else {
			goOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
			pageOrientation.setImageResource(R.drawable.page_orientation_landscape);
		}
		pageActivity.pagePreferences.setRequestedOrientation(goOrientation);
		pageActivity.setRequestedOrientation(goOrientation);
	}

	private OnClickListener pageMenuOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == view){
				view.setVisibility(View.GONE);
			}else if(v == toolsView || v == settingView) {

			}else if(v == toolsOption) {
				toolsOption.setBackgroundResource(R.drawable.menu_option_bg);
				settingOption.setBackgroundResource(R.drawable.menu_option_bg_false);
				settingView.setVisibility(View.INVISIBLE);
				toolsView.setVisibility(View.VISIBLE);
			}else if(v == settingOption) {
				settingOption.setBackgroundResource(R.drawable.menu_option_bg);
				toolsOption.setBackgroundResource(R.drawable.menu_option_bg_false);
				toolsView.setVisibility(View.INVISIBLE);
				settingView.setVisibility(View.VISIBLE);
			}else if(v == addBookMark) {
				addBookmark();
			}else if(v == manageBookMark) {
				manageBookmark();
			}else if(v == pageJump) {
				pageJump();
			}else if(v == pageSeach) {
				pageSeach();
			}else if(v == pageRoll) {
				pageRoll();
			}else if(v == pageType) {
				changePageType();
			}else if(v == pageBackToShelf) {
				pageActivity.finish();
			}else if(v == pageBackground) {
				pageBackground();
			}else if(v == pageFont) {
				pageFont();
			}else if(v == pageRollSpeed) {
				pageRollSpeed();
			}else if(v == pageOrientation) {
				changePageOrientation();
			}
		}
	};


	// 根据当前界面显示界面菜单
	public void showMenu(View currentView) {
		if(null != bookmarkManage && currentView == bookmarkManage.view && bookmarkManage.view.getVisibility() == View.VISIBLE) {
			bookmarkManage.showMenu(0);
		}else {
			if(null != bookmarkManage)
				bookmarkManage.view.setVisibility(View.GONE);
			if(null != pageJumpManage)
				pageJumpManage.view.setVisibility(View.GONE);
			if(null != pageFontManage)
				pageFontManage.view.setVisibility(View.GONE);
			if(null != pageBackgroundManage)
				pageBackgroundManage.view.setVisibility(View.GONE);
			if(null != pageSeachManage)
				pageSeachManage.view.setVisibility(View.GONE);
			if(null != pageRollSpeedManage)
				pageRollSpeedManage.view.setVisibility(View.GONE);

			view.bringToFront();
			view.setVisibility(View.VISIBLE);
		}
	}

	// 根据当前界面进行返回操作
	public void goBack(View currentView) {
		if(null != bookmarkManage && currentView == bookmarkManage.view && bookmarkManage.view.getVisibility() == View.VISIBLE) {
			bookmarkManage.goBack();
		}else if(null != pageJumpManage && currentView == pageJumpManage.view && pageJumpManage.view.getVisibility() == View.VISIBLE) {
			pageJumpManage.goBack();
		}else if(null != pageFontManage && currentView == pageFontManage.view && pageFontManage.view.getVisibility() == View.VISIBLE) {
			pageFontManage.goBack();
		}else if(null != pageBackgroundManage && currentView == pageBackgroundManage.view && pageBackgroundManage.view.getVisibility() == View.VISIBLE) {
			pageBackgroundManage.goBack();
		}else if(null != pageRollSpeedManage && currentView == pageRollSpeedManage.view && pageRollSpeedManage.view.getVisibility() == View.VISIBLE) {
			pageRollSpeedManage.goBack();
		}else if(null != pageSeachManage && currentView == pageSeachManage.view && pageSeachManage.view.getVisibility() == View.VISIBLE) {
			pageSeachManage.goBack();
		}else if(currentView == view && view.getVisibility() == View.VISIBLE){
			view.setVisibility(View.GONE);
		}else {
			pageActivity.finish();
		}
	}
}
