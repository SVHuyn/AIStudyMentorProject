package com.example.realprojectaistudymentor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.realprojectaistudymentor.activity.LoginActivity;
import com.example.realprojectaistudymentor.fragment.AskQuestionFragment;
import com.example.realprojectaistudymentor.fragment.HistoryFragment;
import com.example.realprojectaistudymentor.fragment.HomeFragment;
import com.example.realprojectaistudymentor.fragment.ProfileFragment;
import com.example.realprojectaistudymentor.fragment.QuizFragment;
import com.example.realprojectaistudymentor.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // Nếu chưa đăng nhập thì về LoginActivity
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bottomNav = findViewById(R.id.bottom_navigation);

        // Load fragment mặc định
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_ask) {
                fragment = new AskQuestionFragment();
            } else if (id == R.id.nav_quiz) {
                fragment = new QuizFragment();
            } else if (id == R.id.nav_history) {
                fragment = new HistoryFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
