package com.homefood.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.homefood.R;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    boolean isChef = false;
    MaterialEditText edtUsername, edtPassword;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        aq =  new AQuery(this);
        tdb = new TinyDB(this);

        setContentView(R.layout.activity_login);
        isChef = getIntent().getExtras().getBoolean("isChef");
        btnLogin = (Button) findViewById(R.id.btnLogin);
        edtUsername = (MaterialEditText) findViewById(R.id.edtUsername);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtUsername.getText().toString().isEmpty()) {
//                    Toast.makeText(LoginActivity.this, "Email Address is required", Toast.LENGTH_SHORT).show();

                    edtUsername.setError("Required");
                    edtUsername.requestFocus();
                    return;
                }

                if (edtPassword.getText().toString().isEmpty()) {

                    edtPassword.setError("Required");
                    edtPassword.requestFocus();

//                    Toast.makeText(LoginActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                if (isChef) {
                    loginChefCall();
                } else
                    loginUserCall();
            }
        });

    }

    private void loginUserCall() {
        String url = Constants.getAPIURL() + "login.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", edtUsername.getText().toString());
        params.put("password", edtPassword.getText().toString());
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                if (json != null) {
                    Log.d("DEBUG", "login response:" + json.toString());
                    try {
                        if (!json.getBoolean("error")) {
                            pDialog.dismiss();
                            tdb.putString("logged_chef", "");
                            tdb.putJsonObject("logged_user", json.getJSONObject("user"));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        } else {

                            pDialog
                                    .setTitleText("Error!")
                                    .setContentText(json.getString("error_msg"))
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
                            .setContentText("No response from server!")
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }
        });
    }

    private void loginChefCall() {
        String url = Constants.getAPIURL() + "chef_login.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", edtUsername.getText().toString());
        params.put("password", edtPassword.getText().toString());
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                if (json != null) {
                    Log.d("DEBUG", "login response:" + json.toString());
                    try {
                        if (!json.getBoolean("error")) {
                            pDialog.dismiss();
                            tdb.putJsonObject("logged_chef", json.getJSONObject("chef"));
                            tdb.putString("logged_user", "");
                            startActivity(new Intent(LoginActivity.this, ChefMainActivity.class));
                            LoginActivity.this.finish();
                        } else {

                            pDialog
                                    .setTitleText("Error!")
                                    .setContentText(json.getString("error_msg"))
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
                            .setContentText("No response from server!")
                            .setConfirmText("OK")
                            .setConfirmClickListener(null)
                            .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                }
            }
        });
    }
}
