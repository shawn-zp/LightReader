package com.shawnzip.lightreader.page;

import java.text.DecimalFormat;
import java.util.Vector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.R;
import com.shawnzip.lightreader.bean.Book;
import com.shawnzip.lightreader.bean.Bookmark;
import com.shawnzip.lightreader.database.BookAdapter;
import com.shawnzip.lightreader.database.BookmarkAdapter;
import com.shawnzip.lightreader.database.DatabaseAdapter;
import com.shawnzip.lightreader.units.ReaderTools;

public class BookmarkManage extends PageToolsManage{
	private TableLayout bookmarksTable;
	private Button marketBack;
	private Button marketMenu;
	private BookmarkMenu bookmarkMenu;
	
	private long bookId;
	
	public BookmarkManage(PageActivity activity) {
		super(activity);
		
		LayoutInflater inflater = (LayoutInflater) pageActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
		view = (RelativeLayout)inflater.inflate(R.layout.layout_bookmark, null);
		
        bookmarksTable = (TableLayout)view.findViewById(R.id.bookmarks_table);
        marketBack = (Button)view.findViewById(R.id.bookmark_back);
        marketMenu = (Button)view.findViewById(R.id.bookmark_menu);
        
        marketBack.setOnClickListener(manageOnClickListener);
        marketMenu.setOnClickListener(manageOnClickListener);
	}
	
	public void setBookId(long bookId) {
		this.bookId = bookId;
		if(bookId > 0) {
			initBookmarks();
		}
	}

	private OnClickListener manageOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == marketBack){
				view.setVisibility(View.GONE);
			}else if(v == marketMenu) {
				showMenu(0);
			}		
		}
	};
	
	private OnClickListener bookmarkOncClickListener = new OnClickListener() {		
		public void onClick(View v) {
			Bookmark bookmark = (Bookmark)v.getTag();

			DatabaseAdapter databaseAdapter = new DatabaseAdapter(pageActivity);
	        databaseAdapter.open();
		  	BookAdapter mBookAdapter = new BookAdapter(databaseAdapter);
		  	Vector<Book> bookList = mBookAdapter.queryBooks(BookAdapter.BOOK_KEY_ID + "=" + bookmark.getBookId());
		  	databaseAdapter.close();
		  	
		  	if(bookList.size() > 0){
	  			pageActivity.jumpPage(bookmark.getLocation());
		  		view.setVisibility(View.GONE);
		  	}else {
		  		ReaderTools.showToast(pageActivity, pageActivity.getResources().getString(R.string.bookmark_error));
		  	}
		}
	};
	
	private OnLongClickListener bookmarkOnLongClickListener = new OnLongClickListener() {
		public boolean onLongClick(View v) {
			Bookmark bookmark = (Bookmark)v.getTag();
			showMenu(bookmark.getId());
			return true;
		}
	};
	
	//��ʼ�����
    public void initBookmarks(){
    	bookmarksTable.removeAllViews();
    	
    	DatabaseAdapter databaseAdapter = new DatabaseAdapter(pageActivity);
        databaseAdapter.open();
	  	BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(databaseAdapter);
	  	String bookmarksWhere = BookmarkAdapter.BOOKMARK_KEY_BOOK_ID + "=" +bookId;
    	Vector<Bookmark> bookmarks = bookmarkAdapter.queryBookmarks(bookmarksWhere);
	  	databaseAdapter.close();
    	
	  	LayoutInflater mInflater = pageActivity.getLayoutInflater();
		View modelView = mInflater.inflate(R.layout.layout_bookmark_view, null);
		modelView.measure(0, 0);
		int currentX = 0;
		int viewWidth = modelView.getMeasuredWidth();
		int maxX = ReaderTools.getWindowWidth(pageActivity) - viewWidth;
	  	
    	for(int i=0,size=bookmarks.size();i<size;) {
    		currentX = 0;
    		TableRow row = new TableRow(pageActivity);
    		row.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    		
    		while(i<size && currentX <= maxX) {
				Bookmark bookmark = bookmarks.get(i);
    			float arg = bookmark.getLocation()*100f/pageActivity.pagefactory.getBookLength();
    			View bookmarkView = mInflater.inflate(R.layout.layout_bookmark_view, null);
    			TextView preview = (TextView)bookmarkView.findViewById(R.id.bookmark_preview);
    			TextView location = (TextView)bookmarkView.findViewById(R.id.bookmark_location);
    			preview.setText(bookmark.getPreview());
    			location.setText(new DecimalFormat("#0.0").format(arg)+"%");
    			bookmarkView.setTag(bookmark);
    			bookmarkView.setOnClickListener(bookmarkOncClickListener);
    			bookmarkView.setOnLongClickListener(bookmarkOnLongClickListener);
    			row.addView(bookmarkView);

    			currentX += viewWidth;
    			i++;
    		}
    		bookmarksTable.addView(row);
    	}
    }
    
 	public void showMenu(long bookmarkId) {
 		if(null == bookmarkMenu) {
 			bookmarkMenu = new BookmarkMenu(pageActivity, this);
 			view.addView(bookmarkMenu.view);
 		}
 		bookmarkMenu.setBookmarkId(bookmarkId);
 		bookmarkMenu.view.bringToFront();
 		bookmarkMenu.view.setVisibility(View.VISIBLE);
 	}
 	
 	public void goBack() {
 		if(null != bookmarkMenu && bookmarkMenu.view.getVisibility() == View.VISIBLE) {
 			bookmarkMenu.view.setVisibility(View.GONE);
 		}else {
 			super.goBack();
 		}
 	}
 	
 	public long getBookId() {
 		return bookId;
 	}
}
