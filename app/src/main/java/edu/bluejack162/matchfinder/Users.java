package edu.bluejack162.matchfinder;

import android.net.Uri;

/**
 * Created by alber on 20/06/2017.
 */

public class Users {
    private String username;
    private String email;
    private String password;
    private String profileImage;
    private Uri profileFacebook;

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
}
