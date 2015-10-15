package com.shawnzip.lightreader.bean;

import java.io.File;
import java.io.Serializable;
import java.util.Random;

import com.shawnzip.lightreader.R;

public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final int[] coverIds = new int[]{R.drawable.book_bg_1, R.drawable.book_bg_2,
			R.drawable.book_bg_3, R.drawable.book_bg_4, R.drawable.book_bg_5};

	private long id;		// 图书id
	private String title;	// 图书标题
	private String path;	// 图书路径
	private int location;	// 阅读位置
	private int coverIndex;	// 封面图片资源

	public Book(long id, String title, String path) {
		this.id = id;
		this.title = title;
		this.path = path;
		this.location = 0;
		this.coverIndex = 0;
	}

	public Book(File file) {
		this.title = file.getName();
		this.path = file.getAbsolutePath();
		this.coverIndex = new Random().nextInt(coverIds.length);
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getPath() {
		return path;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public int getCoverIndex() {
		return coverIndex;
	}

	public void setCoverIndex(int coverIndex) {
		this.coverIndex = coverIndex;
	}

}
