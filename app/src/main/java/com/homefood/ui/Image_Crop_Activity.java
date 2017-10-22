package com.homefood.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.edmodo.cropper.CropImageView;
import com.homefood.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Image_Crop_Activity extends AppCompatActivity {

    Button btnSet;
    CropImageView myImage;
    private Toolbar toolbar;
    ImageView imgDone;
    private TextView txtTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_crop_screen);

        imgDone = (ImageView) findViewById(R.id.imgDone);

        txtTitle = (TextView) findViewById(R.id.txtTitle);

        String path = getIntent().getStringExtra("image_path");

        myImage = (CropImageView) findViewById(R.id.CropImageView);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File imgFile = new File(path);

        if (imgFile.exists()) {

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
                    .getAbsolutePath());

            myImage.setImageBitmap(myBitmap);
            myImage.setAspectRatio(10, 10);
            myImage.setFixedAspectRatio(true);
            myImage.setGuidelines(1);

        }

        imgDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new GetProfilePic().execute("");
            }
        });


    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory()
                + "/profile");

        if (!direct.exists()) {
            File myDir = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/profile/");
            myDir.mkdirs();
        }

        File file = new File(new File(Environment.getExternalStorageDirectory()
                .getPath() + "/profile/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave = scaleDown(imageToSave, 300, false);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    private class GetProfilePic extends AsyncTask<String, Void, String> {

        Bitmap croppedImage;
        @SuppressWarnings("unused")
        byte[] data;

        @Override
        protected String doInBackground(String... params) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            croppedImage = Bitmap.createScaledBitmap(croppedImage, 500, 500,
                    false);
            croppedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            data = stream.toByteArray();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            createDirectoryAndSaveFile(croppedImage, "profile.jpeg");
            Log.d("DEBUG", "image cropped success");
            ChefProfileActivity.isPhotoSet = true;
            Image_Crop_Activity.this.finish();
        }

        @Override
        protected void onPreExecute() {
            croppedImage = myImage.getCroppedImage();
        }

    }

}
