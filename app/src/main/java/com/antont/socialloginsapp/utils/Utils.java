package com.antont.socialloginsapp.utils;

import android.content.SharedPreferences;
import android.util.Log;

import com.antont.socialloginsapp.models.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

public class Utils {

    private Gson mGson;
    private SharedPreferences mSharedPreferences;

    public Utils(SharedPreferences sharedPreferences) {
        mSharedPreferences = sharedPreferences;

        mGson = new GsonBuilder().create();
    }

    public void saveUserInfo(UserData userData) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("user_data", mGson.toJson(userData));
        editor.apply();
    }

    public UserData getUserData() {
        mSharedPreferences.getString("user_data", null);
        return mGson.fromJson(mSharedPreferences.getString("user_data", null), UserData.class);
    }

    public UserData getUserDataFromGoogleData(GoogleSignInAccount account) {
        String userId = account.getId();
        String userName = account.getDisplayName();
        String userEmail = account.getEmail();
        String userPictUrl = "";
        if ((account.getPhotoUrl()) != null) {
            userPictUrl = (account.getPhotoUrl()).toString();
        }
        return new UserData(userId, userName, userEmail, userPictUrl);
    }

    public UserData getUserDataFromFacebookData(JSONObject object) {
        Log.d("yay", "getUserDataFromFacebookData: ");
        try {
            UserData userData;
            String id = object.getString("id");

            String profilePictureUrl = "https://graph.facebook.com/" + id + "/picture?type=large";
            String userName = object.getString("last_name") + " " + object.getString("first_name");

            String email = "";
            if (object.has("email")) {
                email = object.getString("email");
            }

            userData = new UserData(id, userName, email, profilePictureUrl);

            return userData;
        } catch (Exception e) {
            Log.d("yay", "BUNDLE Exception : " + e.toString());
            return null;
        }
    }
}
