package com.stephen.aty;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.stephen.R;


public class VectorActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vector);
        imageView = (ImageView) findViewById(R.id.image_view);
    }

    public void startAnim(View view) {
        final Drawable drawable = imageView.getDrawable();
        ((Animatable) drawable).start();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startSearchBar(View view) {
        view.setSelected(!view.isSelected());
        final Drawable drawable = ((ImageView)view).getDrawable();
    }

    public void startTest(View view) {
        final Drawable drawable = ((ImageView)view).getDrawable();
        ((Animatable) drawable).start();
    }
}
