package com.example.book.ui.Model;

import com.example.book.ui.extra.Preconditions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {

    private String username;
    private String password;
    private String email;
    private int coin;
    private String City;
    private String Town;
    private String profileImg;
    private String phoneNumber;
    private List<String> favPostId;

    public User() {
        this.username = "";
        this.password = "";
        this.email = "";
        this.coin = 10;
        this.City = "";
        this.Town = "";
        this.profileImg = "";
        this.phoneNumber = "";
        this.favPostId = new ArrayList<>();
    }

    public User(String username, String email, String phoneNumber) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.coin = 10;
    }

    public User(String username, String email, int coin, String city, String town, String profileImg, String phoneNumber, List<String> favPostId) {
        this.username = username;
        this.email = email;
        this.coin = coin;
        this.City = city;
        this.Town = town;
        this.profileImg = profileImg;
        this.phoneNumber = phoneNumber;
        this.favPostId = favPostId;
    }


    // region Method
    public boolean validateUsername(String string) {
        return Preconditions.checkNotEmpty(string) &&
                Preconditions.checkNotNull(string);
    }

    public boolean validatePassword(String string) {
        return Preconditions.checkNotEmpty(string) &&
                Preconditions.checkNotNull(string) &&
                Preconditions.isStrongPassword(string);
    }

    public boolean validatePhoneNumber(String string) {
        return Preconditions.validPakistanNumber(string) &&
                Preconditions.checkNotNull(string) &&
                Preconditions.checkNotEmpty(string);
    }
    // endregion Method


    // region Getter/Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getTown() {
        return Town;
    }

    public void setTown(String town) {
        Town = town;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getFavPostId() {
        return favPostId;
    }

    public void setFavPostId(List<String> favPostId) {
        this.favPostId = favPostId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
