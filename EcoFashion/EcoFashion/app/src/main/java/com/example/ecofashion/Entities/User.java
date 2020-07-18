package com.example.ecofashion.Entities;

public class User {
    public long id;

    private String userEmail;
    public String userName;
    private String userPassword;
    private String userPhone;
    private String userAddress;
    private String userZip;
    private String userStatus;

    public User(long id, String userEmail, String userName, String userPassword, String userPhone, String userAddress, String userZip, String userStatus) {
        this.id = id;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userZip = userZip;
        this.userStatus = userStatus;
    }

    public User(String userEmail, String userName, String userPassword, String userPhone, String userAddress, String userZip, String userStatus) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userPhone = userPhone;
        this.userAddress = userAddress;
        this.userZip = userZip;
        this.userStatus = userStatus;
    }

    public long getid() {
        return id;
    }

    public void setid(long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) { this.userName = userName; }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserZip() {
        return userZip;
    }

    public void setUserZip(String userZip) {
        this.userZip = userZip;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }
}
