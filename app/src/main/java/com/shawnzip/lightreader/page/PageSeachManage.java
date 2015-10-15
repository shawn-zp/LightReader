package com.shawnzip.lightreader.page;

import java.io.IOException;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.units.ReaderTools;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PageSeachManage extends PageToolsManage{
	private View menuView;
	private EditText seachText;
	private Button seachLast;
	private Button seachNext;

	private int beginSeachLocation;	// 开始查询位置
	private boolean beginSeachPageType;	// 开始查询时的翻页模式
	private int lastSecchLocation;	// 上次查询位置
	private String seachContent;	// 查询内容
	private boolean isSeaching;		// 是否正在查询

	private static final int kShowSeachOutset = 1001;
	private static final int kShowSeachEnd = 1002;

	public PageSeachManage(PageActivity activity) {
		super(activity);

		LayoutInflater inflater = (LayoutInflater) pageActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = (RelativeLayout)inflater.inflate(R.layout.layout_page_seach, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		menuView = view.findViewById(R.id.menu_frame);
		seachText = (EditText)view.findViewById(R.id.page_seach_text);
		seachLast = (Button)view.findViewById(R.id.page_seach_last);
		seachNext = (Button)view.findViewById(R.id.page_seach_next);

		view.setOnClickListener(viewOnClickListener);
		menuView.setOnClickListener(viewOnClickListener);
		seachLast.setOnClickListener(viewOnClickListener);
		seachNext.setOnClickListener(viewOnClickListener);
	}

	// 是否满足查询条件
	private boolean canSeach() {
		seachContent = seachText.getText().toString();
		if(seachContent.length() == 0)
			return false;
		return true;
	}

	// 停止查询
	private void stopSeach(int overLocation) {
		isSeaching = false;
		pageActivity.jumpPage(overLocation);
	}

	// 是否返回查询开始位置
	private void isSeachGoback() {
		Builder alertBuilder = ReaderTools.createAlertDialog(pageActivity,
				pageActivity.getResources().getString(R.string.alert_title),
				pageActivity.getResources().getString(R.string.page_seach_goback));
		alertBuilder.setPositiveButton(R.string.alert_sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				stopSeach(beginSeachLocation);
			}
		});
		alertBuilder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertBuilder.show();
	}

	public void goBack() {
		view.setVisibility(View.GONE);
		pageActivity.pagePreferences.setTranslation(beginSeachPageType);
		if(beginSeachLocation != pageActivity.pagefactory.getBookmarkLocation())
			isSeachGoback();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == kShowSeachOutset) {
				ReaderTools.showToast(pageActivity, pageActivity.getResources().getString(R.string.page_seach_outset));
			}else if (msg.what == kShowSeachEnd) {
				ReaderTools.showToast(pageActivity, pageActivity.getResources().getString(R.string.page_seach_end));
			}else {
				seachText.setFocusable(true);
			}
		}
	};

	// 查询内容
	private void seachContent(final boolean isPre) {
		if(!canSeach())
			return;
		final ProgressDialog seachProgress = ReaderTools.createProgressDialog(pageActivity,
				pageActivity.getResources().getString(R.string.progress_dialog_title),
				pageActivity.getResources().getString(R.string.page_seach_progress));
		seachProgress.setButton(pageActivity.getResources().getString(R.string.progress_dialog_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						stopSeach(beginSeachLocation);
						dialog.dismiss();
					}
				});
		seachProgress.show();
		new Thread() {
			public void run() {
				isSeaching = true;
				String pageContent = "";
				while(isSeaching) {
					// 如果当前页没有查找，寻找当前页有没有查找内容
					if(lastSecchLocation != pageActivity.pagefactory.getBookmarkLocation()) {
						lastSecchLocation = pageActivity.pagefactory.getBookmarkLocation();
						pageContent = pageActivity.pagefactory.getBookPageContent();
						if(pageContent.indexOf(seachContent) >= 0) {
							stopSeach(lastSecchLocation);
							break;
						}
					}
					try {
						if(isPre) {		// 向上查
							pageActivity.pagefactory.prePage();
							if(pageActivity.pagefactory.isFirstPage()) {
								mHandler.sendEmptyMessage(kShowSeachOutset);
								break;
							}
						}else {			// 向下查
							pageActivity.pagefactory.nextPage();
							if(pageActivity.pagefactory.isLastPage()) {
								mHandler.sendEmptyMessage(kShowSeachEnd);
								break;
							}
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				mHandler.sendEmptyMessage(0);
				seachProgress.dismiss();
			}
		}.start();
	}

	private OnClickListener viewOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == view) {
				goBack();
			}else if(v == menuView) {

			}else if(v == seachLast) {
				seachContent(true);
			}else if(v == seachNext) {
				seachContent(false);
			}
		}
	};

	public void setBeginLocation(int location) {
		this.beginSeachLocation = location;
		this.beginSeachPageType = pageActivity.pagePreferences.isTranslation();
		pageActivity.pagePreferences.setTranslation(true);
	}
}
