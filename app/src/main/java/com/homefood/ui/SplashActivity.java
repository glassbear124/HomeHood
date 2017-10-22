package com.homefood.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.homefood.R;
import com.homefood.util.TinyDB;

public class SplashActivity extends AppCompatActivity {

    TinyDB tDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        tDB = new TinyDB(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent;
                if (tDB.getInt("uid") != 0) {
                    mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                } else {
                    mainIntent = new Intent(SplashActivity.this, HomeActivity.class);
                }
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, 3000);

    }
}
