package com.shawnzip.lightreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoadingActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loading);
        
        new Thread(){
        	public void run(){
        		try {
					Thread.sleep(2800);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		        
                startActivity(new Intent(LoadingActivity.this, BookshelfActivity.class));
                finish();
        	}
        }.start();
    }
}
