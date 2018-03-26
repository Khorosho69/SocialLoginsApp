package com.antont.socialloginsapp.activities;

import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.antont.socialloginsapp.models.UserData;
import com.antont.socialloginsapp.utils.Utils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.antont.socialloginsapp.R;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1000;

    private CallbackManager mFacebookCallbackManager;
    private UserData mUserData;
    private GoogleApiClient mGoogleApiClient;
    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_log_in);

        mUtils = new Utils(PreferenceManager.getDefaultSharedPreferences(this));

        mUserData = mUtils.getUserData();
        if (mUserData != null) {
            startMainActivity();
        } else {
            setupFacebookLoginButton();
            setupGoogleLoginButton();
        }
    }

    private void setupGoogleLoginButton() {
        SignInButton googleSignInButton = findViewById(R.id.google_login_button);
        googleSignInButton.setOnClickListener((View view) -> signInWithGoogle());
    }


    private void setupFacebookLoginButton() {
        LoginButton facebookSignInButton = findViewById(R.id.facebook_login_button);

        facebookSignInButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        mFacebookCallbackManager = CallbackManager.Factory.create();

        facebookSignInButton.registerCallback(mFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        getData();
                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult(null);
                    }
                }
        );
    }

    private void getData() {
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
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void signInWithGoogle() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
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
            mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(Callable<Void> logout) {
        if (logout != null && mUserData != null) {
            mUtils.saveUserInfo(mUserData);
            startMainActivity();
        } else {
            Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.ARG_USER_ID, mUserData.getUserId());
        intent.putExtra(MainActivity.ARG_USER_NAME, mUserData.getUserName());
        intent.putExtra(MainActivity.ARG_USER_EMAIL, mUserData.getUserEmail());
        intent.putExtra(MainActivity.ARG_USER_PICT_URL, mUserData.getUserImageUrl());
        startActivity(intent);
    }
}
