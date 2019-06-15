package com.diploma.nurzhan.photo_fact.repository;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.diploma.nurzhan.photo_fact.ui.LoginActivity;
import com.diploma.nurzhan.photo_fact.ui.MainActivity;

import java.util.HashMap;

import static com.diploma.nurzhan.photo_fact.Constants.KEY_EMAIL;
import static com.diploma.nurzhan.photo_fact.Constants.KEY_PASS;
import static com.diploma.nurzhan.photo_fact.Constants.PREF_NAME;
import static com.diploma.nurzhan.photo_fact.Constants.IS_LOGIN;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String email, String password){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_EMAIL, email);

        // Storing email in pref
        editor.putString(KEY_PASS, password);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }



    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // user pass
        user.put(KEY_PASS, pref.getString(KEY_PASS, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}