package com.example.socialmedia.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.example.socialmedia.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class FullImageActivity extends AppCompatActivity {

    PhotoView photoView;
    String imageUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        photoView = findViewById(R.id.full_image_id);
        imageUrl = getIntent().getStringExtra("imageUrl");

        if(!imageUrl.isEmpty()) {
            Picasso.with(FullImageActivity.this).load(imageUrl).networkPolicy(NetworkPolicy.OFFLINE).into(photoView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    Picasso.with(FullImageActivity.this).load(imageUrl).into(photoView);
                }
            });
        }
    }
}