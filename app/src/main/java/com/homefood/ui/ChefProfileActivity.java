package com.homefood.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.homefood.R;
import com.homefood.model.Chef;
import com.homefood.util.Constants;
import com.homefood.util.TinyDB;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;


public class ChefProfileActivity extends AppCompatActivity {

    Button btnUpdateProfile;
    MaterialEditText edtName, edtPhoneNumber, edtSpeciality, edtLunchHours, edtHours, edtDeadline, edtLunchDeadline;
    TextView txtLunchMenu, txtDinnerMenu, txtSpecialMenu;
    SweetAlertDialog pDialog;
    TextView txtFoodCulture;
    AQuery aq;
    TinyDB tdb;
    Chef currentChef;
    AjaxCallback updateCallBack;
    CircleImageView profile_image;
    File profileFile;
    Bitmap currentBitmap;
    LinearLayout cbServicesLayout;
    RadioGroup rgVegNonVeg;
    RadioButton rdVeg, rdNonVeg, rdBoth;
    CheckBox cbDinein, cbTakeout, cbDelivery;
    Switch switchButtonDiscovery;
    TextView txtPlace;
    String strLat = null, strLong = null, strCity = null;

    boolean fromRegister = false;

    private MaterialDialog pickerDialog;
    public static int RESULT_LOAD_IMG = 9898;
    public static String mCurrentPhotoPath;

    public static boolean isPhotoSet = false;

    private static final int PLACE_PICKER_REQUEST = 1;
    Button btnPickPlace;

    final CharSequence[] items = {" Gujarati ", " Maharashtrian ", " South Indian ", " Rajasthani ", " Bengali ", " Goan ", " Hyderabadi ", " Assami ", " Bihari ", " Punjabi "};
    final ArrayList seletedItems = new ArrayList();
    public boolean[] checkedItems = new boolean[items.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_profile);

        if (getIntent().getExtras() != null)
            fromRegister = getIntent().getExtras().getBoolean("from_register");

