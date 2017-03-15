package com.uoit.calvin.thesis_2016;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

public class User {

    private String displayName;
    private String username;
    private long id;
    private int profileImage;
    private Context context;
    private String profileImageUrl;
    private int count;

    public User() {
    }

    public User(Context context, String displayName, String username) {
        this.displayName = displayName;
        this.username = username;
        this.context = context;
        this.profileImage = R.drawable.ic_label_outline_white_big;
    }

    public User(Context context, String displayName, String username, int profileImage) {
        this.displayName = displayName;
        this.username = username;
        this.profileImage = profileImage;
    }



    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void addCount() {
        this.count++;
    }
}
