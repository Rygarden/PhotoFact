package com.diploma.nurzhan.photo_fact.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.nurzhan.photo_fact.Constants;
import com.diploma.nurzhan.photo_fact.MyLocation;
import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.models.Impose;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.SessionManager;
import com.diploma.nurzhan.photo_fact.repository.UserService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.diploma.nurzhan.photo_fact.Constants.KEY_EMAIL;
import static com.diploma.nurzhan.photo_fact.Constants.KEY_PASS;
import static com.diploma.nurzhan.photo_fact.Constants.PERMISSION_REQUEST_CAMERA;
import static com.diploma.nurzhan.photo_fact.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.diploma.nurzhan.photo_fact.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.diploma.nurzhan.photo_fact.Constants.REQUEST_TAKE_PHOTO;

public class HomeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "HomeActivity";

    private ImageButton btnProfile;
    private Button btnFines;
    private Button btnHistory;
    private ImageButton btnCamera;
    private Button btnSignOut;
    private TextView fineDescription;
    private Date currentTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private boolean mLocationPermissionGranted = false;
    private String currentPhotoPath;

    public static List<Impose> fines;
    public static Uri imageFile;
    public static File fileImage;
    public static String user;
    public static String type;
    public static String typeString;
    public static String mLatitude;
    public static double mLat;
    public static String mLongitude;
    public static double mLon;
    public static String timestamp;

    private LocationManager manager;

    private UserService userService;

    private MyLocation myLocation;

    private Spinner spinner;
    private static final String[] imposeType = {"Stopping on pedestrian crossing", "Stopping on Parking for \"disabled\" people", "\"No Stopping\" sign", "\"Parking is prohibited\" sign"};

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


//        btnProfile = findViewById(R.id.btnProfile);
        btnCamera = findViewById(R.id.btnOpen);

        fineDescription = findViewById(R.id.textViewFineDescription);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        fines = new ArrayList<>();

        userService = ApiUtils.getUserService();

        sessionManager = new SessionManager(getApplicationContext());

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Photo-Fact");




        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        sessionManager.checkLogin();

        // get user data from session
        HashMap<String, String> userMap = sessionManager.getUserDetails();

        // name
        String pass = userMap.get(KEY_PASS);

        // email
        final String email = userMap.get(KEY_EMAIL);

