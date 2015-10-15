package com.shawnzip.lightreader.units;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Point;
import android.widget.Toast;

import android.view.Window;
import android.graphics.Rect;

public class ReaderTools {
	//弹出提示对话框
	public static void showTipDialog(Context context, String title, String message){
		AlertDialog.Builder builder = showProcessDialog(context, title, message);
		builder.create().show();
	}
	//弹出警告对话框
	public static AlertDialog.Builder showProcessDialog(Context context, String title, String message){
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton("确定", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		return builder;
	}
	//显示提示信息
	public static void showToast(Context context, String message){
		getToast(context, message).show();
	}
	public static Toast getToast(Context context, String message){
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		return toast;
	}

	//获得屏幕宽
	public static int getWindowWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}
	//获得屏幕高
	public static int getWindowHeight(Activity activity) {
		int height = activity.getWindowManager().getDefaultDisplay().getHeight();
		return height - 50;// shawn.debug (Title)
	}

	//弹出警示框
	public static Builder createAlertDialog(Context context, String title, String message){
		Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		return alert;
	}

	//弹出等待框
	public static ProgressDialog createProgressDialog(Context context, String title, String message) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		return progressDialog;
	}
}
