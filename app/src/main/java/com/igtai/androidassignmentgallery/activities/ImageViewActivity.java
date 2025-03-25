package com.igtai.androidassignmentgallery.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.igtai.androidassignmentgallery.R;

public class ImageViewActivity extends AppCompatActivity {
    private ImageView fullImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        fullImageView = findViewById(R.id.fullImageView);
        String imagePath = getIntent().getStringExtra("imagePath");

        if (imagePath != null) {
            Glide.with(this).load(imagePath).into(fullImageView);
        }
    }
}
