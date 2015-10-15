package com.shawnzip.lightreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAdapter{

    private static final String DB_NAME = "LightReader.db";    // 数据库名  
    private static final int    DB_VERSION = 1;         // 数据库版本  

    public static final String BOOK_TABLE 		= "book_table";		// 书表名  
    public static final String BOOKMARK_TABLE 	= "bookmark_table";	// 书签表名  

    public Context context;
    public DatabaseHelper mDatabaseHelper;
    public SQLiteDatabase mSQLiteDatabase;

    private static class DatabaseHelper extends SQLiteOpenHelper{
        public DatabaseHelper(Context context) {
            super(context, DB_NAME,  null , DB_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub  
            String bookTable = BookAdapter.createTableSql();
            db.execSQL(bookTable);
            String bookmarkTable = BookmarkAdapter.createTableSql();
            db.execSQL(bookmarkTable);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub  
            db.execSQL("DROP TABLE IF EXISTS "+BOOK_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+BOOKMARK_TABLE);
            onCreate(db);
        }
    }

    public DatabaseAdapter(Context context) {
        this.context = context;
    }
    //开启  
    public void open() {
        mDatabaseHelper = new DatabaseHelper(context);
        mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
    }
    //关闭  
    public void close() {
        mSQLiteDatabase.close();
        mDatabaseHelper.close();
    }

    //增
    protected long insertData(String table, String nullColumnHack, ContentValues values) {
        return mSQLiteDatabase.insert(table, nullColumnHack, values);
    }
    //删
    protected int deleteData(String table, String whereClause, String[] whereArgs) {
        return mSQLiteDatabase.delete(table, whereClause, whereArgs);
    }
    //改
    protected int updateData(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return mSQLiteDatabase.update(table, values, whereClause, whereArgs);
    }
    //查
    protected Cursor queryData(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return mSQLiteDatabase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }
}  