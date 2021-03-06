package com.example.whatsapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.whatsapp.R;
import com.example.whatsapp.constants.AppConfig;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private MaterialButton loginBtn;

    private MaterialCardView superhero1;

    private MaterialCardView superhero2;

    private MaterialCardView superhero3;

    private MaterialCardView superhero4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (CometChat.getLoggedInUser()!=null)
        {
            startActivity(new Intent(MainActivity.this,CometChatUI.class));
        }
        loginBtn = findViewById(R.id.login);
        superhero1 = findViewById(R.id.superhero1);
        superhero2 = findViewById(R.id.superhero2);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                }
            });

        superhero1.setOnClickListener(view -> {
                findViewById(R.id.superhero1_progressbar).setVisibility(View.VISIBLE);
                login("superhero1");
        });
        superhero2.setOnClickListener(view -> {
                findViewById(R.id.superhero2_progressbar).setVisibility(View.VISIBLE);
                login("superhero2");
        });
        if(Utils.isDarkMode(this)) {
       //     ivLogo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
        }
        else {
//            ivLogo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColor)));
        }
    }

    private void login(String uid) {
        CometChat.login(uid, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                startActivity(new Intent(MainActivity.this, CometChatUI.class));
                finish();
            }

            @Override
            public void onError(CometChatException e) {
                String str = uid+"_progressbar";
                int id = getResources().getIdentifier(str,"id",getPackageName());
                findViewById(id).setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void regUser(View view) {
        startActivity(new Intent(MainActivity.this,CreateUserActivity.class));
    }
}
