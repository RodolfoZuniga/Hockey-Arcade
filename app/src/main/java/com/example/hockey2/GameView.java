package com.example.hockey2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Paint paint;
    private Paddle paddle1, paddle2;
    private Puck puck;
    private int score1 = 0, score2 = 0;
    private boolean gameStarted = false;

    // Dimensiones de la cancha
    private final int fieldHeight = 2100;
    private int fieldWidth;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        paint = new Paint();

        // Obtener el ancho de la pantalla
        fieldWidth = getResources().getDisplayMetrics().widthPixels;

        // Inicializar paletas y puck
        paddle1 = new Paddle(fieldWidth / 2, 100); // Paleta del Jugador 1
        paddle2 = new Paddle(fieldWidth / 2, fieldHeight - 100); // Paleta del Jugador 2
        puck = new Puck(fieldWidth / 2, fieldHeight / 2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameThread.setRunning(false);
        boolean retry = true;
        while (retry) {
            try {
                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            // Dibujar fondo de cancha
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);

            // Dibujar puntajes
            canvas.drawText("Jugador 1: " + score1, 50, 50, paint);
            canvas.drawText("Jugador 2: " + score2, 50, fieldHeight - 20, paint);

            // Dibujar bordes de la portería
            paint.setColor(Color.RED);
            int goalWidth = fieldWidth / 3;
            canvas.drawRect((fieldWidth - goalWidth) / 2, 0, (fieldWidth + goalWidth) / 2, 10, paint); // Portería superior
            canvas.drawRect((fieldWidth - goalWidth) / 2, fieldHeight - 10, (fieldWidth + goalWidth) / 2, fieldHeight, paint); // Portería inferior

            // Dibujar paletas y puck
            paint.setColor(Color.WHITE);
            paddle1.draw(canvas, paint);
            paddle2.draw(canvas, paint);
            puck.draw(canvas, paint);

            // Verificar si hay gol
            if (isGoalScored()) {
                resetGame();
            }
        }
    }

    private boolean isGoalScored() {
        int goalWidth = fieldWidth / 3;
        // Verificar si el puck entra en la portería superior (Jugador 2 marca un gol)
        if (puck.getY() <= 0 && puck.getX() > (fieldWidth - goalWidth) / 2 && puck.getX() < (fieldWidth + goalWidth) / 2) {
            score2++;
            return true;
        }
        // Verificar si el puck entra en la portería inferior (Jugador 1 marca un gol)
        if (puck.getY() >= fieldHeight && puck.getX() > (fieldWidth - goalWidth) / 2 && puck.getX() < (fieldWidth + goalWidth) / 2) {
            score1++;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();

        if (!gameStarted) {
            gameStarted = true;
        }

        // Recorrer todos los toques detectados
        for (int i = 0; i < pointerCount; i++) {
            int pointerId = event.getPointerId(i);
            float x = event.getX(i);
            float y = event.getY(i);

            // Mover la paleta del jugador correspondiente
            if (y < fieldHeight / 2) { // Área del Jugador 1 (parte superior)
                paddle1.setPosition(x);
            } else { // Área del Jugador 2 (parte inferior)
                paddle2.setPosition(x);
            }
        }

        return true;
    }


    public void update() {
        if (gameStarted) {
            puck.update(fieldWidth, fieldHeight);
            puck.checkCollision(paddle1);
            puck.checkCollision(paddle2);
        }
    }

    private void resetGame() {
        puck.resetPosition(getWidth() / 2, getHeight() / 2);
        gameStarted = false;
    }
}
