package com.example.realprojectaistudymentor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import com.example.realprojectaistudymentor.MainActivity;
import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.database.entity.UserEntity;
import com.example.realprojectaistudymentor.repository.LeaderboardRepository;
import com.example.realprojectaistudymentor.repository.UserRepository;
import com.example.realprojectaistudymentor.utils.Helper;
import com.example.realprojectaistudymentor.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoRegister;

    private UserRepository userRepo;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRepo       = new UserRepository(this);
        sessionManager = new SessionManager(this);

        etEmail      = findViewById(R.id.et_email);
        etPassword   = findViewById(R.id.et_password);
        btnLogin     = findViewById(R.id.btn_login);
        tvGoRegister = findViewById(R.id.tv_go_register);

        btnLogin.setOnClickListener(v -> login());

        tvGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void login() {
        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!Helper.isValidEmail(email)) { etEmail.setError("Invalid email"); return; }
        if (password.isEmpty()) { etPassword.setError("Required"); return; }

        UserEntity user = userRepo.login(email, password);

        if (user == null) {
            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xin quyền thông báo cho Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // Kiểm tra 2FA
        if (user.isTwoFactorEnabled) {
            String otp = Helper.generateOTP();
            Helper.sendOTPNotification(this, otp);
            // Hiện thêm Toast dự phòng để dễ demo
            Toast.makeText(this, "Debug OTP: " + otp, Toast.LENGTH_LONG).show();
            showOTPDialog(user, otp);
        } else {
            proceedToMain(user);
        }
    }

    private void showOTPDialog(UserEntity user, String correctOtp) {
        EditText etOtp = new EditText(this);
        etOtp.setHint("Enter 6-digit OTP");
        etOtp.setPadding(60, 40, 60, 40);
        etOtp.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        etOtp.setText(correctOtp); // Tự động điền mã để demo dễ dàng hơn

        new AlertDialog.Builder(this)
                .setTitle("2FA Verification")
                .setMessage("A verification code has been sent to your device.\n\nFor demo purposes, your code is: " + correctOtp)
                .setView(etOtp)
                .setCancelable(false)
                .setPositiveButton("Verify", (dialog, which) -> {
                    String input = etOtp.getText().toString().trim();
                    if (input.equals(correctOtp)) {
                        proceedToMain(user);
                    } else {
                        Toast.makeText(this, "Invalid OTP. Access Denied.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void proceedToMain(UserEntity user) {
        // Lưu session
        sessionManager.saveLoginSession(user.id, user.email, user.fullName);

        // Sync user lên Firebase Leaderboard
        LeaderboardRepository leaderboardRepo = new LeaderboardRepository();
        int level = Math.max(1, user.xpPoints / 100 + 1);
        leaderboardRepo.syncUser(user.email, user.fullName, user.xpPoints, level);

        // Kiểm tra xem đã chọn sở thích chưa
        if (user.educationLevel == null || user.educationLevel.isEmpty()) {
            startActivity(new Intent(this, OnboardingActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
