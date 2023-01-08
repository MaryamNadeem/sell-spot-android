package com.sellspot.app.Shared;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class TriangleView extends View {

    public TriangleView(Context context) {
        super(context);
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();

        int height = getHeight();
        int width = getWidth();

        paint.setStrokeWidth(4);
        paint.setARGB(100,255,255,255);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Point a = new Point(width, 0);
        Point b = new Point(width, height);
        Point c = new Point(0, height);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(a.x,a.y);
        path.lineTo(b.x, b.y);
        path.lineTo(c.x, c.y);
        path.close();
        canvas.drawPath(path, paint);
    }
}
