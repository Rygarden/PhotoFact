package com.diploma.nurzhan.photo_fact.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.SessionManager;
import com.diploma.nurzhan.photo_fact.repository.UserService;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.diploma.nurzhan.photo_fact.Constants.KEY_EMAIL;
import static com.diploma.nurzhan.photo_fact.Constants.KEY_PASS;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private TextView tvUser;
    private TextView tvUsername;
    private TextView tvEmail;

    private String user;

    private ImageButton btnHistory;
    private ImageButton btnBack;

    private Button btnSignOut;

    private UserService userService;

    private SessionManager sessionManager;

    private LinearLayout linearCard;
    private LinearLayout linearHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUser = findViewById(R.id.tvUser);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        linearCard = findViewById(R.id.linearCard);
        linearHistory = findViewById(R.id.linearHistory);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        btnBack = findViewById(R.id.btnBack);

        btnSignOut = findViewById(R.id.btnSignOut);

        userService = ApiUtils.getUserService();
        sessionManager = new SessionManager(getApplicationContext());

        sessionManager.checkLogin();
        profileShow();

        HashMap<String, String> userMap = sessionManager.getUserDetails();
        String email = userMap.get(KEY_EMAIL);
        tvEmail.setText(email);


        linearCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CardActivity.class));
            }
        });

        linearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), HistoryActivity.class));
            }
        });

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == btnBack) {
//                    finish();
//                }
//            }
//        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set LoggedIn status to false
//                SaveSharedPreferences.setLoggedIn(getApplicationContext(), false);
//                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    sessionManager.logoutUser();
//                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void profileShow(){
        HashMap<String, String> userMap = sessionManager.getUserDetails();
        String username = userMap.get(KEY_EMAIL);
        String pass = userMap.get(KEY_PASS);

        HashMap<String, String> profiles = new HashMap<>();
        profiles.put("username", username);
        profiles.put("pass", pass);

        Call<String> call = userService.profile(profiles);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: Server response: " + response.toString());

                String json = response.body().toString();
                tvUser.setText(json);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
            }
        });
    }

//    private void logout() {
//
//        Call<Void> call = userService.logout();
//
//        call.enqueue(new Callback<Void>() {
//            @Override
//            public void onResponse(Call<Void> call, Response<Void> response) {
//                Log.d(TAG, "onResponse: Server Response: " + response.toString());
//
//                if (response.code() == 200) {
//                    sessionManager.logoutUser();
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
//                Toast.makeText(ProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}
