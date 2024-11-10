package com.example.hockey2;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.hockey2.Paddle;

public class Puck {
    private float x, y, dx = 30, dy = 30;
    private final float radius = 35;

    public Puck(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void update(int fieldWidth, int fieldHeight) {
        x += dx;
        y += dy;

        // Rebotar en los bordes laterales
        if (x < radius || x > fieldWidth - radius) {
            dx = -dx;
        }
        // Rebotar en los bordes superior e inferior
        if (y < radius || y > fieldHeight - radius) {
            dy = -dy;
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public void checkCollision(Paddle paddle) {
        float dx = x - paddle.getX();
        float dy = y - paddle.getY();
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < radius + paddle.getRadius()) {
            this.dy = -this.dy;
        }
    }

    public void resetPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
