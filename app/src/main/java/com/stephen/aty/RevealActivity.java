package com.stephen.aty;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.stephen.R;
import com.stephen.reveal.RevealFrameLayout;

public class RevealActivity extends AppCompatActivity {

    private RevealFrameLayout revealFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal);
        revealFrameLayout = (RevealFrameLayout) findViewById(R.id.fl_reveal);
    }

    public void startReveal(View view) {
        if (revealFrameLayout != null) {
            revealFrameLayout.startReveal(1000);
        }
    }
}
