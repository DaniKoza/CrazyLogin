package com.danikoza.crazylogin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.danikoza.floatingviewslib.FloatingViews;

public class LoginSuccessActivity extends AppCompatActivity {

    FloatingViews myFloatingViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);

        myFloatingViews = findViewById(R.id.floating_locks);
        myFloatingViews.init(R.drawable.ic_shield);

    }

    @Override
    protected void onPause() {
        super.onPause();
        myFloatingViews.pause();
    }
}