package com.homefood.ui;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.homefood.R;
import com.homefood.adapter.SearchChefAdapter;
import com.homefood.model.Chef;
import com.homefood.model.User;
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

public class MyFavoriteActivity extends AppCompatActivity {

    SearchChefAdapter mAdapter;
    public List<Chef> chefList = new ArrayList<Chef>();
    private RecyclerView recyclerView;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;
    TextView txtNoFavorite;
    User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        currUser = (User) tdb.getObject("logged_user", User.class);
        txtNoFavorite = (TextView) findViewById(R.id.txtNoFavorite);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new SearchChefAdapter(this, chefList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
    }

    private void getFavorite() {
        pDialog = new SweetAlertDialog(MyFavoriteActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        String url = Constants.getAPIURL() + "get_favorite.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("user_id", currUser.getId());
        aq.ajax(url, params, String.class, new AjaxCallback<String>() {
            @Override
            public void callback(String url, String json, AjaxStatus status) {

                if (json != null && json.trim().length() != 0 && !json.equalsIgnoreCase("null\n")) {
                    Log.d("DEBUG", "login response:" + json);
                    try {

                        JSONArray chefArray = new JSONArray(json);
                        if (chefArray.length() > 0) {

                            txtNoFavorite.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            chefList = (ArrayList<Chef>) fromJson(chefArray.toString(),
                                    new TypeToken<ArrayList<Chef>>() {
                                    }.getType());
                            Log.d("DEBUG", "size" + chefList.size());
                            mAdapter = new SearchChefAdapter(MyFavoriteActivity.this, chefList);
                            recyclerView.setAdapter(mAdapter);
                            pDialog.dismiss();
                        } else {

                            txtNoFavorite.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);

                            pDialog
                                    .setTitleText("Oops!")
                                    .setContentText("No Favorite Chef found...")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            MyFavoriteActivity.this.finish();
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
                                        MyFavoriteActivity.this.finish();
                                    }
                                })
                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                    }
                } else {
                    pDialog
                            .setTitleText("Oops!")
                            .setContentText("No Favorite Chef found...")
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    MyFavoriteActivity.this.finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        getFavorite();
    }
}
