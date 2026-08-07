package com.example.realprojectaistudymentor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realprojectaistudymentor.MainActivity;
import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.database.entity.UserEntity;
import com.example.realprojectaistudymentor.repository.LeaderboardRepository;
import com.example.realprojectaistudymentor.repository.UserRepository;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private Spinner spinnerEducationLevel;
    private CheckBox cbMath, cbScience, cbProgramming, cbHistory, cbLanguages;
    private RadioGroup rgExplanationStyle;
    private Button btnFinish;

    private UserRepository userRepo;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        userRepo       = new UserRepository(this);
        sessionManager = new SessionManager(this);

        spinnerEducationLevel = findViewById(R.id.spinner_education_level);
        cbMath        = findViewById(R.id.cb_math);
        cbScience     = findViewById(R.id.cb_science);
        cbProgramming = findViewById(R.id.cb_programming);
        cbHistory     = findViewById(R.id.cb_history);
        cbLanguages   = findViewById(R.id.cb_languages);
        rgExplanationStyle = findViewById(R.id.rg_explanation_style);
        btnFinish     = findViewById(R.id.btn_finish);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Middle School", "High School", "University"});
        spinnerEducationLevel.setAdapter(adapter);

        btnFinish.setOnClickListener(v -> savePreferencesAndFinish());
    }

    private void savePreferencesAndFinish() {
        int userId = sessionManager.getUserId();

        String level = spinnerEducationLevel.getSelectedItem().toString()
                .toLowerCase().replace(" ", "_");

        List<String> subjects = new ArrayList<>();
        if (cbMath.isChecked())        subjects.add("Math");
        if (cbScience.isChecked())     subjects.add("Science");
        if (cbProgramming.isChecked()) subjects.add("Programming");
        if (cbHistory.isChecked())     subjects.add("History");
        if (cbLanguages.isChecked())   subjects.add("Languages");

        if (subjects.isEmpty()) {
            Toast.makeText(this, "Select at least 1 subject", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgExplanationStyle.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select explanation style", Toast.LENGTH_SHORT).show();
            return;
        }
        String style = ((RadioButton) findViewById(selectedId)).getTag().toString();

        // Lưu vào DB
        userRepo.savePreferences(userId, level, subjects, style);

        // Sync lên Firebase
        UserEntity user = userRepo.getById(userId);
        LeaderboardRepository leaderboardRepo = new LeaderboardRepository();
        leaderboardRepo.syncUser(user.email, user.fullName, user.xpPoints, user.level);

        Toast.makeText(this, "Setup complete! Welcome.", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}