package com.example.realprojectaistudymentor.model;

/**
 * Model thông báo — hiển thị trên notification list.
 * Notification được tạo rule-based khi user đạt điều kiện (badge, streak, etc.)
 */
public class Notification {

    private String notificationId;
    private String title;
    private String message;
    private String type;        // "badge", "streak", "achievement", "system"
    private boolean read;
    private long timestamp;

    public Notification() {
        this.notificationId = String.valueOf(System.currentTimeMillis());
        this.timestamp = System.currentTimeMillis();
        this.read = false;
    }

    public Notification(String title, String message, String type) {
        this();
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters & Setters
    public String getNotificationId() { return notificationId; }
    public void setNotificationId(String id) { this.notificationId = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
