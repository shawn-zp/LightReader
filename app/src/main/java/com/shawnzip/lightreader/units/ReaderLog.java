package com.shawnzip.lightreader.units;

import android.util.Log;

public class ReaderLog {
	public static void v(String tag, String msg) {
		if(Global.READER_DEBUG)
			Log.v(tag, msg);
	}
	public static void e(String msg) {
		Log.e(Global.TAG, msg);
	}
}
