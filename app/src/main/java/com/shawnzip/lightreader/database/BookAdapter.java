package com.shawnzip.lightreader.database;

import java.io.File;
import java.util.Vector;

import com.shawnzip.lightreader.bean.Book;

import android.content.ContentValues;
import android.database.Cursor;

public class BookAdapter {
	public static final String BOOK_KEY_ID 		= "id";     		// id
	public static final String BOOK_KEY_TITLE 	= "title";			// 书名
	public static final String BOOK_KEY_PATH 	= "path";			// 书路径
	public static final String BOOK_KEY_LOCATION= "location";		// 阅读位置
	public static final String BOOK_KEY_COVER_INDEX= "cover_index";		// 封面图片


	private DatabaseAdapter databaseAdapter;

	public BookAdapter(DatabaseAdapter databaseAdapter) {
		this.databaseAdapter = databaseAdapter;
	}

	// 生成表sql语句
	public static String createTableSql() {
		String sql = "CREATE TABLE "
				+ DatabaseAdapter.BOOK_TABLE
				+ " (" + BOOK_KEY_ID + " INTEGER PRIMARY KEY,"
				+ BOOK_KEY_TITLE + " TEXT,"
				+ BOOK_KEY_PATH + " TEXT,"
				+ BOOK_KEY_LOCATION + " INTEGER,"
				+ BOOK_KEY_COVER_INDEX + " INTEGER)";
		return sql;
	}
	//增
	public Book insertBook(File file) {
		Vector<Book> books = queryBooks(BOOK_KEY_PATH + "='" + file.getAbsolutePath() + "'");
		if(books.size() > 0) {
			return books.get(0);
		}
		Book book = new Book(file);
		ContentValues values = new ContentValues();
		values.put(BOOK_KEY_TITLE, book.getTitle());
		values.put(BOOK_KEY_PATH, book.getPath());
		values.put(BOOK_KEY_LOCATION, book.getLocation());
		values.put(BOOK_KEY_COVER_INDEX, book.getCoverIndex());
		long bookId = databaseAdapter.insertData(DatabaseAdapter.BOOK_TABLE, null, values);
		if(bookId > 0) {
			book.setId(bookId);
			return book;
		}else
			return null;
	}
	//删
	public int deleteBook(String whereClause, String[] whereArgs) {
		return databaseAdapter.deleteData(DatabaseAdapter.BOOK_TABLE, whereClause, whereArgs);
	}
	// 改
	public int updateBook(ContentValues values, String whereClause, String[] whereArgs) {
		return databaseAdapter.updateData(DatabaseAdapter.BOOK_TABLE, values, whereClause, whereArgs);
	}
	//查
	public Vector<Book> queryBooks(String selection) {
		Cursor mCursor = databaseAdapter.queryData(DatabaseAdapter.BOOK_TABLE, null, selection, null, null, null, null);

		int index_id = mCursor.getColumnIndex(BOOK_KEY_ID);
		int index_title = mCursor.getColumnIndex(BOOK_KEY_TITLE);
		int index_path = mCursor.getColumnIndex(BOOK_KEY_PATH);
		int index_location = mCursor.getColumnIndex(BOOK_KEY_LOCATION);
		int index_coverIndex = mCursor.getColumnIndex(BOOK_KEY_COVER_INDEX);
		Vector<Book> books = new Vector<Book>();
		for(mCursor.moveToFirst();!mCursor.isAfterLast();mCursor.moveToNext()) {
			long id = mCursor.getLong(index_id);
			String title = mCursor.getString(index_title);
			String path = mCursor.getString(index_path);
			int location = mCursor.getInt(index_location);
			int coverIndex = mCursor.getInt(index_coverIndex);
			Book book = new Book(id, title, path);
			book.setLocation(location);
			book.setCoverIndex(coverIndex);
			books.add(book);
		}
		return books;
	}
}
