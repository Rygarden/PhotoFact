package com.diploma.nurzhan.photo_fact.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.UserService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryItemActivity extends AppCompatActivity {

    private static final String TAG = "HistoryItemActivity";

    private ImageButton btnBack;
    private LinearLayout imageLinear;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyitem);

        imageLinear = findViewById(R.id.imageLinear);

        userService = ApiUtils.getUserService();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Offense");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        btnBack = findViewById(R.id.btnBack);

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == btnBack) {
//                    finish();
//                }
//            }
//        });

        imageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imageUrl = getIntent().getStringExtra("image_url");
                Intent intent = new Intent(HistoryItemActivity.this, ImageActivity.class);
                intent.putExtra("imagePath", imageUrl);
                startActivity(intent);
            }
        });

        getIncomingIntent();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: check for incoming intents.");

        if(getIntent().hasExtra("offense_address") && getIntent().hasExtra("offense_status") && getIntent().hasExtra("offense_type") && getIntent().hasExtra("offense_date") && getIntent().hasExtra("offense_image") && getIntent().hasExtra("image_url")){
            Log.d(TAG, "getIncomingIntent: found intent extras.");

            String fineAddress = getIntent().getStringExtra("offense_address");
            String fineStatus = getIntent().getStringExtra("offense_status");
            String fineType = getIntent().getStringExtra("offense_type");
            String fineDate = getIntent().getStringExtra("offense_date");
            String fineImage = getIntent().getStringExtra("offense_image");
            String imageUrl = getIntent().getStringExtra("image_url");

            setFines(fineAddress, fineStatus, fineType, fineDate, fineImage, imageUrl);
        }
    }

    private void setFines(String fineAddress, String fineStatus, String fineType, String fineDate, String fineImage, String imageUrl){
        Log.d(TAG, "setFines: setting data to widgets.");

        TextView address = findViewById(R.id.fine_address);
        TextView status = findViewById(R.id.fine_status);
        TextView type = findViewById(R.id.fine_type);
        TextView date = findViewById(R.id.fine_date);
        CircleImageView offenseCircle = findViewById(R.id.offensePhoto);
        TextView offenseImage = findViewById(R.id.circleImage);


        address.setText(fineAddress);
        status.setText(fineStatus);
        if(fineType.equals("1")){
            type.setText("Stopping on pedestrian crossing");
        }else if(fineType.equals("2")){
            type.setText("Stopping on Parking for \"disabled\" people");
        }else if(fineType.equals("3")){
            type.setText("\"No Stopping\" sign");
        }else if(fineType.equals("4")){
            type.setText("\"Parking is prohibited\" sign");
        }
        date.setText(fineDate);

//        getImage(fineImage);

        Picasso.get().load(imageUrl).into(offenseCircle);
        offenseImage.setText(fineImage);
    }


    private void getImage(String fineImage) {
        Log.d(TAG, "getImage: called.");

        final Map<String, String> imageName = new HashMap<>();

        imageName.put("offense_image", fineImage);

        Call<ResponseBody> call = userService.getImage(imageName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server response: " + response.body());

                CircleImageView offenseCircle = findViewById(R.id.offensePhoto);

                Picasso.get().load(String.valueOf(response.body().byteStream())).into(offenseCircle);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
                Toast.makeText(HistoryItemActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
