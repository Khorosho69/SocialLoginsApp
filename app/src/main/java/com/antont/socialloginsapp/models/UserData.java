package com.antont.socialloginsapp.models;

public class UserData {

    private String mUserId;
    private String mUserName;
    private String mUserEmail;
    private String mUserImageUrl;

    public UserData(String userId, String userName, String userEmail, String userImageUrl) {
        mUserId = userId;
        mUserName = userName;
        mUserEmail = userEmail;
        mUserImageUrl = userImageUrl;
    }

    public String getUserId() {
        return mUserId;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public String getUserImageUrl() {
        return mUserImageUrl;
    }
}
