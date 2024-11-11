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

    public void setPosition(float x, float y, int fieldWidth, int fieldHeight) {
        // Limitar movimiento de la paleta dentro de su mitad de pantalla
        this.x = Math.max(radius, Math.min(x, fieldWidth - radius));

        // Limitar el movimiento vertical de la paleta según el jugador
        if (y < fieldHeight / 2) { // Jugador 1
            this.y = Math.max(radius, Math.min(y, fieldHeight / 2 - radius));
        } else { // Jugador 2
            this.y = Math.max(fieldHeight / 2 + radius, Math.min(y, fieldHeight - radius));
        }
    }

    public void reolocalizarPaddles(float x, float y) {
        this.x = x;
        this.y = y;
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
