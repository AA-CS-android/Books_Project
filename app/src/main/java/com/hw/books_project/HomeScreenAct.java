package com.hw.books_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class HomeScreenAct extends AppCompatActivity {

    private Button btnLogout;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        init();
    }

    private void init() {
        btnLogout = findViewById(R.id.btnLogout);
        intent = new Intent(HomeScreenAct.this, MainActivity.class);
        btnLogout.setOnClickListener(this::logout);
    }

    public void logout(View view) {
        FBRef.refAuth.signOut();

        SharedPreferences sharedPref = getSharedPreferences("user_details", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        startActivity(intent);
        finish();
    }
}
