package com.homefood.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.reflect.TypeToken;
import com.homefood.R;
import com.homefood.adapter.SearchChefAdapter;
import com.homefood.model.Chef;
import com.homefood.model.User;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    User currUser;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;
    TextView mTitle;
    public static Location locationCurrent;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private LocationManager locationManager;
    private String provider;

    ImageView imgDiscover, imgFav, imgProfile, imgLogout;
    LinearLayout searchLayout;
    int MY_PERMISSIONS_REQUEST_READ_GPS = 9;
    Button btnLogout;

    TextView txtBengali, txtAssami, txtGujarati, txtBihari, txtGoan, txtHyderabadi, txtMaharashtrian, txtPunjabi, txtRajasthani, txtSouthIndian;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        aq = new AQuery(this);
        tdb = new TinyDB(this);
        currUser = (User) tdb.getObject("logged_user", User.class);

        setContentView(R.layout.activity_main_new);

        imgDiscover = (ImageView) findViewById(R.id.imgDiscover);
        searchLayout = (LinearLayout) findViewById(R.id.searchLayout);
        imgFav = (ImageView) findViewById(R.id.imgFav);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        imgLogout = (ImageView) findViewById(R.id.imgLogout);
        txtBengali = (TextView) findViewById(R.id.txtBengali);
        txtAssami = (TextView) findViewById(R.id.txtAssami);
        txtGujarati = (TextView) findViewById(R.id.txtGujarati);
        txtBihari = (TextView) findViewById(R.id.txtBihari);
        txtGoan = (TextView) findViewById(R.id.txtGoan);
        txtHyderabadi = (TextView) findViewById(R.id.txtHyderabadi);
        txtMaharashtrian = (TextView) findViewById(R.id.txtMaharashtrian);
        txtPunjabi = (TextView) findViewById(R.id.txtPunjabi);
        txtRajasthani = (TextView) findViewById(R.id.txtRajasthani);
        txtSouthIndian = (TextView) findViewById(R.id.txtSouthIndian);

//        mTitle = (TextView) findViewById(R.id.mtitle);
//
//        if (currUser == null)
//            return;
//
//        mTitle.setText(currUser.getName());

        buildGoogleApiClient();

        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DiscoverActivity.class);
                startActivity(i);
            }
        });

        searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });

        imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyFavoriteActivity.class);
                startActivity(i);
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EditUserProfileActivity.class);
                startActivity(i);
            }
        });

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                MainActivity.this.finish();
            }
        });

        txtSouthIndian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "South Indian");
                startActivity(i);
            }
        });

        txtRajasthani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Rajasthani");
                startActivity(i);
            }
        });

        txtPunjabi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Punjabi");
                startActivity(i);
            }
        });

        txtBihari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Bihari");
                startActivity(i);
            }
        });

        txtHyderabadi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Hyderabadi");
                startActivity(i);
            }
        });

        txtGoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Goan");
                startActivity(i);
            }
        });

        txtAssami.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Assami");
                startActivity(i);
            }
        });

        txtGujarati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Gujarati");
                startActivity(i);
            }
        });

        txtMaharashtrian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Maharashtrian");
                startActivity(i);
            }
        });

        txtBengali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, CusineActivity.class);
                i.putExtra("cusine", "Bengali");
                startActivity(i);
            }
        });

        getCount();
    }

    private void getPermission() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_READ_GPS);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Enable GPS")
                    .setContentText("Please enable GPS to continue use this app")
                    .setConfirmText("Enable")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .show();
        }


    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(10000); // Update location every second
        mLocationRequest.setInterval(5000);
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setFastestInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        locationCurrent = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (locationCurrent != null) {
            Toast.makeText(this, "Location Found", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location not Found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        locationCurrent = location;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_GPS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "Please grant the Permission for GPS", Toast.LENGTH_SHORT).show();
                getPermission();
            }
        }

    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    private void getCount() {
        pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = Constants.getAPIURL() + "dashboard.php";
        Map<String, Object> params = new HashMap<String, Object>();
        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json, AjaxStatus status) {

                if (json != null && json.trim().length() != 0 && !json.equalsIgnoreCase("null\n")) {
                    Log.d("DEBUG", "login response:" + json);
                    try {

                        JSONArray countArray = new JSONArray(json);
                        if (countArray.length() > 0) {
                            setCountToTextView(countArray);
                            pDialog.dismiss();
                        } else {
                            pDialog
                                    .setTitleText("Oops!")
                                    .setContentText("No Favorite Chef found...")
                                    .setConfirmText("OK")
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("DEBUG", "JSONException:" + e.getMessage());
                        pDialog
                                .setTitleText("Oops!")
                                .setContentText("Something went wrong, try later!")
                                .setConfirmText("OK")
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                } else {
                }
            }
        });
    }

    private void setCountToTextView(JSONArray countArray) {

        try {
            for (int i = 0; i < countArray.length(); i++) {

                String culture = countArray.getJSONObject(i).getString("food_culture");
                String count = countArray.getJSONObject(i).getString("count");

                if (culture.contains("Gujarati")) {
                    txtGujarati.setText(culture + "\n" + count);
                } else if (culture.contains("Maharashtrian")) {
                    txtMaharashtrian.setText(culture + "\n" + count);
                } else if (culture.contains("South Indian")) {
                    txtSouthIndian.setText(culture + "\n" + count);
                } else if (culture.contains("Rajasthani")) {
                    txtRajasthani.setText(culture + "\n" + count);
                } else if (culture.contains("Bengali")) {
                    txtBengali.setText(culture + "\n" + count);
                } else if (culture.contains("Goan")) {
                    txtGoan.setText(culture + "\n" + count);
                } else if (culture.contains("Hyderabadi")) {
                    txtHyderabadi.setText(culture + "\n" + count);
                } else if (culture.contains("Assami")) {
                    txtAssami.setText(culture + "\n" + count);
                } else if (culture.contains("Bihari")) {
                    txtBihari.setText(culture + "\n" + count);
                } else if (culture.contains("Punjabi")) {
                    txtPunjabi.setText(culture + "\n" + count);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        provider = locationManager.getBestProvider(criteria, false);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            getPermission();
//            return;
//        }
//        locationCurrent = locationManager.getLastKnownLocation(provider);
//
//        if (locationCurrent != null) {
//            System.out.println("Provider " + provider + " has been selected.");
//            onLocationChanged(locationCurrent);
//        } else {
////            latituteField.setText("Location not available");
////            longitudeField.setText("Location not available");
//            Toast.makeText(this, "Location not found...", Toast.LENGTH_SHORT).show();
//        }