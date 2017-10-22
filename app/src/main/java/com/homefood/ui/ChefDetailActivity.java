package com.homefood.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.homefood.R;
import com.homefood.model.Chef;
import com.homefood.model.User;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.homefood.R.id.profile_image;

public class ChefDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    Chef currentChef;
    User currUser;
    SweetAlertDialog pDialog;
    ImageView imgFav;
    MapView mapView;
    GoogleMap map;
    AQuery aq;
    TinyDB tdb;
    LatLng point;
    TextView txtName, txtaddress, txtPhone, txtFoodType, txtHours, txtLunchMenu, txtDinnerMenu, txtSpecialMenu;

    boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_detail);

        aq = new AQuery(this);
        tdb = new TinyDB(this);
        currUser = (User) tdb.getObject("logged_user", User.class);
        txtName = (TextView) findViewById(R.id.txtName);
        txtPhone = (TextView) findViewById(R.id.txtPhone);
        txtHours = (TextView) findViewById(R.id.txtHours);
        txtFoodType = (TextView) findViewById(R.id.txtFoodType);
        txtLunchMenu = (TextView) findViewById(R.id.txtLunchMenu);
        txtDinnerMenu = (TextView) findViewById(R.id.txtDinnerMenu);
        txtSpecialMenu = (TextView) findViewById(R.id.txtSpecialMenu);
        txtaddress = (TextView) findViewById(R.id.txtaddress);
        imgFav = (ImageView) findViewById(R.id.imgFav);

        currentChef = (Chef) getIntent().getSerializableExtra("chef");

        if (currentChef == null)
            return;

        if (currentChef.getProfile_pic() != null && !currentChef.getProfile_pic().isEmpty()) {
            String pro_url = currentChef.getProfile_pic();
            pro_url = pro_url.replace("..", "http://restora.ninepixel.in");
            aq.id(profile_image).image(pro_url);
        }

        txtName.setText(currentChef.getName());
        txtPhone.setText(currentChef.getPhone());
        txtHours.setText(currentChef.getHours());
        txtFoodType.setText(currentChef.getVeg_nonveg().toUpperCase());
        txtLunchMenu.setText(currentChef.getLunch_menu());
        txtDinnerMenu.setText(currentChef.getDinner_menu());
        txtSpecialMenu.setText(currentChef.getSpecial_menu());
        txtaddress.setText(currentChef.getAddress());

        point = new LatLng(Double.valueOf(currentChef.getLatitude()), Double.valueOf(currentChef.getLongitude()));

        try {
            // Gets the MapView from the XML layout and creates it
            mapView = (MapView) findViewById(R.id.mapview);
            mapView.onCreate(savedInstanceState);
            // Gets to GoogleMap from the MapView and does initialization stuff
            mapView.getMapAsync(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        checkForFavorite();

        imgFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrRemoveFav();
            }
        });

    }

    private void addOrRemoveFav() {

        pDialog = new SweetAlertDialog(ChefDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = Constants.getAPIURL() + "save_favorite.php";
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("user_id", currUser.getId());
        params.put("chef_id", currentChef.getId());

        if (isFavorite)
            params.put("isdeleted", 1);
        else
            params.put("isdeleted", 0);

        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json, AjaxStatus status) {
                pDialog.dismiss();

                isFavorite = !isFavorite;

                if (isFavorite) {
                    imgFav.setImageResource(R.drawable.fave_remove);
                } else {
                    imgFav.setImageResource(R.drawable.fav);
                }
            }
        });

    }

    private void checkForFavorite() {

        pDialog = new SweetAlertDialog(ChefDetailActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = Constants.getAPIURL() + "isfavoritechef.php";
        Map<String, Object> params = new HashMap<String, Object>();

        params.put("user_id", currUser.getId());
        params.put("chef_id", currentChef.getId());

        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json, AjaxStatus status) {

                if (json != null) {
                    if (json.contains("true")) {
                        isFavorite = true;
                        pDialog.dismiss();
                    } else {
                        isFavorite = false;
                        pDialog.dismiss();
                    }

                    if (isFavorite) {
                        imgFav.setImageResource(R.drawable.fave_remove);
                    } else {
                        imgFav.setImageResource(R.drawable.fav);
                    }

                } else {
                    pDialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Updates the location and zoom of the MapView

        Marker marker = map.addMarker(new MarkerOptions()
                .position(point)
                .title(currentChef.getName())
        );

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, 14);
        map.animateCamera(cameraUpdate);

    }
}
