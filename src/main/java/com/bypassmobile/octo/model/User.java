package com.bypassmobile.octo.model;


import com.google.gson.annotations.SerializedName;

public class User implements Comparable<User> {

    @SerializedName("login")
    private final String name;

    @SerializedName("avatar_url")
    private final String profileURL;

    public User(String name, String profileURL) {
        this.name = name;
        this.profileURL = profileURL;
    }

    public String getName() {
        return name;
    }

    public String getProfileURL() {
        return profileURL;
    }


    @Override
    public int compareTo(User user) {
        return name.toLowerCase().compareTo(user.name.toLowerCase());
    }
}
