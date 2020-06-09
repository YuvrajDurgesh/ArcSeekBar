package com.yuvrajdurgesh.arcseekbarexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.yuvrajdurgesh.arcseekbar.ArcSeekBar;

public class MainActivity extends AppCompatActivity {

    private ArcSeekBar arcSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arcSeekBar = findViewById(R.id.arcSeekbar);

        arcSeekBar.setOnArcSeekBarChangeListener(new ArcSeekBar.OnArcSeekBarChangeListener() {
            @Override
            public void onProgressChanged(ArcSeekBar ArcSeekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(ArcSeekBar ArcSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(ArcSeekBar ArcSeekBar) {

            }
        });
    }
}
