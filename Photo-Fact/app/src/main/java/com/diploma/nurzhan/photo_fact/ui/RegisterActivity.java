package com.diploma.nurzhan.photo_fact.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.SessionManager;
import com.diploma.nurzhan.photo_fact.repository.UserService;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");

    private Button btnRegister;
    private ImageButton btnBack;

    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtConfirmPassword;
    private EditText edtFullname;

    private UserService userService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btnRegister = findViewById(R.id.btSignUp);
        btnBack = findViewById(R.id.btnBack);

        edtEmail = findViewById(R.id.emailinput);
        edtPassword = findViewById(R.id.passwordinput);
        edtFullname = findViewById(R.id.fullnameInput);
        edtConfirmPassword = findViewById(R.id.passwordConfirm);

        userService = ApiUtils.getUserService();
        sessionManager = new SessionManager(getApplicationContext());

//        Toast.makeText(getApplicationContext(), "User Login Status: " + sessionManager.isLoggedIn(), Toast.LENGTH_LONG).show();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnRegister) {
                    String username = edtEmail.getText().toString().trim();
                    String password = edtPassword.getText().toString().trim();
                    String fullname = edtFullname.getText().toString().trim();
                    String confirmPassword = edtConfirmPassword.getText().toString().trim();
                    //validate form
                    if (validateEmail(username) && validatePassword(password, confirmPassword)) {
                        //do login
                        RegisterUser(username, password, fullname);
                    }

//                    LoginUser();
//                    startActivity(new Intent(getApplicationContext(),
//                            LoginActivity.class));
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btnBack) {
                    finish();
                }
            }
        });
    }


    public void RegisterUser(final String username, final String password, final String fullname) {
        HashMap<String, String> userRegistrationData = new HashMap<String, String>();

        userRegistrationData.put("username", username);
        userRegistrationData.put("pass", password);
        userRegistrationData.put("fullname", fullname);

        Call<ResponseBody> call = userService.register(userRegistrationData);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());

                try {
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: json: " + json);
                    if (json.equals("True")) {
                        sessionManager.createLoginSession(username, password);

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                        finish();

//                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
//                        startActivity(intent);

                        // Creating user login session
                        // For testing i am stroing name, email as follow
                        // Use user real data

                    } else if(json.equals("False")){
                        Toast.makeText(RegisterActivity.this, "The username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
//                    JSONObject data = null;
//                    data = new JSONObject(json);
//                    Log.d(TAG, "onResponse: data: " + data);
                }
//                catch (JSONException e){
//                    Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
//                }
                catch (IOException e) {
                    Log.e(TAG, "onResponse: JSONException: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateEmail(String username) {
        if (username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Toast.makeText(RegisterActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            edtEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password, String confirmPassword) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Password field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(RegisterActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            edtPassword.setError(null);
            return true;
        }
    }


//    public void RegisterUser(){
//
//        String Fullname = edtFullname.getText().toString().trim();
//        String Email = edtEmail.getText().toString().trim();
//        String Password = edtPassword.getText().toString().trim();
//
//        if (TextUtils.isEmpty(Fullname)){
//            Toast.makeText(this, "A Field is Empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (TextUtils.isEmpty(Email)){
//            Toast.makeText(this, "A Field is Empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        if (TextUtils.isEmpty(Password)){
//            Toast.makeText(this, "A Field is Empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        mAuth.createUserWithEmailAndPassword(Email, Password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        try {
//                            //check if successful
//                            if (task.isSuccessful()) {
//                                //User is successfully registered and logged in
//                                //start Profile Activity here
//                                Toast.makeText(RegisterActivity.this, "registration successful",
//                                        Toast.LENGTH_SHORT).show();
//                                finish();
//                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                            }else{
//                                Toast.makeText(RegisterActivity.this, "Couldn't register, try again",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }

}
