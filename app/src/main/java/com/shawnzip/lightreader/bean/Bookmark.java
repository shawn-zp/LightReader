package com.shawnzip.lightreader.bean;

public class Bookmark {
	private long id;		// 书签id
	private long bookId;	// 图书id
	private int location;	// 书签位置
	private String preview;	// 书签内容预览

	public Bookmark(long id, long bookId, int location, String preview) {
		this.id = id;
		this.bookId = bookId;
		this.location = location;
		this.preview = preview;
	}

	public long getId() {
		return id;
	}
	public long getBookId() {
		return bookId;
	}
	public int getLocation() {
		return location;
	}
	public String getPreview() {
		return preview;
	}


}
