package com.example.hockey2;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Paddle {
    private float x, y;
    private final float radius = 80;

    public Paddle(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, Paint paint) {
        paint.setColor(0xFFFF0000);
        canvas.drawCircle(x, y, radius, paint);
    }

    public void setPosition(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
}