        initViews();

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validated()) {
                    pDialog = new SweetAlertDialog(ChefProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
//                            pDialog.dismiss();
                            tdb.putJsonObject("logged_chef", json.getJSONObject("chef"));
                            tdb.putBoolean("is_profile_completed", true);

                            pDialog
                                    .setTitleText("Profile Updated!")
                                    .setContentText("Profile updated successfully.")
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismiss();
                                            if (fromRegister)
                                                startActivity(new Intent(ChefProfileActivity.this, ChefMainActivity.class));
                                            ChefProfileActivity.this.finish();
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
        String url = Constants.getAPIURL() + "chef_update";
        Map<String, Object> params = new HashMap<>();
        params.put("name", edtName.getText().toString());
        params.put("phone", edtPhoneNumber.getText().toString());
        params.put("specialty", edtSpeciality.getText().toString());
        params.put("food_culture", txtFoodCulture.getText().toString());
        params.put("lunch_menu", txtLunchMenu.getText().toString());
        params.put("dinner_menu", txtDinnerMenu.getText().toString());
        params.put("special_menu", txtSpecialMenu.getText().toString());

        params.put("address", txtPlace.getText().toString());
        params.put("latitude", strLat);
        params.put("longitude", strLong);

        params.put("city", strCity); // added

        params.put("lunch_hours", edtHours.getText().toString()); // added
        params.put("hours", edtHours.getText().toString());
        params.put("lunch_deadline", edtDeadline.getText().toString()); // added
        params.put("deadline", edtDeadline.getText().toString());
        params.put("services", getServices());
        params.put("veg_nonveg", getVegNonVeg());

        if (switchButtonDiscovery.isChecked())
            params.put("discovery", "1");
        else
            params.put("discovery", "0");

        params.put("id", currentChef.getId());

        if (isPhotoSet) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            params.put("profile_pic", encoded);
        }

        aq.ajax(url, params, JSONObject.class, updateCallBack);
    }

    private String getServices() {

        String services = "";

        if (cbDinein.isChecked()) {
            if (services.equalsIgnoreCase(""))
                services = "dine_in";
            else
                services = services + ",dine_in";
        }

        if (cbTakeout.isChecked()) {
            if (services.equalsIgnoreCase(""))
                services = "take_out";
            else
                services = services + ",take_out";
        }

        if (cbDelivery.isChecked()) {
            if (services.equalsIgnoreCase(""))
                services = "delivery";
            else
                services = services + ",delivery";
        }

        return services;
    }

    private String getVegNonVeg() {

        if (rgVegNonVeg.getCheckedRadioButtonId() == R.id.rdBoth)
            return "both";
        else if (rgVegNonVeg.getCheckedRadioButtonId() == R.id.rdVeg)
            return "veg";
        else if (rgVegNonVeg.getCheckedRadioButtonId() == R.id.rdNonVeg)
            return "nonveg";

        return null;
    }

    private boolean validated() {

        if (!isPhotoSet && currentChef.getProfile_pic() == null) { //&& currentChef.getProfile_pic().isEmpty()
            profile_image.requestFocus();
            YoYo.with(Techniques.Tada).duration(700).playOn(profile_image);
            Toast.makeText(ChefProfileActivity.this, "Please set Profile Picture", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Required");
            edtName.requestFocus();
            return false;
        } else if (edtPhoneNumber.getText().toString().isEmpty()) {
            edtPhoneNumber.setError("Required");
            edtPhoneNumber.requestFocus();
            return false;
        } else if (!checkAnyCheckBoxChekedorNot(cbServicesLayout)) {
            Toast.makeText(ChefProfileActivity.this, "Please check at least one Service Type", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtLunchHours.getText().toString().isEmpty()) {
            edtLunchHours.setError("Required");
            edtLunchHours.requestFocus();
            return false;
        } else if (edtHours.getText().toString().isEmpty()) {
            edtHours.setError("Required");
            edtHours.requestFocus();
            return false;
        } else if (txtFoodCulture.getText().toString().equalsIgnoreCase("Tap to select...")) {
            Toast.makeText(ChefProfileActivity.this, "Please select Food Culture", Toast.LENGTH_SHORT).show();
            return false;
        } else if (rgVegNonVeg.getCheckedRadioButtonId() == -1) {
            Toast.makeText(ChefProfileActivity.this, "Please select type of Veg or Non-Veg", Toast.LENGTH_SHORT).show();
            return false;
        } else if (txtLunchMenu.getText().toString().isEmpty() || txtLunchMenu.getText().toString().equalsIgnoreCase("Tap to edit...")) {
            txtLunchMenu.setError("Required");
            txtLunchMenu.requestFocus();
            return false;
        } else if (txtDinnerMenu.getText().toString().isEmpty() || txtLunchMenu.getText().toString().equalsIgnoreCase("Tap to edit...")) {
            txtDinnerMenu.setError("Required");
            txtDinnerMenu.requestFocus();
            return false;
        } else if (txtSpecialMenu.getText().toString().isEmpty() || txtLunchMenu.getText().toString().equalsIgnoreCase("Tap to edit...")) {
            txtSpecialMenu.setError("Required");
            txtSpecialMenu.requestFocus();
            return false;
        } else if (edtLunchDeadline.getText().toString().isEmpty()) {
            edtLunchDeadline.setError("Required");
            edtLunchDeadline.requestFocus();
            return false;
        } else if (edtDeadline.getText().toString().isEmpty()) {
            edtDeadline.setError("Required");
            edtDeadline.requestFocus();
            return false;
        } else if (strLat == null || strLong == null || strLat.equalsIgnoreCase("") || strLong.equalsIgnoreCase("")) {
            Toast.makeText(ChefProfileActivity.this, "Please pick your place", Toast.LENGTH_SHORT).show();
            return false;
        } else if (edtSpeciality.getText().toString().isEmpty()) {
            edtSpeciality.setError("Required");
            edtSpeciality.requestFocus();
            return false;
        }
        return true;
    }

    private void initViews() {

        aq = new AQuery(this);
        tdb = new TinyDB(this);

        btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtPhoneNumber = (MaterialEditText) findViewById(R.id.edtPhoneNumber);
        edtSpeciality = (MaterialEditText) findViewById(R.id.edtSpeciality);
        txtFoodCulture = (TextView) findViewById(R.id.txtFoodCulture);
        txtLunchMenu = (TextView) findViewById(R.id.edtLunchMenu);
        txtDinnerMenu = (TextView) findViewById(R.id.edtDinnerMenu);
        txtSpecialMenu = (TextView) findViewById(R.id.edtSpecialMenu);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        cbServicesLayout = (LinearLayout) findViewById(R.id.cbServicesLayout);
        btnPickPlace = (Button) findViewById(R.id.btnPickPlace);
        txtPlace = (TextView) findViewById(R.id.txtPlace);
        rgVegNonVeg = (RadioGroup) findViewById(R.id.rgVegNonVeg);
        switchButtonDiscovery = (Switch) findViewById(R.id.switchButtonDiscovery);
        edtDeadline = (MaterialEditText) findViewById(R.id.edtDeadline);
        edtHours = (MaterialEditText) findViewById(R.id.edtHours);
        edtLunchHours = (MaterialEditText) findViewById(R.id.edtLunchHours);
        edtLunchDeadline = (MaterialEditText) findViewById(R.id.edtLunchDeadline);
        cbDelivery = (CheckBox) findViewById(R.id.cbDelivery);
        cbDinein = (CheckBox) findViewById(R.id.cbDinein);
        cbTakeout = (CheckBox) findViewById(R.id.cbTakeout);
        rdBoth = (RadioButton) findViewById(R.id.rdBoth);
        rdVeg = (RadioButton) findViewById(R.id.rdVeg);
        rdNonVeg = (RadioButton) findViewById(R.id.rdNonVeg);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        btnPickPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    Intent intent = intentBuilder.build(ChefProfileActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException
                        | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        txtFoodCulture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMultiSelectDialog();
            }
        });

        currentChef = (Chef) tdb.getObject("logged_chef", Chef.class);

        if (currentChef == null)
            return;

        edtName.setText(currentChef.getName());
        edtPhoneNumber.setText(currentChef.getPhone());
        edtSpeciality.setText(currentChef.getSpecialty());
        edtDeadline.setText(currentChef.getDeadline());
        edtHours.setText(currentChef.getHours());

        if (currentChef.getFood_culture() != null && !currentChef.getFood_culture().equalsIgnoreCase(""))
            txtFoodCulture.setText(currentChef.getFood_culture());

        txtLunchMenu.setText(currentChef.getLunch_menu());
        txtDinnerMenu.setText(currentChef.getDinner_menu());
        txtSpecialMenu.setText(currentChef.getSpecial_menu());

        if (currentChef.getServices() != null && currentChef.getServices().contains("dine_in"))
            cbDinein.setChecked(true);
        else
            cbDinein.setChecked(false);

        if (currentChef.getServices() != null && currentChef.getServices().contains("delivery"))
            cbDelivery.setChecked(true);
        else
            cbDelivery.setChecked(false);

        if (currentChef.getServices() != null && currentChef.getServices().contains("take_out"))
            cbTakeout.setChecked(true);
        else
            cbTakeout.setChecked(false);

        if (currentChef.getVeg_nonveg() != null) {
            if (currentChef.getVeg_nonveg().equalsIgnoreCase("veg"))
                rdVeg.setChecked(true);
            else if (currentChef.getVeg_nonveg().equalsIgnoreCase("nonveg"))
                rdNonVeg.setChecked(true);
            else if (currentChef.getVeg_nonveg().equalsIgnoreCase("both"))
                rdBoth.setChecked(true);
        }


        if (currentChef.getDiscovery() != null && currentChef.getDiscovery().equalsIgnoreCase("1"))
            switchButtonDiscovery.setChecked(true);
        else
            switchButtonDiscovery.setChecked(false);

        if (currentChef.getProfile_pic() != null && !currentChef.getProfile_pic().isEmpty()) {
            String pro_url = currentChef.getProfile_pic();
            pro_url = pro_url.replace("..", "http://restora.ninepixel.in");
            aq.id(profile_image).image(pro_url);
        }

        strLat = currentChef.getLatitude();
        strLong = currentChef.getLongitude();
//        strCity = currentChef.getLongitude();

        if (currentChef.getAddress() != null && !currentChef.getAddress().equalsIgnoreCase(""))
            txtPlace.setText(currentChef.getAddress());

        if (currentChef.getFood_culture() != null)
            for (int i = 0; i < items.length; i++) {
                if (currentChef.getFood_culture().contains(items[i])) {
                    checkedItems[i] = true;
                    seletedItems.add(i);
                } else {
                    checkedItems[i] = false;
                }
            }


        txtDinnerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuActivity();
            }
        });

        txtLunchMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuActivity();
            }
        });

        txtSpecialMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuActivity();
            }
        });

    }

    private void openMenuActivity() {
        Intent i = new Intent(ChefProfileActivity.this, UpdateMenuActivity.class);
        i.putExtra("from_profile", true);
        i.putExtra("lunch", txtLunchMenu.getText().toString());
        i.putExtra("dinner", txtDinnerMenu.getText().toString());
        i.putExtra("special", txtSpecialMenu.getText().toString());
        startActivityForResult(i, 108);
    }

    private void dispatchTakePictureIntent() {
        pickerDialog = new MaterialDialog.Builder(ChefProfileActivity.this)
                .title("Profile Picture")
                .content("Open camera or gallery to choose profile picture for your account")
                .positiveText("Gallery")
                .negativeText("Camera")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                        materialDialog.dismiss();
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, 99);
                        }

                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {

                        materialDialog.dismiss();

                        Intent takePictureIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                        }
                        if (photoFile != null) {
                            takePictureIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile));
                            startActivityForResult(
                                    takePictureIntent,
                                    RESULT_LOAD_IMG);
                        }
                    }
                })
                .show();
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName,
                ".jpg",
                storageDir
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("DEBUG", "mCurrentPhotoPath " + mCurrentPhotoPath);
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 15: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Toast.makeText(ChefProfileActivity.this, "Please grant permission", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 16: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Toast.makeText(ChefProfileActivity.this, "Please grant permission for CAMERA", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 17: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                } else {
                    Toast.makeText(ChefProfileActivity.this, "Please grant permission for WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void checkPermission() {

        int permissionCheckWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        int permissionCheckCamera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);

        if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED && permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    15);
            return;
        } else if (permissionCheckWrite != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    16);
            return;
        } else if (permissionCheckCamera != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    17);
            return;
        } else {
            dispatchTakePictureIntent();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (resultCode == RESULT_OK) {
                if (requestCode == RESULT_LOAD_IMG) {
                    if (mCurrentPhotoPath != null) {
                        Log.d("DEBUG", "mCurrentPhotoPath = "
                                + mCurrentPhotoPath);
                        Intent i = new Intent(ChefProfileActivity.this,
                                Image_Crop_Activity.class);
                        i.putExtra("image_path", mCurrentPhotoPath);
                        startActivity(i);
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                                .show();
                    }
                } else if (requestCode == 99) {
                    final Uri imageUri = data.getData();
                    Log.d("DEBUG", "mCurrentPhotoPath = "
                            + imageUri);

                    final InputStream imageStream;
                    try {
                        imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        OutputStream fOut = new FileOutputStream(mCurrentPhotoPath);
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                        fOut.flush();
                        fOut.close();

                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    Intent i = new Intent(ChefProfileActivity.this,
                            Image_Crop_Activity.class);
                    i.putExtra("image_path", mCurrentPhotoPath);
                    startActivity(i);
                } else if (requestCode == PLACE_PICKER_REQUEST) {

                    final Place place = PlacePicker.getPlace(this, data);
                    final CharSequence name = place.getName();
                    final CharSequence address = place.getAddress();
                    String attributions = (String) place.getAttributions();
                    if (attributions == null) {
                        attributions = "";
                    }

                    strLat = place.getLatLng().latitude + "";
                    strLong = place.getLatLng().longitude + "";

                    Geocoder geocoder = new Geocoder(this);
                    try {
                        List<Address> addresses = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
                        strCity = addresses.get(0).getAddressLine(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        strCity = "-";
                    }

                    Log.d("DEBUG", "PLACE:" + name + " , " + address);
                    txtPlace.setText(name + " , " + address);

                } else if (requestCode == 108) {
                    txtLunchMenu.setText(data.getStringExtra("lunch"));
                    txtDinnerMenu.setText(data.getStringExtra("dinner"));
                    txtSpecialMenu.setText(data.getStringExtra("special"));
                }
            } else {

//                if (requestCode == PLACE_PICKER_REQUEST) {
////                    Toast.makeText(this, "You haven't picked Place",
////                            Toast.LENGTH_LONG).show();
//                } else {
////                    Toast.makeText(this, "You haven't picked Image",
////                            Toast.LENGTH_LONG).show();
//                }

            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPhotoSet)
            getImageFile();
    }

    private boolean getImageFile() {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/profile");
        if (direct.exists()) {
            profileFile = new File(new File(Environment
                    .getExternalStorageDirectory().getPath() + "/profile/"),
                    "profile.jpeg");
            if (profileFile.exists()) {
                currentBitmap = BitmapFactory.decodeFile(profileFile.getAbsolutePath());
                profile_image.setImageBitmap(currentBitmap);
                return true;
            }
        }
        return false;
    }

    boolean checkAnyCheckBoxChekedorNot(LinearLayout cbLayout) {

        if (cbDinein.isChecked())
            return true;

        if (cbTakeout.isChecked())
            return true;

        if (cbDelivery.isChecked())
            return true;

//        for (int i = 0; i < cbLayout.getChildCount(); i++) {
//
//            CheckBox temp = (CheckBox) cbLayout.getChildAt(i);
//            if (temp.isChecked()) ;
//            {
//                isCheked = true;
//                break;
//            }
//
//        }

        return false;
    }

    void showMultiSelectDialog() {

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select The Food Culture ")
                .setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            seletedItems.add(indexSelected);
                        } else if (seletedItems.contains(indexSelected)) {
                            seletedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < seletedItems.size(); i++) {
                            sb.append(items[(int) seletedItems.get(i)]);
                            sb.append(",");
                        }

                        sb.deleteCharAt(sb.toString().lastIndexOf(","));
                        String foodCulture = sb.toString();

                        for (int i = 0; i < items.length; i++) {

                            if (foodCulture.contains(items[i])) {
                                checkedItems[i] = true;
                            } else {
                                checkedItems[i] = false;
                            }

                        }

                        txtFoodCulture.setText(foodCulture);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //  Your code when user clicked on Cancel
                    }
                }).create();
        dialog.show();
    }

}