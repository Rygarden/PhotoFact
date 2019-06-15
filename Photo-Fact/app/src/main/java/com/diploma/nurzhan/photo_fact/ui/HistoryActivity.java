package com.diploma.nurzhan.photo_fact.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.RecyclerItemTouchHelper;
import com.diploma.nurzhan.photo_fact.RecyclerItemTouchHelperListener;
import com.diploma.nurzhan.photo_fact.adapters.HistoryRecyclerViewAdapter;
import com.diploma.nurzhan.photo_fact.models.JSONmodels.Data;
import com.diploma.nurzhan.photo_fact.models.JSONmodels.Offense;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.SessionManager;
import com.diploma.nurzhan.photo_fact.repository.UserService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.diploma.nurzhan.photo_fact.Constants.KEY_EMAIL;
import static com.diploma.nurzhan.photo_fact.Constants.KEY_PASS;

public class HistoryActivity extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    private static final String TAG = "HistoryActivity";

    private HistoryRecyclerViewAdapter adapter;
    private List<Data> dataList;

    private UserService userService;
    private RecyclerView recyclerView;
//    private RelativeLayout parentLayout;
    private CoordinatorLayout rootLayout;
    private TextView emptyView;

    private ImageButton btnBack;

    private SessionManager sessionManager;
    private HashMap<String, String> userMap;
    private String email;
    private String pass;

    private ProgressBar mProgressBar;
    private int mProgressBarStatus = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

//        btnBack = findViewById(R.id.btnBack);

        recyclerView = findViewById(R.id.recyclerViewHistory);
        mProgressBar = findViewById(R.id.progressbar);
//        parentLayout = findViewById(R.id.parent_layout);

        userService = ApiUtils.getUserService();
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rootLayout = findViewById(R.id.parent_layout);
        emptyView = findViewById(R.id.empty_view);

        userMap = sessionManager.getUserDetails();
        email = userMap.get(KEY_EMAIL);
        pass = userMap.get(KEY_PASS);

        recyclerView.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mProgressBarStatus < 100){
                    mProgressBarStatus++;
                    android.os.SystemClock.sleep(10);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(mProgressBarStatus);
                        }
                    });
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        loadJSON();
                        mProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == btnBack) {
//                    finish();
//                }
//            }
//        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void loadJSON(){
        Log.d(TAG, "initItems: called.");

        Map<String, String> usernames = new HashMap<>();
        usernames.put("username", email);

        userService.loadHistory(usernames).enqueue(new Callback<Offense>() {
            @Override
            public void onResponse(Call<Offense> call, Response<Offense> response) {
                    Log.d(TAG, "onResponse: Server response: " + response.body());

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Log.d(TAG, "onResponse: on Success: " + response.body());

                            Offense offense = response.body();
//                            Log.d(TAG, "onResponse: check");
//                            String stringResponse = response.body().toString();
//                            Log.d(TAG, "onResponse: check2");
//                                JSONObject obj = new JSONObject((Map) response.body());
//                                Log.d(TAG, "onResponse: check3");
//                                if (obj.optString("status").equals("True")) {
//                                    Log.d(TAG, "onResponse: check4");
//                                    data = new ArrayList(Arrays.asList(offense.getData()));


                                    recyclerView.setHasFixedSize(true);
                                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                    recyclerView.setLayoutManager(layoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
//                                    dataList.addAll(response.body().getData());
                                    adapter = new HistoryRecyclerViewAdapter(HistoryActivity.this, offense.getData());
                                    recyclerView.addItemDecoration(new DividerItemDecoration(HistoryActivity.this, LinearLayoutManager.VERTICAL));

                                    recyclerView.setAdapter(adapter);

                            if (offense.getData() == null) {
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                            }

                                    adapter.notifyDataSetChanged();

//                                } else {
//                                    Toast.makeText(HistoryActivity.this, obj.optString("message") + "", Toast.LENGTH_SHORT).show();
//                                }
                        }else {
                                    Log.d(TAG, "onEmptyResponse: Returned empty response");
                        }

                        adapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onFailure(Call<Offense> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
                Toast.makeText(HistoryActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof HistoryRecyclerViewAdapter.ViewHolder){
            String imageName = HistoryRecyclerViewAdapter.dataModelList.get(viewHolder.getAdapterPosition()).getOffenseImageName();

            final Data deletedItem = HistoryRecyclerViewAdapter.dataModelList.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            deleteOffense(imageName);
            adapter.removeItem(deleteIndex);

//            Snackbar snackbar = Snackbar.make(rootLayout, imageName + " remove from history", Snackbar.LENGTH_LONG);
//            snackbar.setAction("UNDO", new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    adapter.restoreItem(deletedItem, deleteIndex);
//                }
//            });
//            snackbar.setActionTextColor(Color.YELLOW);
//            snackbar.show();
        }
    }

    private void deleteOffense(String imageName){
        Log.d(TAG, "deleteOffense: called.");

        Map<String, String> usernames = new HashMap<>();
        usernames.put("username", email);
        usernames.put("pass", pass);
        usernames.put("offense_image", imageName);

        userService.deleteOffense(usernames).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server response: " + response.body());

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Log.d(TAG, "onResponse: on Success: " + response.body());
                    }else {
                        Log.d(TAG, "onEmptyResponse: Returned empty response");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
            }
        });
    }
}


