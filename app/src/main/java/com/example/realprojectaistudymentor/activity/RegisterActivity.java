package com.example.realprojectaistudymentor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realprojectaistudymentor.MainActivity;
import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.database.entity.UserEntity;
import com.example.realprojectaistudymentor.repository.LeaderboardRepository;
import com.example.realprojectaistudymentor.repository.UserRepository;
import com.example.realprojectaistudymentor.utils.Helper;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    // Step 1 — Register views
    private LinearLayout layoutStep1, layoutStep2;
    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private TextView tvPasswordStrength;
    private Button btnNext;

    // Step 2 — Onboarding views (first use preferences)
    private Spinner spinnerEducationLevel;
    private CheckBox cbMath, cbScience, cbProgramming, cbHistory, cbLanguages;
    private RadioGroup rgExplanationStyle;
    private Button btnFinish;

    private UserRepository userRepo;
    private SessionManager sessionManager;
    private long newUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRepo       = new UserRepository(this);
        sessionManager = new SessionManager(this);

        // Step 1
        layoutStep1       = findViewById(R.id.layout_step1);
        layoutStep2       = findViewById(R.id.layout_step2);
        etFullName        = findViewById(R.id.et_full_name);
        etEmail           = findViewById(R.id.et_email);
        etPassword        = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tvPasswordStrength = findViewById(R.id.tv_password_strength);
        btnNext           = findViewById(R.id.btn_next);

        // Step 2
        spinnerEducationLevel = findViewById(R.id.spinner_education_level);
        cbMath        = findViewById(R.id.cb_math);
        cbScience     = findViewById(R.id.cb_science);
        cbProgramming = findViewById(R.id.cb_programming);
        cbHistory     = findViewById(R.id.cb_history);
        cbLanguages   = findViewById(R.id.cb_languages);
        rgExplanationStyle = findViewById(R.id.rg_explanation_style);
        btnFinish     = findViewById(R.id.btn_finish);

        // Spinner education level
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Middle School", "High School", "University"});
        spinnerEducationLevel.setAdapter(adapter);

        // Password strength realtime
        etPassword.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String strength = Helper.getPasswordStrength(s.toString());
                tvPasswordStrength.setText("Strength: " + strength);
                tvPasswordStrength.setTextColor(
                        strength.equals("Strong") ? getColor(R.color.success) :
                                strength.equals("Medium") ? getColor(R.color.warning) :
                                        getColor(R.color.error));
            }
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Bấm Next → validate Step 1
        btnNext.setOnClickListener(v -> validateAndGoStep2());

        // Bấm Finish → lưu preferences → vào app
        btnFinish.setOnClickListener(v -> savePreferencesAndFinish());
    }

    private void validateAndGoStep2() {
        String name     = etFullName.getText().toString().trim();
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirm  = etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) { etFullName.setError("Required"); return; }
        if (!Helper.isValidEmail(email)) { etEmail.setError("Invalid email"); return; }
        if (!Helper.isValidPassword(password)) {
            etPassword.setError("Min 8 chars, 1 uppercase, 1 number"); return;
        }
        if (!password.equals(confirm)) { etConfirmPassword.setError("Passwords do not match"); return; }

        // Đăng ký vào DB
        newUserId = userRepo.register(email, password, name);
        if (newUserId == -1) {
            etEmail.setError("Email already registered");
            return;
        }

        // Chuyển sang Step 2 — onboarding
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.VISIBLE);
    }

    private void savePreferencesAndFinish() {
        // Education level
        String level = spinnerEducationLevel.getSelectedItem().toString()
                .toLowerCase().replace(" ", "_");

        // Subjects
        List<String> subjects = new ArrayList<>();
        if (cbMath.isChecked())        subjects.add("Math");
        if (cbScience.isChecked())     subjects.add("Science");
        if (cbProgramming.isChecked()) subjects.add("Programming");
        if (cbHistory.isChecked())     subjects.add("History");
        if (cbLanguages.isChecked())   subjects.add("Languages");
        if (subjects.isEmpty()) { Toast.makeText(this, "Select at least 1 subject", Toast.LENGTH_SHORT).show(); return; }

        // Explanation style
        int selectedId = rgExplanationStyle.getCheckedRadioButtonId();
        if (selectedId == -1) { Toast.makeText(this, "Select explanation style", Toast.LENGTH_SHORT).show(); return; }
        String style = ((RadioButton) findViewById(selectedId)).getTag().toString();

        // Lưu preferences
        userRepo.savePreferences((int) newUserId, level, subjects, style);

        // Lưu session
        UserEntity user = userRepo.getById((int) newUserId);
        sessionManager.saveLoginSession(user.id, user.email, user.fullName);

        // Sync user mới lên Firebase Leaderboard
        LeaderboardRepository leaderboardRepo = new LeaderboardRepository();
        leaderboardRepo.syncUser(user.email, user.fullName, user.xpPoints, user.level);

        // Vào app
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
