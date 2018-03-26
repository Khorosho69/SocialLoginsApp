package com.antont.socialloginsapp.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.antont.socialloginsapp.R;
import com.antont.socialloginsapp.models.UserData;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    public static final String ARG_USER_ID = "ARG_USER_ID";
    public static final String ARG_USER_NAME = "ARG_USER_NAME";
    public static final String ARG_USER_EMAIL = "ARG_USER_EMAIL";
    public static final String ARG_USER_PICT_URL = "ARG_USER_PICT_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserData userData = getUserDataFromIntent(getIntent());

        initItems(userData);
    }

    private UserData getUserDataFromIntent(Intent intent) {
        String userId = intent.getStringExtra(ARG_USER_ID);
        String userName = intent.getStringExtra(ARG_USER_NAME);
        String userEmail = intent.getStringExtra(ARG_USER_EMAIL);
        String userPictUrl = intent.getStringExtra(ARG_USER_PICT_URL);

        return new UserData(userId, userName, userEmail, userPictUrl);
    }

    private void initItems(UserData data) {
        ImageView userPict = findViewById(R.id.user_picture_image);
        Picasso.get()
                .load(data.getUserImageUrl())
                .placeholder(R.drawable.ic_portrait_black_24dp)
                .into( userPict);

        TextView userNameTextView = findViewById(R.id.user_name_text_view);
        userNameTextView.setText(data.getUserName());

        TextView userEmailTextView = findViewById(R.id.email_text_view);
        userEmailTextView.setText(data.getUserEmail());
    }

}
