package com.shawnzip.lightreader.page;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class PaletteView extends View{
	public final int mWidth = 350;
	public final int mHeight = 30;

	private Paint mPaint;
	private final int[] mColors;
    
    public PaletteView(Context c) {
        super(c);
        setLayoutParams(new LayoutParams(mWidth, mHeight));
        
        mColors = new int[] {
            0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
            0xFFFFFF00, 0xFFFF0000
        };
        Shader s = new LinearGradient(0, 0, mWidth, mHeight, mColors, null, Shader.TileMode.REPEAT);
        
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setShader(s);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(32);
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
            return mColors[0];
        }
        if (unit >= 1) {
            return mColors[mColors.length - 1];
        }

        float p = unit * (mColors.length - 1);
        int i = (int)p;
        p -= i;

        // now p is just the fractional part [0...1) and i is the index
        int c0 = mColors[i];
        int c1 = mColors[i+1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);
        
        return Color.argb(a, r, g, b);
    }
}
