package com.sellspot.app.Shared;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class CorneredLinearLayout extends LinearLayout {

    private RectF rectF;
    private Path path = new Path();
    private float cornerRadius = 50;

    public CorneredLinearLayout(Context context) {
        super(context);
    }

    public CorneredLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CorneredLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF = new RectF(0, 0, w, h);
        resetPath();
    }

    @Override
    public void draw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(path);
        super.draw(canvas);
        canvas.restoreToCount(save);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(path);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }

    private void resetPath() {
        path.reset();
        path.addRoundRect(rectF, cornerRadius, cornerRadius, Path.Direction.CW);
        path.close();
    }
}
