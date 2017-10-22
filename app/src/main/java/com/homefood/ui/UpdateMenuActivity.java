package com.homefood.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.homefood.R;
import com.homefood.model.Chef;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateMenuActivity extends AppCompatActivity {

    EditText edtLunchMenu, edtDinnerMenu, edtSpecialMenu;
    Button btnUpdate;
    AQuery aq;
    TinyDB tdb;
    Chef currentChef;
    AjaxCallback updateCallBack;
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_menu);

        initViews();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validated()) {
                    pDialog = new SweetAlertDialog(UpdateMenuActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Loading...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    updateChefProfile();
                }
            }
        });


        updateCallBack = new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    Log.d("DEBUG", "updateUser response:" + json.toString());
                    try {
                        if (!json.getBoolean("error")) {

                            tdb.putJsonObject("logged_chef", json.getJSONObject("chef"));
                            pDialog
                                    .setTitleText("Updated!")
                                    .setContentText("Menu updated successfully.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            UpdateMenuActivity.this.finish();
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
        };

    }

    private void updateChefProfile() {

        if (getIntent().getExtras() != null) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("lunch", edtLunchMenu.getText().toString());
            returnIntent.putExtra("dinner", edtDinnerMenu.getText().toString());
            returnIntent.putExtra("special", edtSpecialMenu.getText().toString());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        } else {
            String url = Constants.getAPIURL() + "chef_update";
            Map<String, Object> params = new HashMap<>();

            params.put("name", currentChef.getName());
            params.put("phone", currentChef.getPhone());
            params.put("specialty", currentChef.getSpecialty());
            params.put("food_culture", currentChef.getFood_culture());

            params.put("lunch_menu", edtLunchMenu.getText().toString());
            params.put("dinner_menu", edtDinnerMenu.getText().toString());
            params.put("special_menu", edtSpecialMenu.getText().toString());

            params.put("address", currentChef.getAddress());
            params.put("latitude", currentChef.getLatitude());
            params.put("longitude", currentChef.getLongitude());

            params.put("hours", currentChef.getHours());
            params.put("deadline", currentChef.getDeadline());
            params.put("services", currentChef.getServices());
            params.put("veg_nonveg", currentChef.getVeg_nonveg());
            params.put("discovery", currentChef.getDiscovery());

            params.put("id", currentChef.getId());
            aq.ajax(url, params, JSONObject.class, updateCallBack);
        }
    }


    private boolean validated() {
        if (edtLunchMenu.getText().toString().isEmpty()) {
            edtLunchMenu.setError("Required");
            edtLunchMenu.requestFocus();
            return false;
        } else if (edtDinnerMenu.getText().toString().isEmpty()) {
            edtDinnerMenu.setError("Required");
            edtDinnerMenu.requestFocus();
            return false;
        } else if (edtSpecialMenu.getText().toString().isEmpty()) {
            edtSpecialMenu.setError("Required");
            edtSpecialMenu.requestFocus();
            return false;
        }
        return true;
    }

    private void initViews() {

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        edtLunchMenu = (EditText) findViewById(R.id.edtLunchMenu);
        edtDinnerMenu = (EditText) findViewById(R.id.edtDinnerMenu);
        edtSpecialMenu = (EditText) findViewById(R.id.edtSpecialMenu);

        if (getIntent().getExtras() != null) {

            String lunch = getIntent().getStringExtra("lunch");
            String dinner = getIntent().getStringExtra("dinner");
            String special = getIntent().getStringExtra("special");

            if (!lunch.equalsIgnoreCase("Tap to edit..."))
                edtLunchMenu.setText(lunch);

            if (!dinner.equalsIgnoreCase("Tap to edit..."))
                edtDinnerMenu.setText(dinner);

            if (!special.equalsIgnoreCase("Tap to edit..."))
                edtSpecialMenu.setText(special);

        } else {
            currentChef = (Chef) tdb.getObject("logged_chef", Chef.class);
            edtLunchMenu.setText(currentChef.getLunch_menu());
            edtDinnerMenu.setText(currentChef.getDinner_menu());
            edtSpecialMenu.setText(currentChef.getSpecial_menu());
        }

    }

}
