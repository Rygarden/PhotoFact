package com.diploma.nurzhan.photo_fact.ui;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.SessionManager;
import com.diploma.nurzhan.photo_fact.repository.UserService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.diploma.nurzhan.photo_fact.Constants.KEY_EMAIL;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.fileImage;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.imageFile;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.mLatitude;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.mLongitude;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.timestamp;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.type;
import static com.diploma.nurzhan.photo_fact.ui.HomeActivity.typeString;

public class ConfirmActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmActivity";

    private UserService userService;

    private ImageView imageView;
    private TextView mType;
    private TextView time;
    private EditText mAddress;
    private String user;
    private Button btnImpose;
    private ImageButton btnProfile;
    private ImageButton btnBack;
    private TextView offenseImage;
    private CircleImageView offenseCircle;
    private LinearLayout imageLinear;
    private ImageButton editLocarion;

    private SessionManager sessionManager;
    private String newImageURI = "";
    private String fineAddress;
    private Dialog dialog;
    public static Double mapLat;
    public static Double mapLon;

    private ProgressBar mProgressBar;
    private int mProgressBarStatus = 0;
    private Handler mHandler = new Handler();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        newImageURI = getIntent().getStringExtra("imageFile");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Confirm");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mType = findViewById(R.id.fine_type);
        time = findViewById(R.id.fine_date);
        mAddress = findViewById(R.id.offenseAddress);
        btnImpose = findViewById(R.id.btnImpose);
//        btnProfile = findViewById(R.id.btnProfile);
//        btnBack = findViewById(R.id.btnBack);
        offenseImage = findViewById(R.id.circleImage);
        offenseCircle = findViewById(R.id.offensePhoto);
        imageLinear = findViewById(R.id.imageLinear);
        editLocarion = findViewById(R.id.editLocation);
        mProgressBar = findViewById(R.id.progressbar);

        userService = ApiUtils.getUserService();
        sessionManager = new SessionManager(getApplicationContext());

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
                        mProgressBar.setVisibility(View.GONE);
                        setValues();
                    }
                });
            }
        }).start();

        dialog = new Dialog(this);
        sessionManager.checkLogin();

        imageLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse(newImageURI), "image/*");
//                startActivity(intent);

                Intent intent = new Intent(ConfirmActivity.this, ImageActivity.class);
                intent.putExtra("imagePath", newImageURI);
                startActivity(intent);

//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(newImageURI)));
            }
        });

        btnImpose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                impose();
            }
        });

        editLocarion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (v == btnBack) {
//                    finish();
//                }
//            }
//        })


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setValues(){

        Geocoder geocoder, geocoder1 = null;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(HomeActivity.mLat, HomeActivity.mLon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            String city = addresses.get(0).getLocality();
//            String state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();

//            String listString = String.join(", ", address);

//            imageView.setImageBitmap(BitmapFactory.decodeFile(String.valueOf(imageFile)));

            List<Address> addresses1;
            geocoder1 = new Geocoder(this, Locale.getDefault());

            if(mapLat != null && mapLon != null){
                addresses1 = geocoder1.getFromLocation(mapLat, mapLon, 1);
                mAddress.setText(address, TextView.BufferType.EDITABLE);
            }else {
                mAddress.setText(address, TextView.BufferType.EDITABLE);
            }
            mType.setText(typeString);
            time.setText(timestamp);

//            imageView =  findViewById(R.id.offensePhoto);
//            imageView.setImageURI(Uri.parse(newImageURI));

            Picasso.get().load(newImageURI).into(offenseCircle);
            offenseImage.setText(getFileName(Uri.parse(newImageURI)));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString){
        return RequestBody.create(
                MultipartBody.FORM, descriptionString
        );
    }

    @Nullable
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        fileImage
                );
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, fileImage.getName(), requestFile);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void impose() {
        Log.d(TAG, "impose: called.");

        HashMap<String, String> userMap = sessionManager.getUserDetails();
        String email = userMap.get(KEY_EMAIL);



        MultipartBody.Part body = prepareFilePart("offense_image", imageFile);
        RequestBody username = createPartFromString(email);
        RequestBody typeOffense = createPartFromString(type);
        RequestBody locationA = createPartFromString(mLatitude);
        RequestBody locationB = createPartFromString(mLongitude);
        RequestBody fineAddress = createPartFromString(mAddress.getText().toString());
        RequestBody time = createPartFromString(timestamp);

        HashMap<String, RequestBody> fines = new HashMap<>();
        fines.put("username", username);
        fines.put("type_of_offense", typeOffense);
        fines.put("locationA", locationA);
        fines.put("locationB", locationB);
        fines.put("fineAddress", fineAddress);
        fines.put("time", time);

        Call<ResponseBody> call = userService.uploadFileWithPartMap(body, fines);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server response: " + response.toString());

                try{
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: json: " + json);

                    if(json.equals("True")){
                        Toast.makeText(ConfirmActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ConfirmActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e){
                    Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: Something went wrong: " + t.getLocalizedMessage());
                Toast.makeText(ConfirmActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
