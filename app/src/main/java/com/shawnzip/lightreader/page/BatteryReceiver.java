package com.shawnzip.lightreader.page;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryReceiver extends BroadcastReceiver {
	private String battery;
	
	public BatteryReceiver() {
		super();
		battery = "δ֪";
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int current=intent.getExtras().getInt("level");//��õ�ǰ����
		int total=intent.getExtras().getInt("scale");//����ܵ���
		battery = (current*100/total) + "%";
	}
	
	public String getBattery() {
		return battery;
	}
}
