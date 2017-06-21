package edu.bluejack162.matchfinder;

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    EditText usernameTxt, passwordTxt;

    TextView registerTxtBtn;

    Button loginBtn;

    DatabaseReference dataBaseReference;

    List<Users> userList;

    LoginButton loginWithFbBtn;

    CallbackManager callBackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        init();
        //loginWithFacebook();
    }

    public void init()
    {
        usernameTxt = (EditText) findViewById(R.id.usernameTxtId);
        passwordTxt = (EditText) findViewById(R.id.passwordTxtId);
        loginBtn = (Button) findViewById(R.id.loginBtnId);
        registerTxtBtn = (TextView) findViewById(R.id.registerTxtId);
        dataBaseReference = FirebaseDatabase.getInstance().getReference();
        userList = new ArrayList<>();
        loginWithFbBtn = (LoginButton) findViewById(R.id.loginFaceboockBtnId);
        callBackManager = CallbackManager.Factory.create();
        loginWithFbBtn.setReadPermissions(Arrays.asList("public_profile","email","user_birthday"));

        //set Onclic Listener
        loginBtn.setOnClickListener(this);
        registerTxtBtn.setOnClickListener(this);

        loginWithFbBtn.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "Login success ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == loginBtn)
        {
            String username = usernameTxt.getText().toString();
            final String password = passwordTxt.getText().toString();
            if(username.equals(""))
            {
                Toast.makeText(this, "username must be filled", Toast.LENGTH_SHORT).show();
            }
            else if(password.equals(""))
            {
                Toast.makeText(this, "password must be fileld", Toast.LENGTH_SHORT).show();
            }
            else
            {
                final Query query = dataBaseReference.child("users").orderByChild("username").equalTo(username);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() < 1)
                        {
                            Toast.makeText(LoginActivity.this, "User not Exists", Toast.LENGTH_SHORT).show();
                        }
                        for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                        {
                            Users user = userSnapShot.getValue(Users.class);
                            if(!password.equals(user.getPassword()))
                            {
                                Toast.makeText(LoginActivity.this, "Username and Password Invalid", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this, "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else if(v == registerTxtBtn)
        {
            Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
            startActivity(intent);
        }
    }

    public void loginWithFacebook()
    {
        LoginManager.getInstance().registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(LoginActivity.this, "Login success " + loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, "Login Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callBackManager.onActivityResult(requestCode, resultCode, data);
    }
}
