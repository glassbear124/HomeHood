package com.homefood.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.homefood.R;
import com.homefood.model.User;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    MaterialEditText edtUsername, edtPhoneNumber, edtEmailAddress, edtPassword;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        edtUsername = (MaterialEditText) findViewById(R.id.edtUsername);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhoneNumber = (MaterialEditText) findViewById(R.id.edtPhoneNumber);
        edtEmailAddress = (MaterialEditText) findViewById(R.id.edtEmailAddress);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edtUsername.getText().toString().isEmpty()) {
                    edtUsername.setError("Required");
                    edtUsername.requestFocus();
                    return;
                }

                if (edtPhoneNumber.getText().toString().isEmpty()) {
                    edtPhoneNumber.setError("Required");
                    edtPhoneNumber.requestFocus();
                    return;
                }

                if (edtEmailAddress.getText().toString().isEmpty()) {
                    edtEmailAddress.setError("Required");
                    edtEmailAddress.requestFocus();
                    return;
                }

                if (edtPassword.getText().toString().isEmpty()) {
                    edtPassword.setError("Required");
                    edtPassword.requestFocus();
                    return;
                }

                pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                registerUserCall();
            }
        });


    }

    private void registerUserCall() {
        String url = Constants.getAPIURL() + "register.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", edtEmailAddress.getText().toString());
        params.put("password", edtPassword.getText().toString());
        params.put("phone", edtPhoneNumber.getText().toString());
        params.put("name", edtUsername.getText().toString());
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                if (json != null) {
                    Log.d("DEBUG", "register response:" + json.toString());
                    try {
                        if (!json.getBoolean("error")) {
                            pDialog.dismiss();
                            tdb.putString("logged_chef", "");
                            tdb.putJsonObject("logged_user", json.getJSONObject("user"));
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            RegisterActivity.this.finish();
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
