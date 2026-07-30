package com.example.realprojectaistudymentor.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.realprojectaistudymentor.R;
import com.example.realprojectaistudymentor.activity.LoginActivity;
import com.example.realprojectaistudymentor.database.entity.UserEntity;
import com.example.realprojectaistudymentor.repository.UserRepository;
import com.example.realprojectaistudymentor.utils.SessionManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

    private static final String AVATAR_FILE = "avatar.jpg";

    private ImageView ivAvatar;
    private TextView tvName, tvEmail, tvLevel, tvEducationLevel,
            tvSubjects, tvExplanationStyle, tvXp;
    private Switch switch2FA;
    private Switch switchDarkMode;
    private Button btnLogout;

    private static final String PREF_DARK_MODE = "dark_mode";

    private UserRepository userRepo;
    private SessionManager sessionManager;

    // Gallery picker
    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    saveAvatar(uri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivAvatar           = view.findViewById(R.id.iv_avatar);
        tvName             = view.findViewById(R.id.tv_name);
        tvEmail            = view.findViewById(R.id.tv_email);
        tvLevel            = view.findViewById(R.id.tv_level);
        tvXp               = view.findViewById(R.id.tv_xp);
        tvEducationLevel   = view.findViewById(R.id.tv_education_level);
        tvSubjects         = view.findViewById(R.id.tv_subjects);
        tvExplanationStyle = view.findViewById(R.id.tv_explanation_style);
        switch2FA          = view.findViewById(R.id.switch_2fa);
        switchDarkMode     = view.findViewById(R.id.switch_dark_mode);
        btnLogout          = view.findViewById(R.id.btn_logout);

        userRepo       = new UserRepository(requireContext());
        sessionManager = new SessionManager(requireContext());

        loadProfile();
        loadAvatar();

        // Click avatar hoặc camera icon → mở gallery
        View.OnClickListener pickImage = v -> galleryLauncher.launch("image/*");
        ivAvatar.setOnClickListener(pickImage);
        view.findViewById(R.id.iv_camera_overlay).setOnClickListener(pickImage);

        // Toggle 2FA
        switch2FA.setOnCheckedChangeListener((btn, isChecked) -> {
            Toast.makeText(requireContext(),
                    isChecked ? "2FA enabled" : "2FA disabled",
                    Toast.LENGTH_SHORT).show();
        });

        // Toggle Dark Mode
        SharedPreferences prefs = requireContext().getSharedPreferences("AIStudyMentorPrefs", 0);
        boolean isDarkMode = prefs.getBoolean(PREF_DARK_MODE, false);
        switchDarkMode.setChecked(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean(PREF_DARK_MODE, isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Đăng xuất
        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            startActivity(new Intent(requireContext(), LoginActivity.class));
            requireActivity().finish();
        });
    }

    private void loadProfile() {
        int userId = sessionManager.getUserId();
        UserEntity user = userRepo.getById(userId);

        if (user == null) {
            // Fallback — dùng data từ session
            tvName.setText(sessionManager.getUserName());
            tvEmail.setText(sessionManager.getUserEmail());
            tvLevel.setText("Level 1");
            tvXp.setText("0 XP");
            tvEducationLevel.setText("Not set");
            tvSubjects.setText("Not set");
            tvExplanationStyle.setText("Not set");
            return;
        }

        tvName.setText(user.fullName);
        tvEmail.setText(user.email);
        tvLevel.setText("Level " + user.level);
        tvXp.setText(user.xpPoints + " XP");
        tvEducationLevel.setText(user.educationLevel != null ? user.educationLevel : "Not set");
        tvSubjects.setText(user.preferredSubjects != null ? user.preferredSubjects.replace(",", ", ") : "Not set");
        tvExplanationStyle.setText(user.explanationStyle != null ? user.explanationStyle : "Not set");
        switch2FA.setChecked(user.isTwoFactorEnabled);
    }

    /**
     * Load avatar từ internal storage.
     */
    private void loadAvatar() {
        File file = new File(requireContext().getFilesDir(), AVATAR_FILE);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivAvatar.setImageBitmap(bitmap);
        }
        // Nếu chưa có avatar → giữ nguyên icon mặc định (ic_avatar_default)
    }

    /**
     * Lưu ảnh từ URI vào internal storage + cập nhật DB.
     */
    private void saveAvatar(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();

            // Resize nếu ảnh quá lớn (giữ square, max 500px)
            bitmap = cropToSquare(bitmap);
            bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);

            // Ghi vào internal storage
            File file = new File(requireContext().getFilesDir(), AVATAR_FILE);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            // Hiển thị ngay
            ivAvatar.setImageBitmap(bitmap);

            // Lưu đường dẫn vào DB
            userRepo.saveAvatar(sessionManager.getUserId(), file.getAbsolutePath());

            Toast.makeText(requireContext(), "Avatar updated!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to save avatar: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Cắt ảnh thành hình vuông (center crop).
     */
    private Bitmap cropToSquare(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int size = Math.min(w, h);
        int x = (w - size) / 2;
        int y = (h - size) / 2;
        return Bitmap.createBitmap(bitmap, x, y, size, size);
    }
}
