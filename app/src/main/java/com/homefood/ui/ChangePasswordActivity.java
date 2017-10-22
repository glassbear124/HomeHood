package com.homefood.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.homefood.R;
import com.homefood.model.Chef;
import com.homefood.model.User;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChangePasswordActivity extends AppCompatActivity {

    Button btnReset;
    MaterialEditText edtCPassword, edtPassword;
    Chef currentChef;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;
    User currUser;
    boolean isChef = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        if (tdb.getString("logged_user").equalsIgnoreCase("") && !tdb.getString("logged_chef").equalsIgnoreCase("")) {
            currentChef = (Chef) tdb.getObject("logged_chef", Chef.class);
            isChef = true;
        } else if (!tdb.getString("logged_user").equalsIgnoreCase("") && tdb.getString("logged_chef").equalsIgnoreCase("")) {
            currUser = (User) tdb.getObject("logged_user", User.class);
            isChef = false;
        } else
            return;


        btnReset = (Button) findViewById(R.id.btnReset);
        edtCPassword = (MaterialEditText) findViewById(R.id.edtCPassword);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtPassword.getText().toString().isEmpty()) {
                    edtPassword.setError("Required");
                    edtPassword.requestFocus();
                    return;
                }

                if (edtCPassword.getText().toString().isEmpty()) {
                    edtCPassword.setError("Required");
                    edtCPassword.requestFocus();
                    return;
                }

                if (!edtCPassword.getText().toString().equals(edtPassword.getText().toString())) {
                    edtCPassword.setError("Password does not Match");
                    edtCPassword.requestFocus();
                    return;
                }

                pDialog = new SweetAlertDialog(ChangePasswordActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                registerUserCall();
            }
        });


    }

    private void registerUserCall() {
        String url = Constants.getAPIURL() + "changepassword.php";
        Map<String, Object> params = new HashMap<String, Object>();

        if (isChef) {
            params.put("email", currentChef.getEmail());
            params.put("type", 1);
        } else {
            params.put("email", currUser.getEmail());
            params.put("type", 2);
        }

        params.put("password", edtPassword.getText().toString());

        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                if (json != null) {
                    Log.d("DEBUG", "update response:" + json.toString());
                    try {
                        if (!json.getBoolean("error")) {
                            pDialog
                                    .setTitleText("Password Updated!")
                                    .setContentText("Password changed successfully.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            ChangePasswordActivity.this.finish();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);

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
