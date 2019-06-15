package com.diploma.nurzhan.photo_fact.ui;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");

    private Button btSignIn;
    private ImageButton btnBack;
    private ConstraintLayout loginForm;

    private EditText edtEmail;
    private EditText edtPassword;

    private UserService userService;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btSignIn = findViewById(R.id.btnLogin);
        btnBack = findViewById(R.id.btnBack);
        loginForm = findViewById(R.id.loginForm);

        edtEmail = findViewById(R.id.inputUsername);
        edtPassword = findViewById(R.id.inputPassword);

        userService = ApiUtils.getUserService();

        sessionManager = new SessionManager(getApplicationContext());

        // Check if UserResponse is Already Logged In
//        if(SaveSharedPreferences.getLoggedStatus(getApplicationContext())) {
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class).putExtra("fullname", edtEmail.getText().toString());
//            startActivity(intent);
//            finish();
//        } else {
//            loginForm.setVisibility(View.VISIBLE);
//        }

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == btSignIn) {
                    String username = edtEmail.getText().toString();
                    String password = edtPassword.getText().toString();

                    //validate form
                    if (validateEmail(username) && validatePassword(password)) {
                        //do login
                        doLogin(username, password);
                    }
//                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                        startActivity(intent);
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


//
//    public void LoginUser() {
//        String Email = edtEmail.getText().toString().trim();
//        String Password = edtPassword.getText().toString().trim();
//
//        mAuth.signInWithEmailAndPassword(Email, Password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()){
//                            currentUser = mAuth.getCurrentUser();
//                            finish();
//                            startActivity(new Intent(getApplicationContext(),
//                                    HomeActivity.class));
//                        }else {
//                            Toast.makeText(LoginActivity.this, "couldn't login",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

    private boolean validateEmail(String username) {
        if (username.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Toast.makeText(LoginActivity.this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            edtEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Password field can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(LoginActivity.this, "Password too short", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            edtPassword.setError(null);
            return true;
        }
    }

    private void doLogin(final String username, final String password) {

        Map<String, String> userLoginData = new HashMap<>();

        userLoginData.put("username", username);
        userLoginData.put("pass", password);

        Call<ResponseBody> call = userService.login(userLoginData);

//        Call call = userService.login(username, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server Response: " + response.toString());
                try{
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: json: " + json);

                    if(json.equals("True")) {
                        sessionManager.createLoginSession(username, password);
                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(i);
                        finish();
//                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                        intent.putExtra("fullname", username);
//                        startActivity(intent);
//                        finish();

                        // Set Logged In statue to 'true'
//                        SaveSharedPreferences.setLoggedIn(getApplicationContext(), true);
//                        Intent mIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |FLAG_ACTIVITY_CLEAR_TASK).putExtra("fullname", username);
//                        startActivity(mIntent);
//                        finish();

                        // Creating user login session
                        // For testing i am stroing name, email as follow
                        // Use user real data
                    }else if(json.equals("False")){
                        Toast.makeText(LoginActivity.this, "The username or password is incorrect", Toast.LENGTH_SHORT).show();
                    }
//                    JSONObject data = null;
//                    data = new JSONObject(json);
//                    Log.d(TAG, "onResponse: data: " + data);
//                }catch (JSONException e){
//                    Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
                }catch (IOException e){
                    Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
                }
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//                if (response.isSuccessful()) {
//                    ResObj resObj = (ResObj) response.body();
//                    if (resObj.getMessage().equals("true")) {
//                        //login start main activity
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        intent.putExtra("username", username);
//                        startActivity(intent);
//
//                    } else {
//                        Toast.makeText(LoginActivity.this, "The username or password is incorrect", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(LoginActivity.this, "Error! Please try again!", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
