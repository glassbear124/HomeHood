package com.homefood.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.homefood.R;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ChefRegisterActivity extends AppCompatActivity {

    TextView txtTitle;
    Button btnRegister, btnConfirm;
    MaterialEditText edtUsername, edtPhoneNumber, edtEmailAddress, edtPassword, edtOTP;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;

    LinearLayout otpLayout, signUpLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_register);

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        edtUsername = (MaterialEditText) findViewById(R.id.edtUsername);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtPhoneNumber = (MaterialEditText) findViewById(R.id.edtPhoneNumber);
        edtEmailAddress = (MaterialEditText) findViewById(R.id.edtEmailAddress);
        edtOTP = (MaterialEditText) findViewById(R.id.edtOTP);
        txtTitle = (TextView) findViewById(R.id.txtTitle);

        signUpLayout = (LinearLayout) findViewById(R.id.signUpLayout);
        otpLayout = (LinearLayout) findViewById(R.id.otpLayout);

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

                pDialog = new SweetAlertDialog(ChefRegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                registerChefCall();
            }
        });


        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtOTP.getText().toString().isEmpty()) {
//                    Toast.makeText(ChefRegisterActivity.this, "OTP is required!", Toast.LENGTH_SHORT).show();

                    edtOTP.setError("Required");
                    edtOTP.requestFocus();

                    return;
                }

                if (!edtOTP.getText().toString().equalsIgnoreCase(tdb.getString("otp"))) {
                    Toast.makeText(ChefRegisterActivity.this, "OTP is not valid!", Toast.LENGTH_SHORT).show();
                    return;
                }


                pDialog = new SweetAlertDialog(ChefRegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                emailVerifiedCall();
            }


        });

    }

    private void emailVerifiedCall() {
        String url = Constants.getAPIURL() + "email_verified.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", tdb.getString("chef_email"));
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                if (json != null) {
                    Log.d("DEBUG", "register response:" + json.toString());
                    try {
                        if (!json.getBoolean("error")) {
                            pDialog.dismiss();
                            Intent i = new Intent(ChefRegisterActivity.this, ChefProfileActivity.class);
                            i.putExtra("from_register", true);
                            startActivity(i);
                            ChefRegisterActivity.this.finish();
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

    private void registerChefCall() {
        String url = Constants.getAPIURL() + "chef_register.php";
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
                            tdb.putJsonObject("logged_chef", json.getJSONObject("chef"));
                            tdb.putString("logged_user", "");
                            tdb.putString("otp", json.getString("otp"));

//                            Toast.makeText(ChefRegisterActivity.this, "" + json.getString("otp"), Toast.LENGTH_SHORT).show();

                            tdb.putString("chef_email", json.getJSONObject("chef").getString("email"));

                            signUpLayout.setVisibility(View.GONE);
                            otpLayout.setVisibility(View.VISIBLE);
                            txtTitle.setText("Confirm OTP");
//                            startActivity(new Intent(ChefRegisterActivity.this, MainActivity.class));
//                            ChefRegisterActivity.this.finish();
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
