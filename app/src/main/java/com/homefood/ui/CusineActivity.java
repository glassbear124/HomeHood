package com.homefood.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.homefood.R;
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

public class CusineActivity extends AppCompatActivity {

    SearchChefAdapter mAdapter;
    public List<Chef> chefList = new ArrayList<Chef>();
    private RecyclerView recyclerView;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;

    String cuisine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cusine);

        cuisine = getIntent().getExtras().getString("cusine");

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new SearchChefAdapter(this, chefList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        searchChefs();
    }

    private void searchChefs() {

        pDialog = new SweetAlertDialog(CusineActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Searching...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = Constants.getAPIURL() + "search.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("search", "");
        params.put("veg", "");
        params.put("cuisine", cuisine);
        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json, AjaxStatus status) {

                if (json != null && json.trim().length() != 0 && !json.equalsIgnoreCase("null\n")) {
                    Log.d("DEBUG", "login response:" + json);
                    try {
                        JSONArray chefArray = new JSONArray(json);
                        if (chefArray.length() > 0) {

                            chefList = (ArrayList<Chef>) fromJson(chefArray.toString(),
                                    new TypeToken<ArrayList<Chef>>() {
                                    }.getType());
                            Log.d("DEBUG", "size" + chefList.size());
                            mAdapter = new SearchChefAdapter(CusineActivity.this, chefList);
                            recyclerView.setAdapter(mAdapter);
                            pDialog.dismiss();
                        } else {
                            pDialog
                                    .setTitleText("Oops!")
                                    .setContentText("No Chef found...")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            CusineActivity.this.finish();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("DEBUG", "JSONException:" + e.getMessage());
                        pDialog
                                .setTitleText("Oops!")
                                .setContentText("Something went wrong, try later!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        CusineActivity.this.finish();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                } else {
                    pDialog
                            .setTitleText("Oops!")
                            .setContentText("No Chef found...")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    CusineActivity.this.finish();
                                }
                            })
                            .changeAlertType(SweetAlertDialog.WARNING_TYPE);
                }
            }
        });

    }

    public Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }
}
