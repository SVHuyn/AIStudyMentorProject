package com.example.realprojectaistudymentor.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Patterns;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.realprojectaistudymentor.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Helper {

    private static final String CHANNEL_ID = "2FA_CHANNEL";

    // Format date thành chuỗi dễ đọc
    public static String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(date);
    }

    public static String formatDate(long timestamp) {
        return formatDate(new Date(timestamp));
    }

    // Validate email
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Validate password — tối thiểu 8 ký tự, có chữ hoa, số (theo tài liệu: password strength)
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password) || password.length() < 8) return false;
        boolean hasUpper  = !password.equals(password.toLowerCase());
        boolean hasDigit  = password.matches(".*\\d.*");
        return hasUpper && hasDigit;
    }

    // Kiểm tra có mạng không
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
        return false;
    }

    // Hash password bằng SHA-256 (đơn giản cho intern)
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // fallback nếu lỗi
        }
    }

    // Tính phần trăm
    public static int calculatePercentage(int correct, int total) {
        if (total == 0) return 0;
        return (int) ((correct * 100.0) / total);
    }

    // Rút gọn text dài
    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }

    // Kiểm tra password strength — trả về mô tả ("Weak", "Medium", "Strong")
    public static String getPasswordStrength(String password) {
        if (password.length() < 6) return "Weak";
        boolean hasUpper   = !password.equals(password.toLowerCase());
        boolean hasLower   = !password.equals(password.toUpperCase());
        boolean hasDigit   = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()].*");
        int score = 0;
        if (hasUpper)   score++;
        if (hasLower)   score++;
        if (hasDigit)   score++;
        if (hasSpecial) score++;
        if (password.length() >= 12) score++;
        if (score <= 2) return "Weak";
        if (score == 3) return "Medium";
        return "Strong";
    }

    // Tạo mã OTP 6 số ngẫu nhiên
    public static String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Gửi thông báo chứa mã OTP (Mô phỏng 2FA)
    public static void sendOTPNotification(Context context, String otp) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Two-Factor Authentication",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("AI Study Mentor: Verification Code")
                .setContentText("Your OTP code is: " + otp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
    }
}
