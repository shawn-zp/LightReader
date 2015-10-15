package com.shawnzip.lightreader;

import java.util.Vector;
import java.io.File;

import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.bean.Book;
import com.shawnzip.lightreader.database.BookAdapter;
import com.shawnzip.lightreader.database.DatabaseAdapter;
import com.shawnzip.lightreader.menu.BookshelfMenu;
import com.shawnzip.lightreader.units.ReaderLog;
import com.shawnzip.lightreader.units.ReaderTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class BookshelfActivity extends Activity {
	private RelativeLayout bookshelfLayout;
	private TableLayout bookshelfTable;
	private Button addBook;
	private Button menuButton;
	private BookshelfMenu bookshelfMenu;

	private final int ADD_BOOK_CODE = 1001;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		bookshelfLayout = (RelativeLayout)inflater.inflate(R.layout.layout_bookshelf, null);
		setContentView(bookshelfLayout);

		bookshelfTable = (TableLayout)findViewById(R.id.bookshelf_table);
		addBook = (Button)findViewById(R.id.bookshelf_add_book);
		menuButton = (Button)findViewById(R.id.bookshelf_menu);

		addBook.setOnClickListener(mOnClickListener);
		menuButton.setOnClickListener(mOnClickListener);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initBookshelf();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Intent txtIntent = getIntent();
		Uri data = txtIntent.getData();
		if(data != null) {
			File file = new File(data.getPath());

			DatabaseAdapter databaseAdapter = new DatabaseAdapter(BookshelfActivity.this);
			databaseAdapter.open();
			BookAdapter mBookAdapter = new BookAdapter(databaseAdapter);
			Book book = mBookAdapter.insertBook(file);
			databaseAdapter.close();
			goReadBook(book);
			txtIntent.setData(null);
		}
	}

	//初始化书架
	public void initBookshelf(){
		bookshelfTable.removeAllViews();

		DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
		databaseAdapter.open();
		BookAdapter mBookAdapter = new BookAdapter(databaseAdapter);
		Vector<Book> bookList = mBookAdapter.queryBooks(null);
		databaseAdapter.close();

		LayoutInflater mInflater = this.getLayoutInflater();
		View modelView = mInflater.inflate(R.layout.layout_book_view, null);
		modelView.measure(0, 0);
		int currentX = 0;
		int viewWidth = modelView.getMeasuredWidth();
		int maxX = ReaderTools.getWindowWidth(this) - viewWidth;

		for(int i=0,size=bookList.size();i<size;) {
			currentX = 0;
			TableRow row = new TableRow(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

			while(i<size && currentX <= maxX) {
				Book book = bookList.get(i);

				View bookView = mInflater.inflate(R.layout.layout_book_view, null);
				ImageView bookImage = (ImageView)bookView.findViewById(R.id.book_image);
				TextView bookTitle = (TextView)bookView.findViewById(R.id.book_name);

				if(book.getCoverIndex()<Book.coverIds.length)
					bookImage.setImageResource(Book.coverIds[book.getCoverIndex()]);
				else
					bookImage.setImageResource(Book.coverIds[0]);
				bookTitle.setText(book.getTitle());
				bookView.setTag(book);
				bookView.setOnClickListener(mBookClickListener);
				bookView.setOnLongClickListener(mBookLongClickListener);
				row.addView(bookView);

				currentX += viewWidth;
				i++;
			}
			bookshelfTable.addView(row);
		}
	}

	private void showMenu(long bookId) {
		if(null == bookshelfMenu) {
			bookshelfMenu = new BookshelfMenu(this);
			bookshelfLayout.addView(bookshelfMenu.view);
		}
		bookshelfMenu.setBookId(bookId);
		bookshelfMenu.view.bringToFront();
		bookshelfMenu.view.setVisibility(View.VISIBLE);
	}
	//点击图书按钮
	private OnClickListener mBookClickListener = new OnClickListener() {
		public void onClick(View v) {
			goReadBook((Book)v.getTag());
		}
	};
	//长按图书按钮
	private OnLongClickListener mBookLongClickListener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			Book book = (Book)v.getTag();
			showMenu(book.getId());
			return true;
		}
	};

	private OnClickListener mOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == addBook) {
				startActivityForResult(new Intent(BookshelfActivity.this, AddBookActivity.class), ADD_BOOK_CODE);
			}else if(v == menuButton) {
				showMenu(0);
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != RESULT_OK) {
			ReaderLog.e("ActivityResult resultCode unSucess");
			return;
		}
		//此处的用于判断接收的Activity是不是你想要的那个
		switch (requestCode) {
			case ADD_BOOK_CODE:
				Book book = (Book)data.getSerializableExtra("book");
				goReadBook(book);
				break;
			default:
				break;
		}
	}

	//转到读书activity
	private void goReadBook(Book book) {
		if(null == book) {
			ReaderTools.showToast(this,
					this.getResources().getString(R.string.bookshelf_book_error));
		}else {
			Intent intent = new Intent(this, PageActivity.class);
			intent.putExtra("book", book);
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU) {
			showMenu(0);
		}else if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(null != bookshelfMenu && bookshelfMenu.view.getVisibility() == View.VISIBLE){
				bookshelfMenu.view.setVisibility(View.GONE);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && null != currentFolder.getParentFile()) {
//			fill(currentFolder.getParentFile().getAbsolutePath());
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	@Override
	public void finish() {
		super.finish();
	}
}
