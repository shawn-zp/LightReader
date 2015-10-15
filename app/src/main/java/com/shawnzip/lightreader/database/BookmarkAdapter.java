package com.shawnzip.lightreader.database;

import java.util.Vector;

import android.content.ContentValues;
import android.database.Cursor;

import com.shawnzip.lightreader.bean.Bookmark;

public class BookmarkAdapter {
	public static final String BOOKMARK_KEY_ID 	= "id";     			// id  
	public static final String BOOKMARK_KEY_BOOK_ID	= "book_id";     	// 图书id
	public static final String BOOKMARK_KEY_LOCATION= "location";		// 书签位置
	public static final String BOOKMARK_KEY_PREVIEW = "preview";		// 书签内容预览

	private DatabaseAdapter databaseAdapter;

	public BookmarkAdapter(DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;
	}

	// 生成表sql语句
	public static String createTableSql() {
		String sql = "CREATE TABLE "
				+ DatabaseAdapter.BOOKMARK_TABLE
				+ " (" + BOOKMARK_KEY_ID + " INTEGER PRIMARY KEY,"
				+ BOOKMARK_KEY_BOOK_ID + " INTEGER,"
				+ BOOKMARK_KEY_LOCATION + " INTEGER,"
				+ BOOKMARK_KEY_PREVIEW + " TEXT)";
		return sql;
	}
	//增
	public boolean insertBookmark(int location, long bookId, String preview) {
		Vector<Bookmark> bookmarks = queryBookmarks(BOOKMARK_KEY_BOOK_ID + "=" + bookId + " and " +BOOKMARK_KEY_LOCATION + "=" + location);
		if(bookmarks.size() > 0) {
			return true;
		}
		ContentValues values = new ContentValues();
		values.put(BOOKMARK_KEY_BOOK_ID, bookId);
		values.put(BOOKMARK_KEY_LOCATION, location);
		values.put(BOOKMARK_KEY_PREVIEW, preview);
		long bookmarkId = databaseAdapter.insertData(DatabaseAdapter.BOOKMARK_TABLE, null, values);
		if(bookmarkId > 0)
			return true;
		else
			return false;
	}
	//删
	public int deleteBookmark(String whereClause, String[] whereArgs) {
		return databaseAdapter.deleteData(DatabaseAdapter.BOOKMARK_TABLE, whereClause, whereArgs);
	}
	//查
	public Vector<Bookmark> queryBookmarks(String selection) {
		Cursor mCursor = databaseAdapter.queryData(DatabaseAdapter.BOOKMARK_TABLE, null, selection, null, null, null, null);

		int index_id = mCursor.getColumnIndex(BOOKMARK_KEY_ID);
		int index_bookId = mCursor.getColumnIndex(BOOKMARK_KEY_BOOK_ID);
		int index_location = mCursor.getColumnIndex(BOOKMARK_KEY_LOCATION);
		int index_preview = mCursor.getColumnIndex(BOOKMARK_KEY_PREVIEW);
		Vector<Bookmark> bookmarks = new Vector<Bookmark>();
		for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {
			long id = mCursor.getLong(index_id);
			long bookId = mCursor.getLong(index_bookId);
			int location = mCursor.getInt(index_location);
			String preview = mCursor.getString(index_preview);
			Bookmark bookmark = new Bookmark(id, bookId, location, preview);
			bookmarks.add(bookmark);
		}
		return bookmarks;
	}
}
