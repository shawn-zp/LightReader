package com.shawnzip.lightreader;

import java.io.IOException;

import com.shawnzip.lightreader.bean.Book;
import com.shawnzip.lightreader.database.BookAdapter;
import com.shawnzip.lightreader.database.DatabaseAdapter;
import com.shawnzip.lightreader.menu.PageMenu;
import com.shawnzip.lightreader.page.BatteryReceiver;
import com.shawnzip.lightreader.page.PageFactory;
import com.shawnzip.lightreader.page.PagePreferences;
import com.shawnzip.lightreader.page.PageWidget;
import com.shawnzip.lightreader.page.TimeReceiver;
import com.shawnzip.lightreader.units.Global;
import com.shawnzip.lightreader.units.ReaderLog;
import com.shawnzip.lightreader.units.ReaderTools;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

public class PageActivity extends Activity{
	public Book book;
	public PagePreferences pagePreferences;	// 阅读喜好设置

	public RelativeLayout pageLayout;
	private PageMenu pageMenu;

	/** Called when the activity is first created. */
	public PageFactory pagefactory;

	private PageWidget mPageWidget;
	Bitmap mCurPageBitmap, mNextPageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas;

	public BatteryReceiver batterReceiver;
	public TimeReceiver timeReceiver;

	private boolean autoSaveInfo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 检测图书是否可用
		Intent intent = this.getIntent();
		book = (Book)intent.getSerializableExtra("book");
		if(null == book) {
			ReaderLog.v(Global.TAG, "book not find");
			ReaderTools.showToast(PageActivity.this,
					getResources().getString(R.string.readbook_init_error));
			finish();
			return;
		}

		// 创建页面工厂
		try {
			pagefactory = new PageFactory(this, book.getPath());
		} catch (IOException e) {
			ReaderLog.e("bookPath not find:"+e.getMessage());
			ReaderTools.showToast(PageActivity.this,
					getResources().getString(R.string.readbook_init_error));
			finish();
			return;
		}

		batterReceiver = new BatteryReceiver();
		IntentFilter filter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batterReceiver, filter);//注册BroadcastReceiver
		timeReceiver = new TimeReceiver();

		// 设置阅读位置，页面效果
		pagefactory.setLocation(book.getLocation());
		pagePreferences = new PagePreferences(this);
		pagefactory.setPaintWithPreferences();
		setRequestedOrientation(pagePreferences.getRequestedOrientation());

		pageLayout = new RelativeLayout(this);
		pageLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mPageWidget = new PageWidget(this);
		pageLayout.addView(mPageWidget);
		setContentView(pageLayout);

		int mWidth = ReaderTools.getWindowWidth(this);
		final int mHeight = ReaderTools.getWindowHeight(this);
		mCurPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);

		pagefactory.onDraw(mCurPageCanvas);

		mPageWidget.setBitmaps(mCurPageBitmap, mCurPageBitmap);

		mPageWidget.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub
				boolean ret=false;
				if (v == mPageWidget) {
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						mPageWidget.abortAnimation();
						mPageWidget.calcCornerXY(e.getX(), e.getY());

						pagefactory.onDraw(mCurPageCanvas);
						double menuRect = Math.abs(e.getY() - (mHeight * 0.5));
						if (menuRect < PagePreferences.menuTouchSize) {
							onClickMenu();
						}
						else if (mPageWidget.DragToRight()) {
							try {
								pagefactory.prePage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(pagefactory.isFirstPage())return false;
							pagefactory.onDraw(mNextPageCanvas);
						} else {
							try {
								pagefactory.nextPage();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(pagefactory.isLastPage())return false;
							pagefactory.onDraw(mNextPageCanvas);
						}
						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
					}

					ret = mPageWidget.doTouchEvent(e);
					return ret;
				}
				return false;
			}
		});

		// 自动保存读书进度
		new Thread() {
			public void run() {
				autoSaveInfo = true;
				while(autoSaveInfo) {
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(autoSaveInfo)
						saveReadInfo();
				}
			}
		}.start();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveReadInfo();
	}



	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// 停止自动翻页
		if(PagePreferences.pageRolling && null != pageMenu && null != pageMenu.pageRollManage) {
			pageMenu.pageRollManage.stopRoll();
		}

		if(keyCode == KeyEvent.KEYCODE_MENU) {
			onClickMenu();
		}else if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(null == pageMenu) {
				finish();
			}else {
				View currentView = pageLayout.getChildAt(pageLayout.getChildCount() - 1);
				pageMenu.goBack(currentView);
			}
		}else if(keyCode == KeyEvent.KEYCODE_HOME) {
			saveReadInfo();
		}
		return super.onKeyDown(keyCode, event);
	}

	// 点击菜单按钮
	private void onClickMenu() {
		if(null == pageMenu) {
			pageMenu = new PageMenu(PageActivity.this);
			pageLayout.addView(pageMenu.view);
		}
		View currentView = pageLayout.getChildAt(pageLayout.getChildCount() - 1);
		pageMenu.showMenu(currentView);
	}

	// 更新页面
	private void updatePage() {
		try {
			pagefactory.prePage();
			pagefactory.nextPage();
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
			pagefactory.onDraw(mCurPageCanvas);
			pagefactory.onDraw(mNextPageCanvas);
			mPageWidget.postInvalidate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 页面跳转
	public void jumpPage(int location) {
		pagefactory.setLocation(location);
		updatePage();
	}

	// 更改翻页模式
	public void updateReadType() {
		mPageWidget.resetBezier();
	}

	// 更改字体
	public void updateFont(int size, int linesize) {
		pagePreferences.setTextSize(size);
		pagePreferences.setLineSize(linesize);
		pagefactory.setPaintWithPreferences();
		updatePage();
	}

	// 更改阅读背景
	public void updateBackground(int resId, int color) {
		pagePreferences.setBackground(resId);
		pagePreferences.setBackColor(color);
		updatePage();
	}

	// 更改阅读字体颜色
	public void updateTextColor(int color) {
		pagePreferences.setTextColor(color);
		pagefactory.setPaintWithPreferences();
		updatePage();
	}

	// 自动翻页
	public void pageRool() {
		try {
			pagefactory.nextPage();
			if(pagefactory.isLastPage()) {
				PagePreferences.pageRolling = false;
				return;
			}
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
			pagefactory.onDraw(mCurPageCanvas);
			pagefactory.onDraw(mNextPageCanvas);
			mPageWidget.postInvalidate();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 自动翻页速度
	public void updatePageRollSpeed(int speed) {
		pagePreferences.setPageRollSpeed(speed);
	}

	//保存阅读信息
	private synchronized void saveReadInfo() {
		pagePreferences.save();	// 保存阅读喜好

		// 保存阅读位置
		book.setLocation(pagefactory.getBookmarkLocation());
		ContentValues values = new ContentValues();
		values.put(BookAdapter.BOOK_KEY_LOCATION, pagefactory.getBookmarkLocation());

		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
		databaseAdapter.open();
		BookAdapter mBookAdapter = new BookAdapter(databaseAdapter);
		mBookAdapter.updateBook(values, BookAdapter.BOOK_KEY_ID + "=" + book.getId(), null);
		databaseAdapter.close();
	}

	public void finish() {
		autoSaveInfo = false;
		saveReadInfo();
		unregisterReceiver(batterReceiver);
		timeReceiver.release();
		mCurPageBitmap.recycle();
		mCurPageBitmap = null;
		mNextPageBitmap.recycle();
		mNextPageBitmap = null;
		mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
		super.finish();
	}
}
