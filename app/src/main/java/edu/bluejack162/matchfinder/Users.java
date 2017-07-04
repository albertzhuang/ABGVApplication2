package edu.bluejack162.matchfinder;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alber on 20/06/2017.
 */

public class Users {
    private String username;
    private String email;
    private String password;
    private String profileImage;
    private Uri profileFacebook;
    private String gender;
    private String favoriteSport;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFavoriteSport() {
        return favoriteSport;
    }

    public void setFavoriteSport(String favoriteSport) {
        this.favoriteSport = favoriteSport;
    }

    public Uri getProfileFacebook() {
        return profileFacebook;
    }

    public void setProfileFacebook(Uri profileFacebook) {
        this.profileFacebook = profileFacebook;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Users(){

    }

    public Users(Users users)
    {
        this.username = users.getUsername();
        this.email = users.getEmail();
        this.password = users.getPassword();
        this.profileImage = users.getProfileImage();
        this.gender = users.getGender();
        this.favoriteSport = users.getFavoriteSport();
    }

    public Users(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Users(String username, String email, String password, String profileImage) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
    }

    public Users(String username, String email, String password, String profileImage, String gender, String favoriteSport) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.gender = gender;
        this.favoriteSport = favoriteSport;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("username", username);
        result.put("profileImage", profileImage);
        result.put("gender",gender);
        result.put("favoriteSport", favoriteSport);
        return result;
    }
}