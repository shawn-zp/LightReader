package com.shawnzip.lightreader.page;

import java.util.Calendar;

public class TimeReceiver implements Runnable{

	private boolean isReceiver;
	private String time;
	
	public TimeReceiver() {
		isReceiver = true;
		new Thread(this).start();
	}
	
	public void run() {
		while(isReceiver) {
			Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            
            time = ""+(hour<10?"0"+hour:hour);
            time += ":"+(minute<10?"0"+minute:minute);
			
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getTime() {
		return time;
	}
	
	public void release() {
		isReceiver = false;
	}
}
