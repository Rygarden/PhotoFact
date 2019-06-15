package com.diploma.nurzhan.photo_fact.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.SessionManager;
import com.diploma.nurzhan.photo_fact.repository.UserService;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.diploma.nurzhan.photo_fact.Constants.KEY_EMAIL;
import static com.diploma.nurzhan.photo_fact.Constants.KEY_PASS;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.imageFile;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.mLatitude;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.mLongitude;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.timestamp;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.type;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.user;

public class CardActivity extends AppCompatActivity {

    private static final String TAG = "CardActivity";

    private EditText card_number;
    private EditText iin;
    private Button button;
    private String mCardNumber;
    private String mIIN;

    private SessionManager sessionManager;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        card_number = findViewById(R.id.edtCardNumber);
        iin = findViewById(R.id.edtIIN);
        button = findViewById(R.id.btnAdd);

        userService = ApiUtils.getUserService();
        sessionManager = new SessionManager(getApplicationContext());

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add card");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCardNumber = card_number.getText().toString();
        mIIN = iin.getText().toString();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCardNumber();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void addCardNumber() {
        Log.d(TAG, "addCardNumber: called.");

        HashMap<String, String> userMap = sessionManager.getUserDetails();
        String email = userMap.get(KEY_EMAIL);
        String pass = userMap.get(KEY_PASS);

        HashMap<String, String> cards = new HashMap<>();
        cards.put("username", email);
        cards.put("pass", pass);
        cards.put("card_number", mCardNumber);
        cards.put("iin_number", mIIN);

        Call<String> call = userService.addCard(cards);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: Server response: " + response.toString());

                String json = response.body().toString();
                Log.d(TAG, "onResponse: json: " + json);

                if (json.equals("True")) {
                    Toast.makeText(CardActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CardActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
                Toast.makeText(CardActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
