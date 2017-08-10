package com.stephen.aty;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.stephen.R;
import com.stephen.views.WaveView;

public class WaveAty extends Activity implements SeekBar.OnSeekBarChangeListener {

    WaveView waveView;
    SeekBar seekBar;
    SeekBar seekBar2;
    SeekBar seekBar3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave);
        waveView = (WaveView) findViewById(R.id.wave);
        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(100);
        seekBar2 = (SeekBar) findViewById(R.id.seek_bar2);
        seekBar2.setOnSeekBarChangeListener(this);
        seekBar2.setProgress(1);
        seekBar3 = (SeekBar) findViewById(R.id.seek_bar3);
        seekBar3.setOnSeekBarChangeListener(this);
        seekBar3.setProgress(50);
    }

    public void startWave(View view) {
        if (waveView != null) {
            if (waveView.isWaving()) {
                waveView.stopWave();
                ((Button) view).setText("START WAVE");
            } else {
                waveView.startWave();
                ((Button) view).setText("STOP WAVE");
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar.getId() == R.id.seek_bar) {
            if (waveView != null) {
                waveView.setWaveHeight(progress);
            }
        } else if (seekBar.getId() == R.id.seek_bar2) {
            if (waveView != null) {
                waveView.setWaveCount(progress / 10);
            }
        } else if (seekBar.getId() == R.id.seek_bar3) {
            if (waveView != null) {
                waveView.setWaterHeightProgress(progress);
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

