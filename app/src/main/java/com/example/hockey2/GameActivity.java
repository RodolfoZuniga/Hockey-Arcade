package com.example.hockey2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Crear el GameView y establecerlo como la vista de contenido
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }
}
