package com.homefood.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.homefood.R;

public class ChefMainActivity extends AppCompatActivity {

    LinearLayout btnProfile, btnUpdateMenu, btnChangePassword;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_main);

        btnProfile = (LinearLayout) findViewById(R.id.btnProfile);
        btnChangePassword = (LinearLayout) findViewById(R.id.btnChangePassword);
        btnUpdateMenu = (LinearLayout) findViewById(R.id.btnUpdateMenu);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChefMainActivity.this, ChefProfileActivity.class);
                i.putExtra("from_register", false);
                startActivity(i);
            }
        });

        btnUpdateMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChefMainActivity.this, UpdateMenuActivity.class));
            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChefMainActivity.this, ChangePasswordActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChefMainActivity.this, LoginActivity.class);
                i.putExtra("isChef", true);
                startActivity(i);
                ChefMainActivity.this.finish();
            }
        });
    }
}
