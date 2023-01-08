package com.sellspot.app.Shared;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class OpaqueView extends View {

    public OpaqueView(Context context) {
        super(context);
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public OpaqueView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int height = getHeight();
        int width = getWidth();
        int h = (height - width)/2;

        Paint paint = new Paint();
        paint.setARGB(114,0,0,0);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        canvas.drawRect(0, 0, width, h, paint);
        canvas.drawRect(0, height-h,width,height, paint);
    }
}
