package com.homefood.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.homefood.R;
import com.homefood.adapter.DiscoverAdapter;
import com.homefood.adapter.SearchChefAdapter;
import com.homefood.model.Chef;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DiscoverActivity extends AppCompatActivity {

    LinearLayout discoverLayout, listLayout;
    FrameLayout dineinLayout, takeoutLayout, deliveryLayout;
    DiscoverAdapter mAdapter;
    public List<Chef> chefList = new ArrayList<Chef>();
    private RecyclerView recyclerView;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;

    String serviceType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        listLayout = (LinearLayout) findViewById(R.id.listLayout);
        discoverLayout = (LinearLayout) findViewById(R.id.discoverLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        dineinLayout = (FrameLayout) findViewById(R.id.dineinLayout);
        takeoutLayout = (FrameLayout) findViewById(R.id.takeoutLayout);
        deliveryLayout = (FrameLayout) findViewById(R.id.deliveryLayout);

        mAdapter = new DiscoverAdapter(this, chefList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if (MainActivity.locationCurrent == null) {
            Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show();
            return;
        }


        dineinLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "dine_in";
                discoverChefs();
            }
        });

        takeoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "take_out";
                discoverChefs();
            }
        });

        deliveryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceType = "delivery";
                discoverChefs();
            }
        });

    }

    private void discoverChefs() {

        pDialog = new SweetAlertDialog(DiscoverActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Searching...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = Constants.getAPIURL() + "discover.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("services", serviceType);
        params.put("lat", MainActivity.locationCurrent.getLatitude() + "");
        params.put("long", MainActivity.locationCurrent.getLongitude() + "");
        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json, AjaxStatus status) {

                if (json != null && json.trim().length() != 0 && !json.equalsIgnoreCase("null\n")) {
                    Log.d("DEBUG", "login response:" + json);
                    try {

                        JSONArray chefArray = new JSONArray(json);
                        if (chefArray.length() > 0) {

                            discoverLayout.setVisibility(View.GONE);
                            listLayout.setVisibility(View.VISIBLE);

                            chefList = (ArrayList<Chef>) fromJson(chefArray.toString(),
                                    new TypeToken<ArrayList<Chef>>() {
                                    }.getType());
                            Log.d("DEBUG", "size" + chefList.size());
                            mAdapter = new DiscoverAdapter(DiscoverActivity.this, chefList);
                            recyclerView.setAdapter(mAdapter);
                            pDialog.dismiss();
                        } else {
                            pDialog
                                    .setTitleText("Oops!")
                                    .setContentText("No Nearby Chef found...")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("DEBUG", "JSONException:" + e.getMessage());
                        pDialog
                                .setTitleText("Oops!")
                                .setContentText("Something went wrong, try later!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                } else {
                    pDialog
                            .setTitleText("Oops!")
                            .setContentText("No Nearby Chef found...")
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                }
            }
        });

    }

    public Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }
}
