package com.shawnzip.lightreader;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.shawnzip.lightreader.bean.Book;
import com.shawnzip.lightreader.database.BookAdapter;
import com.shawnzip.lightreader.database.DatabaseAdapter;
import com.shawnzip.lightreader.units.Global;
import com.shawnzip.lightreader.units.ReaderTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class AddBookActivity extends Activity{
	private Button k_back;
	private Button backToFatherFolder;		// 返回父文件夹
	private TextView k_currentFolder;		//当前文件夹
	private ScrollView fileScroll;			// 文件滚动视图
	private LinearLayout fileList;			//文件列表

	private File currentFolder;

	//文件过滤器，查找文件夹或文本文件
	private static final FileFilter BOOKS_FILTER = new FileFilter() {
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().toLowerCase().matches("^.*?\\.(txt|text)$");
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_addbook_directory);
		k_back = (Button)findViewById(R.id.addbook_back);
		backToFatherFolder = (Button)findViewById(R.id.addbook_path_back);
		k_currentFolder = (TextView)findViewById(R.id.addbook_current_folder);
		fileScroll = (ScrollView)findViewById(R.id.addbook_file_scroll);
		fileList = (LinearLayout)findViewById(R.id.addbook_file_list);

		k_back.setOnClickListener(onClickListener);
		backToFatherFolder.setOnClickListener(onClickListener);

		SharedPreferences addBookPreferences = getSharedPreferences(Global.PREFERENCES_ADD_BOOK, Context.MODE_PRIVATE);
		String lastBookFolder = Environment.getExternalStorageDirectory().getAbsolutePath();// addBookPreferences.getString("last_book_folder", "/");
		fill(lastBookFolder);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && null != currentFolder.getParentFile()) {
			fill(currentFolder.getParentFile().getAbsolutePath());
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private OnClickListener onClickListener = new OnClickListener() {
		public void onClick(View v) {
			if(v == k_back) {
				finish();
			}else if(v == backToFatherFolder) {
				if(currentFolder.getParentFile() != null){
					fill(currentFolder.getParentFile().getAbsolutePath());
				}
			}
		}
	};

	// 点击文件或文件夹
	private OnClickListener fileOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			File file = (File)v.getTag();
			if (file.isDirectory())
				fill(file.getAbsolutePath());
			else {
				//保存上本书目录，下次添加书直接进入
				SharedPreferences addBookPreferences = getSharedPreferences(Global.PREFERENCES_ADD_BOOK, Context.MODE_PRIVATE);
				addBookPreferences.edit().putString("last_book_folder", file.getParent()).commit();

				DatabaseAdapter databaseAdapter = new DatabaseAdapter(AddBookActivity.this);
				databaseAdapter.open();
				BookAdapter mBookAdapter = new BookAdapter(databaseAdapter);
				Book book = mBookAdapter.insertBook(file);
				databaseAdapter.close();
				if(null != book) {
					AddBookActivity.this.setResult(RESULT_OK, new Intent().putExtra("book", book));
					finish();
				}else {
					ReaderTools.showToast(AddBookActivity.this,
							getResources().getString(R.string.addbook_error));
				}
			}
		}
	};
	//跳转目录
	private void fill(String folderPath) {
		File folder = null;
		try{
			folder = new File(folderPath);
		}catch(Exception e) {
			folder = new File("/");
		}

		currentFolder = folder;
		k_currentFolder.setText(folder.getAbsolutePath());
		List<File> files = new ArrayList<File>();
		File[] filterFiles = folder.listFiles(BOOKS_FILTER);
		if(null != filterFiles && filterFiles.length>0) {
			for (File file : filterFiles) {
				files.add(file);
			}
		}
		initFileList(files);
	}
	// 初始化文件列表
	private void initFileList(List<File> files) {
		LayoutInflater mInflater = LayoutInflater.from(this);
		fileList.removeAllViews();
		fileScroll.scrollTo(0, 0);

		for(int i=0,size=files.size();i<size;i++) {
			View convertView = mInflater.inflate(R.layout.layout_addbook_file, null);
			ImageView image = (ImageView)convertView.findViewById(R.id.addbook_image);
			TextView title = (TextView)convertView.findViewById(R.id.addbook_title);
			TextView info = (TextView)convertView.findViewById(R.id.addbook_info);

			File file = files.get(i);
			title.setText(file.getName());
			if (file.isDirectory()) {
				image.setImageResource(R.drawable.addbook_folder);
				info.setText(R.string.addbook_directory);
			}else {
				image.setImageResource(R.drawable.addbook_file);
				long fileSize = file.length();
				if(fileSize >= 1024*1024) {
					float sizeF = fileSize/(1024f*1024f);
					info.setText(new DecimalFormat("#.00").format(sizeF) + "MB");
				}else if(fileSize >= 1024) {
					float sizeF = fileSize/1024f;
					info.setText(new DecimalFormat("#.00").format(sizeF) + "KB");
				}else {
					info.setText(fileSize + "B");
				}
			}
			convertView.setTag(file);
			convertView.setOnClickListener(fileOnClickListener);
			fileList.addView(convertView);
		}

	}
}