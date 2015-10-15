package com.shawnzip.lightreader.page;

import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.database.BookmarkAdapter;
import com.shawnzip.lightreader.database.DatabaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

public class BookmarkMenu {
	public View view;
	private View menuView;
	private ImageButton bookmarkDelete;			// 删除书签
	private ImageButton bookmarkDeleteAll;		// 删除全部

	private Context context;
	private BookmarkManage bookmarkManage;
	private long bookmarkId;

	public BookmarkMenu(Context context, BookmarkManage bookmarkManage) {
		this.context = context;
		this.bookmarkManage = bookmarkManage;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.layout_bookmark_menu, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		menuView = view.findViewById(R.id.menu_frame);
		bookmarkDelete = (ImageButton)view.findViewById(R.id.bookmark_delete);
		bookmarkDeleteAll = (ImageButton)view.findViewById(R.id.bookmark_delete_all);

		view.setOnClickListener(bookmarkMenuOncClickListener);
		menuView.setOnClickListener(bookmarkMenuOncClickListener);
		bookmarkDelete.setOnClickListener(bookmarkMenuOncClickListener);
		bookmarkDeleteAll.setOnClickListener(bookmarkMenuOncClickListener);
	}

	public void setBookmarkId(long bookmarkId) {
		this.bookmarkId = bookmarkId;
		if(bookmarkId <= 0)
			bookmarkDelete.setVisibility(View.GONE);
		else
			bookmarkDelete.setVisibility(View.VISIBLE);
	}

	private OnClickListener bookmarkMenuOncClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == view) {
				view.setVisibility(View.GONE);
			}else if(v == bookmarkDelete) {
				DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
				databaseAdapter.open();
				BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(databaseAdapter);
				int num = bookmarkAdapter.deleteBookmark(BookmarkAdapter.BOOKMARK_KEY_ID + "=" + bookmarkId, null);
				databaseAdapter.close();
				if(num > 0) {
					bookmarkManage.initBookmarks();
				}
				view.setVisibility(View.GONE);
			}else if(v == bookmarkDeleteAll) {
				DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
				databaseAdapter.open();
				BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(databaseAdapter);
				int num = bookmarkAdapter.deleteBookmark(BookmarkAdapter.BOOKMARK_KEY_BOOK_ID + "=" + bookmarkManage.getBookId(), null);
				databaseAdapter.close();
				if(num > 0) {
					bookmarkManage.initBookmarks();
				}
				view.setVisibility(View.GONE);
			}
		}
	};
}
