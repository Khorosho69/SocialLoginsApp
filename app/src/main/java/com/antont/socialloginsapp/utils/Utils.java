package com.antont.socialloginsapp.utils;

import android.content.Intent;
import android.content.SharedPreferences;

import com.antont.socialloginsapp.activities.MainActivity;
import com.antont.socialloginsapp.models.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

public class Utils {

    private static final String ARG_USER_ID = "ARG_USER_ID";
    private static final String ARG_USER_NAME = "ARG_USER_NAME";
    private static final String ARG_USER_EMAIL = "ARG_USER_EMAIL";
    private static final String ARG_USER_PICT_URL = "ARG_USER_PICT_URL";

    private static String SHARED_PREFERENCES_USER_DATA = "user_data";

    private Gson mGson;
    private SharedPreferences mSharedPreferences;

    public Utils(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;

        mGson = new GsonBuilder().create();
    }

    public void saveUserInfoIntoSharedPreferences(UserData userData) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(SHARED_PREFERENCES_USER_DATA, mGson.toJson(userData));
        editor.apply();
    }

    public UserData getUserDataFromSharedPreferences() {
        mSharedPreferences.getString(SHARED_PREFERENCES_USER_DATA, null);
        return mGson.fromJson(mSharedPreferences.getString(SHARED_PREFERENCES_USER_DATA, null), UserData.class);
    }

    public void removeUserDataFromSharedPreferences() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(SHARED_PREFERENCES_USER_DATA);
        editor.apply();
    }

    public UserData getUserDataFromGoogleData(GoogleSignInAccount account) {
        String userId = account.getId();
        String userName = account.getDisplayName();
        String userEmail = account.getEmail();
        String userPictUrl = "empty";
        if ((account.getPhotoUrl()) != null) {
            userPictUrl = (account.getPhotoUrl()).toString();
        }
        return new UserData(userId, userName, userEmail, userPictUrl);
    }

    public UserData getUserDataFromFacebookData(JSONObject object) {
        try {
            UserData userData;
            String id = object.getString("id");

            String profilePictureUrl = "empty";
            if (object.has("picture")) {
                profilePictureUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
            }
            String userName = object.getString("last_name") + " " + object.getString("first_name");

            String email = "none";
            if (object.has("email")) {
                email = object.getString("email");
            }

            userData = new UserData(id, userName, email, profilePictureUrl);

            return userData;
        } catch (Exception e) {
            return null;
        }
    }

    public Intent putUserDataToIntent(Intent intent, UserData userData) {
        intent.putExtra(ARG_USER_ID, userData.getUserId());
        intent.putExtra(ARG_USER_NAME, userData.getUserName());
        intent.putExtra(ARG_USER_EMAIL, userData.getUserEmail());
        intent.putExtra(ARG_USER_PICT_URL, userData.getUserImageUrl());
        return intent;
    }

    public UserData getUserDataFromIntent(Intent intent) {
        String userId = intent.getStringExtra(ARG_USER_ID);
        String userName = intent.getStringExtra(ARG_USER_NAME);
        String userEmail = intent.getStringExtra(ARG_USER_EMAIL);
        String userPictUrl = intent.getStringExtra(ARG_USER_PICT_URL);

        return new UserData(userId, userName, userEmail, userPictUrl);
    }
}
