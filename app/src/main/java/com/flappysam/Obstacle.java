package com.flappysam;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Obstacle extends View {
    Paint paint = new Paint();

    public Obstacle(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawLine(70, 70, 0, 0, paint);
    }

}