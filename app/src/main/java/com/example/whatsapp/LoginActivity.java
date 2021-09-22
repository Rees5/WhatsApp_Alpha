package com.example.whatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.cometchat_ui.CometChatUI;
import com.cometchat.pro.uikit.ui_resources.utils.Utils;
import com.cometchat.pro.whatsapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.example.whatsapp.constants.AppConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout inputLayout,pwLayout;
    private ProgressBar progressBar;
    private TextInputEditText uid,pw;
    private TextView title;
    private TextView des1,des2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        title = findViewById(R.id.tvTitle);
        des1 = findViewById(R.id.tvDes1);
        des2 = findViewById(R.id.tvDes2);
        uid = findViewById(R.id.etUID);
        pw= findViewById(R.id.etpw);
        progressBar = findViewById(R.id.loginProgress);
        inputLayout = findViewById(R.id.inputUID);
        pwLayout = findViewById(R.id.inputpw);
        uid.setOnEditorActionListener((textView, i, keyEvent) -> {
             if (i== EditorInfo.IME_ACTION_DONE){
                 if (uid.getText().toString().isEmpty()) {
                     Toast.makeText(LoginActivity.this, "Fill Username field", Toast.LENGTH_LONG).show();
                 }
                 else {
                     progressBar.setVisibility(View.VISIBLE);
                     inputLayout.setEndIconVisible(false);
                     login(uid.getText().toString());
                 }
             }
            return true;
        });

        pwLayout.setEndIconOnClickListener(view -> {
            if (uid.getText().toString().isEmpty()) {
                Toast.makeText(LoginActivity.this, "Fill Username field", Toast.LENGTH_LONG).show();
            }
            else {
                findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);
                inputLayout.setEndIconVisible(false);
                userLogin(uid.getText().toString(),pw.getText().toString());
            }

        });
       // checkDarkMode();
    }

    private void userLogin(String uid, String pwd) {

        //if everything is fine

        class UserLogin extends AsyncTask<Void, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressBar.setVisibility(View.GONE);


                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);

                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                        //getting the user from the response
                        JSONObject userJson = obj.getJSONObject("user");

                        //creating a new user object
                        Users user = new Users(
                                userJson.getInt("id"),
                                userJson.getString("username"),
                                userJson.getString("email"),
                                userJson.getString("gender")
                        );

                        //storing the user in shared preferences
                        SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);

                        //starting the profile activity
                        login(uid);
                       // finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... voids) {
                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("username", uid);
                params.put("password", pwd);

                //returing the response
                return requestHandler.sendPostRequest(URLs.URL_LOGIN, params);
            }
        }

        UserLogin ul = new UserLogin();
        ul.execute();
    }

    private void checkDarkMode() {
        if(Utils.isDarkMode(this)) {
            title.setTextColor(getResources().getColor(R.color.textColorWhite));
            des1.setTextColor(getResources().getColor(R.color.textColorWhite));
            des2.setTextColor(getResources().getColor(R.color.textColorWhite));
            uid.setTextColor(getResources().getColor(R.color.textColorWhite));
            inputLayout.setBoxStrokeColor(getResources().getColor(R.color.textColorWhite));
            inputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            inputLayout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
            uid.setHintTextColor(getResources().getColor(R.color.textColorWhite));
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.textColorWhite)));
        } else {
            title.setTextColor(getResources().getColor(R.color.primaryTextColor));
            des1.setTextColor(getResources().getColor(R.color.primaryTextColor));
            des2.setTextColor(getResources().getColor(R.color.primaryTextColor));
            uid.setTextColor(getResources().getColor(R.color.primaryTextColor));
            inputLayout.setBoxStrokeColor(getResources().getColor(R.color.primaryTextColor));
            uid.setHint("");
            inputLayout.setHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.secondaryTextColor)));
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryTextColor)));
        }
    }
    private void login(String uid) {


        CometChat.login(uid, AppConfig.AppDetails.AUTH_KEY, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                startActivity(new Intent(LoginActivity.this, CometChatUI.class));
                finish();
            }

            @Override
            public void onError(CometChatException e) {
                inputLayout.setEndIconVisible(true);
                findViewById(R.id.loginProgress).setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createUser(View view) {
        startActivity(new Intent(LoginActivity.this,CreateUserActivity.class));
    }

}
