package com.example.musica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer reproductor;
    private TextView lblTiempoActual, lblTiempoTotal;
    private SeekBar seekbar;
    private Runnable handlerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        lblTiempoActual = findViewById(R.id.lblTiempoActual);
        lblTiempoTotal = findViewById(R.id.lblTiempoTotal);
        seekbar = findViewById(R.id.seekbar);
        reproductor = MediaPlayer.create(this, R.raw.say_my_grace);

        lblTiempoTotal.setText(millisMMSS(reproductor.getDuration()));
        lblTiempoActual.setText(millisMMSS(0));
        seekbar.setMax(reproductor.getDuration());

        // Play
        findViewById(R.id.btnPlay).setOnClickListener(v -> {
            if (!this.reproductor.isPlaying()) {
                reproductor.start();
                startTimer();
            }
        });

        // Pause
        findViewById(R.id.btnPause).setOnClickListener(v -> {
            if (this.reproductor.isPlaying()) {
                reproductor.pause();
            }
        });

        // Stop
        findViewById(R.id.btnStop).setOnClickListener(v -> {
            reproductor.stop();
            try {
                reproductor.prepare();
            } catch (IOException ignored) {}
            seekbar.setProgress(0);
            lblTiempoActual.setText(millisMMSS(0));
        });

        // Cambio en la seekbar
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (reproductor.isPlaying() && fromUser) {
                    reproductor.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startTimer() {
        handlerTask = () -> {
            if (reproductor.isPlaying()) {
                seekbar.setProgress(reproductor.getCurrentPosition());
                lblTiempoActual.setText(millisMMSS(seekbar.getProgress()));
            }
            new Handler().postDelayed(handlerTask, 1000);
        };
        handlerTask.run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean res = super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.menuItemSalir) {
            salir();
        }

        return res;
    }

    private void salir() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.ad_salir_titulo)
                .setMessage(R.string.ad_salir_mensaje)
                .setCancelable(false)
                .setNegativeButton(R.string.ad_salir_no, null)
                .setPositiveButton(R.string.ad_salir_si, (dialog, which) -> {
                    finish();
                })
                .create()
                .show();
    }

    @SuppressLint("DefaultLocale")
    private String millisMMSS(int millis) {
        return String.format("%02d : %02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }
}