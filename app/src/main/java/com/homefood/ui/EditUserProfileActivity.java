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
import com.homefood.model.User;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditUserProfileActivity extends AppCompatActivity {

    Button btnRegister;
    MaterialEditText edtUsername, edtPhoneNumber, edtEmailAddress;
    SweetAlertDialog pDialog;
    AQuery aq;
    TinyDB tdb;
    User currUser;
    TextView txtChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        currUser = (User) tdb.getObject("logged_user", User.class);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        edtUsername = (MaterialEditText) findViewById(R.id.edtUsername);
        edtPhoneNumber = (MaterialEditText) findViewById(R.id.edtPhoneNumber);
        edtEmailAddress = (MaterialEditText) findViewById(R.id.edtEmailAddress);
        txtChangePassword = (TextView) findViewById(R.id.txtChangePassword);

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

                pDialog = new SweetAlertDialog(EditUserProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Loading...");
                pDialog.setCancelable(false);
                pDialog.show();

                registerUserCall();
            }
        });

        edtUsername.setText(currUser.getName());
        edtEmailAddress.setText(currUser.getEmail());
        edtPhoneNumber.setText(currUser.getPhone());

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditUserProfileActivity.this, ChangePasswordActivity.class);
                startActivity(i);
            }
        });

    }

    private void registerUserCall() {
        String url = Constants.getAPIURL() + "user_update.php";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("email", edtEmailAddress.getText().toString());
        params.put("id", currUser.getId());
        params.put("phone", edtPhoneNumber.getText().toString());
        params.put("name", edtUsername.getText().toString());
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {

                if (json != null) {
                    Log.d("DEBUG", "update response:" + json.toString());
                    try {
                        if (!json.getBoolean("error")) {
                            tdb.putString("logged_chef", "");
                            tdb.putJsonObject("logged_user", json.getJSONObject("user"));
                            pDialog
                                    .setTitleText("Profile Updated!")
                                    .setContentText("Profile updated successfully.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            startActivity(new Intent(EditUserProfileActivity.this, MainActivity.class));
                                            EditUserProfileActivity.this.finish();
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
