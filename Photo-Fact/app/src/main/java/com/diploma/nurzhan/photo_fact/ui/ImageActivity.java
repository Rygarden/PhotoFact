package com.diploma.nurzhan.photo_fact.ui;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.UserService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.io.IOException;

import retrofit2.Callback;
import retrofit2.Response;

public class ImageActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private String uri;
    private ImageView imageView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        imageView = findViewById(R.id.imageView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Offense image");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            uri = bundle.getString("imagePath");
            Picasso.get().load(uri).into(imageView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
