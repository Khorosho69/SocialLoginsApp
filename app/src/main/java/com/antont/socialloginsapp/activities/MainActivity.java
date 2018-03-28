package com.antont.socialloginsapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.antont.socialloginsapp.R;
import com.antont.socialloginsapp.models.UserData;
import com.antont.socialloginsapp.utils.Utils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Utils mUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUtils = new Utils(PreferenceManager.getDefaultSharedPreferences(this));
        UserData userData = mUtils.getUserDataFromIntent(getIntent());

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener((View view) -> logOut());
        initItems(userData);
    }

    private void logOut() {
        mUtils.removeUserDataFromSharedPreferences();

        signOutFromGoogle();
        signOutFromFacebook();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @SuppressLint("RestrictedApi")
    private void signOutFromGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(this, gso);

        client.signOut();
        client.revokeAccess();
    }

    private void signOutFromFacebook() {
        LoginManager.getInstance().logOut();
    }

    private void initItems(UserData data) {
        ImageView userPictImageView = findViewById(R.id.user_picture_image);

        Picasso.get()
                .load(data.getUserImageUrl())
                .placeholder(R.drawable.ic_portrait_black_24dp)
                .into(userPictImageView);

        TextView userNameTextView = findViewById(R.id.user_name_text_view);
        userNameTextView.setText(data.getUserName());

        TextView userEmailTextView = findViewById(R.id.email_text_view);
        userEmailTextView.setText(data.getUserEmail());
    }
}
