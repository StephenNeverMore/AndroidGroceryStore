package com.stephen.aty;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stephen.R;
import com.stephen.util.LogUtils;
import com.stephen.views.CropView;

public class CropActivity extends AppCompatActivity implements CropView.CropCallback {

    private CropView cropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        cropView = (CropView) findViewById(R.id.crop_view);
        cropView.setCropCallback(this);
        cropView.setLeftTopPoint(100, 100);
    }

    @Override
    public void updatePoint(float leftTopX, float leftTopY, float rightBottomX, float rightBottomY, float maxX, float maxY) {
        LogUtils.e(" leftTopX = " + leftTopX);
        LogUtils.e(" leftTopY = " + leftTopY);
        LogUtils.e(" rightBottomX = " + leftTopX);
        LogUtils.e(" rightBottomY = " + rightBottomY);
        LogUtils.e(" maxX = " + maxX);
        LogUtils.e(" maxY = " + maxY);
    }
}
