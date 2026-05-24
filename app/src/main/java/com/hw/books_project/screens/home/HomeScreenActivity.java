package com.hw.books_project.screens.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hw.books_project.R;
import com.hw.books_project.screens.auth.AuthActivity;
import com.hw.books_project.utils.FBRef;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeScreenActivity extends AppCompatActivity {

    private Button btnLogout;
    private Intent intent;
    private BottomNavigationView bottomNavigationView;
    Fragment books, libraries, ai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_screen);
        init();
    }

    private void init() {
        btnLogout = findViewById(R.id.btnLogout);
        intent = new Intent(HomeScreenActivity.this, AuthActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        btnLogout.setOnClickListener(this::logout);
        bottomNavigationView = findViewById(R.id.bottom_nev_bar);

        books = new BooksFragment();
        libraries = new LibrariesFragment();
        ai = new AiRecommendFragment();

        setCurrentFragment(libraries);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.books) {
                setCurrentFragment(books);
            } else if (itemId == R.id.libraries) {
                setCurrentFragment(libraries);
            } else if (itemId == R.id.nav_ai) {
                setCurrentFragment(ai);
            }
            return true;
        });


    }
    private void setCurrentFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
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
