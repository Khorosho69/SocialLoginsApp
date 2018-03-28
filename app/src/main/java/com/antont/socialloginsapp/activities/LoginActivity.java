package com.antont.socialloginsapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.antont.socialloginsapp.models.UserData;
import com.antont.socialloginsapp.utils.Utils;
import com.antont.socialloginsapp.view_models.LoginActivityViewModel;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.antont.socialloginsapp.R;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.models.User;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    private LoginActivityViewModel mLoginActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplication().getApplicationContext());

        setContentView(R.layout.activity_log_in);

        mLoginActivityViewModel = ViewModelProviders.of(this).get(LoginActivityViewModel.class);

        UserData userData = mLoginActivityViewModel.getUserData();

        if (userData != null) {
            mLoginActivityViewModel.startMainActivity();
        } else {
            setupFacebookLoginButton();
            setupGoogleLoginButton();
        }
    }

    private void setupGoogleLoginButton() {
        SignInButton googleSignInButton = findViewById(R.id.google_login_button);
        googleSignInButton.setOnClickListener((View view) -> startActivityForResult(mLoginActivityViewModel.signInWithGoogle(), LoginActivityViewModel.RC_SIGN_IN));
    }

    private void setupFacebookLoginButton() {
        LoginButton facebookSignInButton = findViewById(R.id.facebook_login_button);

        facebookSignInButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        facebookSignInButton.registerCallback(
                mLoginActivityViewModel.getCallbackManager(),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        mLoginActivityViewModel.getFacebookData();
                    }

                    @Override
                    public void onCancel() {
                        mLoginActivityViewModel.handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        mLoginActivityViewModel.handleSignInResult(null);
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLoginActivityViewModel.onActivityResult(requestCode, resultCode, data);
    }
}
