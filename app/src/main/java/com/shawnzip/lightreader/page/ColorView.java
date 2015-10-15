package com.shawnzip.lightreader.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

public class ColorView extends View{
	public final int mWidth = 350;
    public final int mHeight = 130;
    
    private final int topColor = 0xFFFFFFFF;
    private final int bottomColor = 0xFF000000;
    private int mainColor;

	private Paint mPaint;
    
    ColorView(Context c, int color) {
        super(c);
        android.widget.LinearLayout.LayoutParams lp = new android.widget.LinearLayout.LayoutParams(mWidth, mHeight);
        lp.setMargins(0, 20, 0, 0);
        setLayoutParams(lp);
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mWidth);
        
        initView(color);
    }
    
    public void initView(int color) {
    	mainColor = color;
        int[] mColors = new int[] { topColor, mainColor, bottomColor };
	    Shader s = new LinearGradient(0, 0, mWidth, mHeight, mColors, null, Shader.TileMode.REPEAT);
        mPaint.setShader(s);
	    postInvalidate();
    }

    @Override 
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(new RectF(0, 0, mWidth, mHeight), mPaint);
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }
    
    public int interpColor(float unit) {
        if (unit <= 0) {
            return topColor;
        }
        if (unit >= 1) {
            return bottomColor;
        }

        int c0,c1;
        if(unit <= 0.5f) {
        	c0 = topColor;
        	c1 = mainColor;
        }else {
        	c0 = mainColor;
        	c1 = bottomColor;
        	unit -= 0.5f;
        }
        float p = unit*2;

        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        
        return Color.argb(a, r, g, b);
    }
}
