package com.example.realprojectaistudymentor.model;

public abstract class User {
    protected String userId;
    protected String email;
    protected String password;
    protected String fullName;

    public User() {}

    public User(String email, String password, String fullName) {
        this.userId   = String.valueOf(System.currentTimeMillis());
        this.email    = email;
        this.password = password;
        this.fullName = fullName;
    }

    public abstract void register();
    public abstract void login();

    public String getUserId()   { return userId; }
    public String getEmail()    { return email; }
    public String getFullName() { return fullName; }
    public String getPassword() { return password; }
    public void setUserId(String userId)     { this.userId = userId; }
    public void setEmail(String email)       { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
