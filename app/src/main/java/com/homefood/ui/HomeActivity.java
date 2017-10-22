package com.homefood.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.homefood.R;

public class HomeActivity extends AppCompatActivity {

    Button btnCustSignUp, btnCustLogIn, btnChefSignUp, btnChefLogIn;
    Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnChefLogIn = (Button) findViewById(R.id.btnChefLogIn);
        btnChefSignUp = (Button) findViewById(R.id.btnChefSignUp);
        btnCustLogIn = (Button) findViewById(R.id.btnCustLogIn);
        btnCustSignUp = (Button) findViewById(R.id.btnCustSignUp);

        btnChefLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(HomeActivity.this, LoginActivity.class);
                mIntent.putExtra("isChef", true);
                startActivity(mIntent);
                HomeActivity.this.finish();
            }
        });

        btnChefSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(HomeActivity.this, ChefRegisterActivity.class);
                startActivity(mIntent);
                HomeActivity.this.finish();
            }
        });

        btnCustLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(HomeActivity.this, LoginActivity.class);
                mIntent.putExtra("isChef", false);
                startActivity(mIntent);
                HomeActivity.this.finish();
            }
        });

        btnCustSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent = new Intent(HomeActivity.this, RegisterActivity.class);
                mIntent.putExtra("isUpdate", false);
                startActivity(mIntent);
                HomeActivity.this.finish();
            }
        });

    }
}
