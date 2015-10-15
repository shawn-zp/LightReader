package com.shawnzip.lightreader.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;

import com.shawnzip.lightreader.BookshelfActivity;
import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.database.BookAdapter;
import com.shawnzip.lightreader.database.DatabaseAdapter;

public class BookshelfMenu {
	public View view;
	private ImageButton bookshelfDelete;			// 删除图书
	private ImageButton bookshelfDeleteAll;		// 删除全部
	private ImageButton bookshelfExit;			// 推出系统

	private BookshelfActivity bookshelfActivity;
	private long bookId;

	public BookshelfMenu(BookshelfActivity bookshelfActivity) {
		this.bookshelfActivity = bookshelfActivity;

		LayoutInflater inflater = (LayoutInflater) bookshelfActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.layout_bookshelf_menu, null);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		bookshelfDelete = (ImageButton)view.findViewById(R.id.bookshelf_menu_delete);
		bookshelfDeleteAll = (ImageButton)view.findViewById(R.id.bookshelf_menu_delete_all);
		bookshelfExit = (ImageButton)view.findViewById(R.id.bookshelf_menu_exit);

		view.setOnClickListener(bookshelfMenuOncClickListener);
		bookshelfDelete.setOnClickListener(bookshelfMenuOncClickListener);
		bookshelfDeleteAll.setOnClickListener(bookshelfMenuOncClickListener);
		bookshelfExit.setOnClickListener(bookshelfMenuOncClickListener);
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
		if(bookId <= 0)
			bookshelfDelete.setVisibility(View.GONE);
		else
			bookshelfDelete.setVisibility(View.VISIBLE);
	}

	private OnClickListener bookshelfMenuOncClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == view) {
				view.setVisibility(View.GONE);
			}else if(v == bookshelfDelete) {
				DatabaseAdapter databaseAdapter = new DatabaseAdapter(bookshelfActivity);
				databaseAdapter.open();
				BookAdapter mBookAdapter = new BookAdapter(databaseAdapter);
				int num = mBookAdapter.deleteBook(BookAdapter.BOOK_KEY_ID + "=" + bookId, null);
				databaseAdapter.close();
				view.setVisibility(View.GONE);
				if(num > 0) {
					bookshelfActivity.initBookshelf();
				}
			}else if(v == bookshelfDeleteAll) {
				DatabaseAdapter databaseAdapter = new DatabaseAdapter(bookshelfActivity);
				databaseAdapter.open();
				BookAdapter mBookAdapter = new BookAdapter(databaseAdapter);
				int num = mBookAdapter.deleteBook(null, null);
				databaseAdapter.close();
				view.setVisibility(View.GONE);
				if(num > 0) {
					bookshelfActivity.initBookshelf();
				}
			}else if(v == bookshelfExit) {
				bookshelfActivity.finish();
			}
		}
	};
}
