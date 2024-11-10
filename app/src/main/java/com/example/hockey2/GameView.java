package com.example.hockey2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
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
    private int fieldHeight;
    private int fieldWidth;

    private int fullHeight;

    // Temporizador
    private long gameTime = 60000; // 60 segundos
    private CountDownTimer gameTimer;
    private boolean gameEnded = false;

    //empezar el juego
    private boolean player1Ready = false;
    private boolean player2Ready = false;


    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        paint = new Paint();

        // Obtener el ancho de la pantalla
        fullHeight = getResources().getDisplayMetrics().heightPixels;
        fieldHeight = fullHeight - 200;
        fieldWidth = getResources().getDisplayMetrics().widthPixels;

        // Inicializar paletas y puck
        paddle1 = new Paddle(fieldWidth / 2, 120); // Paleta del Jugador 1
        paddle2 = new Paddle(fieldWidth / 2, fieldHeight - 120); // Paleta del Jugador 2
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

            // Dibujar la cancha de hockey
            drawHockeyField(canvas);

            // Dibujar el tiempo restante debajo de la cancha y centrado
            canvas.drawText("Tiempo: " + gameTime / 1000 + "s", (fieldWidth / 2) - 50, fullHeight - 50, paint);

            // Dibujar paletas y puck
            paint.setColor(Color.YELLOW);
            paddle1.draw(canvas, paint);

            paint.setColor(Color.BLUE);
            paddle2.draw(canvas, paint);

            paint.setColor(Color.WHITE);
            puck.draw(canvas, paint);

            // Verificar si hay gol
            if (isGoalScored()) {
                resetGame();
            }
        }
    }

    private void drawHockeyField(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(8);

        // Líneas laterales de la cancha
        canvas.drawLine(0, 0, 0, fieldHeight, paint); // Línea izquierda
        canvas.drawLine(fieldWidth, 0, fieldWidth, fieldHeight, paint); // Línea derecha

        // Línea central
        canvas.drawLine(0, fieldHeight / 2, fieldWidth, fieldHeight / 2, paint);

        // Líneas de portería
        int goalWidth = fieldWidth / 3;
        int goalHeight = 10;

        paint.setColor(Color.RED);
        canvas.drawRect((fieldWidth - goalWidth) / 2, 0, (fieldWidth + goalWidth) / 2, goalHeight, paint); // Portería superior
        canvas.drawRect((fieldWidth - goalWidth) / 2, fieldHeight - goalHeight, (fieldWidth + goalWidth) / 2, fieldHeight, paint); // Portería inferior

        // Área de portería semicircular (arriba)
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        int areaRadius = goalWidth / 2;
        canvas.drawArc(
                (fieldWidth - goalWidth) / 2,
                -areaRadius,
                (fieldWidth + goalWidth) / 2,
                areaRadius,
                0, 180, false, paint
        );

        // Área de portería semicircular (abajo)
        canvas.drawArc(
                (fieldWidth - goalWidth) / 2,
                fieldHeight - areaRadius,
                (fieldWidth + goalWidth) / 2,
                fieldHeight + areaRadius,
                180, 180, false, paint
        );

        // Líneas de los tercios del campo
        canvas.drawLine(0, fieldHeight / 3, fieldWidth, fieldHeight / 3, paint);
        canvas.drawLine(0, 2 * fieldHeight / 3, fieldWidth, 2 * fieldHeight / 3, paint);

        // Puntos de cara a cara en la cancha (centro y lados)
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(fieldWidth / 2, fieldHeight / 2, 20, paint); // Punto central
        canvas.drawCircle(fieldWidth / 4, fieldHeight / 4, 20, paint); // Punto superior izquierdo
        canvas.drawCircle(3 * fieldWidth / 4, fieldHeight / 4, 20, paint); // Punto superior derecho
        canvas.drawCircle(fieldWidth / 4, 3 * fieldHeight / 4, 20, paint); // Punto inferior izquierdo
        canvas.drawCircle(3 * fieldWidth / 4, 3 * fieldHeight / 4, 20, paint); // Punto inferior derecho

        paint.setStyle(Paint.Style.STROKE);
    }

    private boolean isGoalScored() {
        int goalWidth = fieldWidth / 3;
        int goalTopY = 10; // Parte superior de la portería
        int goalBottomY = fieldHeight - 10; // Parte inferior de la portería
        float puckRadius = puck.getRadius();

        // Coordenadas de la portería superior (Jugador 2 marca gol)
        float goalLeftX = (fieldWidth - goalWidth) / 2;
        float goalRightX = (fieldWidth + goalWidth) / 2;

        // Verificar si el puck toca la portería superior (Jugador 2 marca gol)
        if (puck.getY() - puckRadius <= goalTopY &&
                puck.getX() > goalLeftX &&
                puck.getX() < goalRightX) {
            score2++; // Incrementar el puntaje del Jugador 2
            return true;
        }

        // Verificar si el puck toca la portería inferior (Jugador 1 marca gol)
        if (puck.getY() + puckRadius >= goalBottomY &&
                puck.getX() > goalLeftX &&
                puck.getX() < goalRightX) {
            score1++; // Incrementar el puntaje del Jugador 1
            return true;
        }

        return false;
    }

    // Iniciar el temporizador
    private void startGameTimer() {
        gameTimer = new CountDownTimer(gameTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameTime = millisUntilFinished;
                invalidate();
            }

            @Override
            public void onFinish() {
                gameEnded = true; // El juego ha terminado
                showGameResultDialog(); // Mostrar el cuadro de diálogo con el resultado
            }
        };
        gameTimer.start();
    }

    private void showGameResultDialog() {
        String winner;
        if (score1 > score2) {
            winner = "Jugador 1";
        } else if (score2 > score1) {
            winner = "Jugador 2";
        } else {
            winner = "Empate";
        }

        // Crear el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Fin del Juego");
        builder.setMessage("Ganador: " + winner + "\n" +
                "Marcador: " + score1 + " - " + score2 + "\n" +
                "Tiempo transcurrido: " + (60000 - gameTime) / 1000 + " segundos");

        builder.setPositiveButton("Jugar de Nuevo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                restartGame(); // Reiniciar el juego
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    private void restartGame() {
        score1 = 0;
        score2 = 0;
        gameTime = 60000; // Reiniciar a 60 segundos
        gameEnded = false;
        startGameTimer(); // Reiniciar el temporizador
        invalidate(); // Redibujar la pantalla
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int pointerCount = event.getPointerCount();

        // Recorrer todos los toques detectados
        for (int i = 0; i < pointerCount; i++) {
            int pointerId = event.getPointerId(i);
            float x = event.getX(i);
            float y = event.getY(i);

            // Mover la paleta del jugador correspondiente
            if (y < fieldHeight / 2) { // Área del Jugador 1 (parte superior)
                paddle1.setPosition(x, y, fieldWidth, fieldHeight);
                player1Ready = true;
            } else { // Área del Jugador 2 (parte inferior)
                paddle2.setPosition(x, y, fieldWidth, fieldHeight);
                player2Ready = true;
            }
        }

        if (!gameStarted && player1Ready && player2Ready) {
            gameStarted = true;
            startGameTimer();
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

        player1Ready = false;
        player2Ready = false;
    }
}
