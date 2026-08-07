package com.example.realprojectaistudymentor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.repository.UserRepository;
import com.example.realprojectaistudymentor.utils.Helper;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPassword, etConfirmPassword;
    private TextView tvPasswordStrength;
    private Button btnRegister;

    private UserRepository userRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userRepo = new UserRepository(this);

        etFullName         = findViewById(R.id.et_full_name);
        etEmail            = findViewById(R.id.et_email);
        etPassword         = findViewById(R.id.et_password);
        etConfirmPassword  = findViewById(R.id.et_confirm_password);
        tvPasswordStrength = findViewById(R.id.tv_password_strength);
        btnRegister        = findViewById(R.id.btn_next); // Reuse btn_next as Register button

        btnRegister.setText("Register Account");

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

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
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

        long newUserId = userRepo.register(email, password, name);
        if (newUserId == -1) {
            etEmail.setError("Email already registered");
            return;
        }

        Toast.makeText(this, "Registration Successful! Please login.", Toast.LENGTH_LONG).show();

        // Quay lại màn hình đăng nhập
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}