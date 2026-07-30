package com.example.realprojectaistudymentor.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

        // Kiểm tra 2FA (optional theo tài liệu)
        if (user.isTwoFactorEnabled) {
            // TODO: gửi OTP và mở màn hình xác thực 2FA
            Toast.makeText(this, "2FA: OTP sent to " + email, Toast.LENGTH_SHORT).show();
        }

        // Lưu session
        sessionManager.saveLoginSession(user.id, user.email, user.fullName);

        // Sync user lên Firebase Leaderboard
        LeaderboardRepository leaderboardRepo = new LeaderboardRepository();
        int level = Math.max(1, user.xpPoints / 100 + 1);
        leaderboardRepo.syncUser(user.email, user.fullName, user.xpPoints, level);

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