//        btnProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            }
//        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                        && (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
//                    isServicesEnabled();
                } else {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
                    dispatchTakePictureIntent();
                }
            }
        });

        spinner = (Spinner) findViewById(R.id.selectTypeOfImpose);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(HomeActivity.this,
                android.R.layout.simple_spinner_item, imposeType);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        switch (position) {
            case 0:
                // Whatever you want to happen when the first item gets selected
                fineDescription.setText("RK traffic rules, section: “Stopping and Parking” section 12.4, paragraph 5: “Stopping is prohibited on pedestrian crossings and closer than five meters in front of them”.");
                getIntent().putExtra("chooseOne", "FirstOne");
                type = "1";
                typeString = "Stopping on pedestrian crossing";
                break;
            case 1:
                int n = 2;
                // Whatever you want to happen when the second item gets selected
                fineDescription.setText("Violation of the rules of stopping or Parking the vehicle in places designated for stopping or Parking the vehicle disabled, Art. 597 h. 4 SDA RK.");
                getIntent().putExtra("chooseOne", "SecondOne");
                type = "2";
                typeString = "Stopping on Parking for \"disabled\" people";
                break;
            case 2:
                // Whatever you want to happen when the third item gets selected
                fineDescription.setText("Road sign 3.27 “No Stopping”. It is forbidden to stop and Park vehicles.");
                getIntent().putExtra("chooseOne", "ThirdOne");
                type = "3";
                typeString = "\"No Stopping\" sign";
                break;
            case 3:
                // Whatever you want to happen when the fourth item gets selected
                fineDescription.setText("3.28 “Parking is prohibited”. Parking of vehicles is prohibited.");
                getIntent().putExtra("chooseOne", "FourthOne");
                type = "4";
                typeString = "\"Parking is prohibited\" sign";
//                impose();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        btnCamera.setEnabled(false);
    }

    public void showMyLocation() {
        Log.d(TAG, "showMyLocation: called.");
        // create class object
        myLocation = new MyLocation(HomeActivity.this);
        // check if GPS enabled
        if (myLocation.canGetLocation()) {
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();

            mLatitude = Double.toString(latitude);
            mLongitude = Double.toString(longitude);

//            mLat = latitude;
//            mLon = longitude;

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            double longitude1 = location.getLongitude();
            double latitude1 = location.getLatitude();

            mLat = latitude1;
            mLon = longitude1;
            // \n is for new line
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
            Log.d(TAG, "showMyLocation: latitude: " + latitude1);
            Log.d(TAG, "showMyLocation: longitude: " + longitude1);
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            myLocation.showSettingsAlert();
            Log.d(TAG, "showMyLocation: Something wrong!");
        }
    }


    @SuppressLint("ResourceAsColor")
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.colorPrimary);
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(R.color.colorCustom);

    }

    public boolean isServicesEnabled(){
        Log.d(TAG, "isServicesEnabled: called.");
        manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            Log.d(TAG, "isServicesEnabled: request permission.");
            buildAlertMessageNoGps();
            return false;
        }

        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            showMyLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    showMyLocation();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            //TODO... onCamera Picker Result
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(mLocationPermissionGranted){
                    Log.d(TAG, "onActivityResult: taking location.");
                    showMyLocation();
                }
                else{
                    getLocationPermission();
                }
            }
            case REQUEST_TAKE_PHOTO:
            if (resultCode == Activity.RESULT_OK)
            {
                Intent intent = new Intent(HomeActivity.this, ConfirmActivity.class);
                intent.putExtra("imageFile", imageFile.toString());
                startActivity(intent);
            }


        }
    }

    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile: called.");
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d(TAG, "createImageFile: photo path: " + currentPhotoPath);
        return image;
    }

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent: called.");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d(TAG, "dispatchTakePictureIntent: save image file.");
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.diploma.nurzhan.photo_fact",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.d(TAG, "dispatchTakePictureIntent: captured image: " + takePictureIntent);
                galleryAddPic();
                imageFile = photoURI;
            }
        }
    }

    private void galleryAddPic() {
        Log.d(TAG, "galleryAddPic: called.");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        fileImage = f;
    }

    private void showTime(){
        currentTime = Calendar.getInstance().getTime();
        Log.d(TAG, "showTime: current Time: " + currentTime);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        timestamp = dateFormat.format(currentTime);
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

    public void impose() {
        Log.d(TAG, "impose: called.");
//        Map<String, Object> fines = new HashMap<>();
//
//        fines.put("offense_image", image);
//        fines.put("username", user);
//        fines.put("type_of_offense", type);
//        fines.put("locationA", mLatitude);
//        fines.put("locationB", mLongitude);
//        fines.put("time", timestamp);
//
//        Call<ResponseBody> call = userService.impose(fines);

//
//        MultipartBody.Part imageFile =
//                MultipartBody.Part.createFormData("picture", fileImage.getName(), requestFile);

        MultipartBody.Part body = prepareFilePart("offense_image", imageFile);

        // create a map of data to pass along
        RequestBody username = createPartFromString(user);
        RequestBody typeOffense = createPartFromString(type);
        RequestBody locationA = createPartFromString(mLatitude);
        RequestBody locationB = createPartFromString(mLongitude);
        RequestBody time = createPartFromString(timestamp);

        HashMap<String, RequestBody> fines = new HashMap<>();
        fines.put("username", username);
        fines.put("type_of_offense", typeOffense);
        fines.put("locationA", locationA);
        fines.put("locationB", locationB);
        fines.put("time", time);

        Call<ResponseBody> call = userService.uploadFileWithPartMap(body, fines);
//        Call<ResponseBody> call = userService.uploadFileWithPartMap(prepareFilePart("offense_image", imageFile),
//                createPartFromString(user), createPartFromString(type), createPartFromString(mLatitude), createPartFromString(mLongitude), createPartFromString(timestamp));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d(TAG, "onResponse: Server response: " + response.toString());

                try{
                    String json = response.body().string();
                    Log.d(TAG, "onResponse: json: " + json);

                    if(json.equals("True")){
                        Toast.makeText(HomeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(HomeActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e){
                    Log.e(TAG, "onResponse: JSONException: " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        showTime();
        if(isServicesEnabled()){
            if(mLocationPermissionGranted){
                showMyLocation();
            }
            else{
                getLocationPermission();
            }
        }
    }
}
