package com.antont.socialloginsapp.view_models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.antont.socialloginsapp.activities.LoginActivity;
import com.antont.socialloginsapp.activities.MainActivity;
import com.antont.socialloginsapp.models.UserData;
import com.antont.socialloginsapp.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.concurrent.Callable;

public class LoginActivityViewModel extends AndroidViewModel {

    public static final int RC_SIGN_IN = 1000;

    private CallbackManager mCallbackManager;

    private UserData mUserData;
    private GoogleApiClient mGoogleApiClient;
    private Utils mUtils;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);

        mUtils = new Utils(PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext()));
        mUserData = mUtils.getUserDataFromSharedPreferences();
    }

    public Intent signInWithGoogle() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplication().getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    }

    public void getFacebookData() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                (jsonObject, response) -> {
                    mUserData = mUtils.getUserDataFromFacebookData(jsonObject);
                    handleSignInResult(() -> {
                        LoginManager.getInstance().logOut();
                        return null;
                    });
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public CallbackManager getCallbackManager() {
        if (mCallbackManager == null)
            mCallbackManager = CallbackManager.Factory.create();
        return mCallbackManager;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                mUserData = mUtils.getUserDataFromGoogleData(result.getSignInAccount());
                final GoogleApiClient client = mGoogleApiClient;
                handleSignInResult(() -> {
                    if (client != null) {
                        Auth.GoogleSignInApi.signOut(client).setResultCallback(
                                status -> Log.d(LoginActivity.class.getCanonicalName(), status.getStatusMessage())
                        );
                    }
                    return null;
                });
            } else {
                handleSignInResult(null);
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void handleSignInResult(Callable<Void> logout) {
        if (logout != null && mUserData != null) {
            mUtils.saveUserInfoIntoSharedPreferences(mUserData);
            startMainActivity();
        } else {
            Toast.makeText(getApplication().getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
        }
    }

    public void startMainActivity() {
        Intent intent = mUtils.putUserDataToIntent(new Intent(getApplication().getApplicationContext(), MainActivity.class), mUserData);
        getApplication().getApplicationContext().startActivity(intent);
    }

    public UserData getUserData() {
        return mUserData;
    }
}
