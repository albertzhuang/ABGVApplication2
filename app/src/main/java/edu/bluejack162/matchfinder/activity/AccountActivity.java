package edu.bluejack162.matchfinder.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import edu.bluejack162.matchfinder.R;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener{

    Button logOutGoogleBtnId;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        logOutGoogleBtnId = (Button) findViewById(R.id.logOutGoogleBtnId);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null)
                {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        };

        logOutGoogleBtnId.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == logOutGoogleBtnId)
        {
            SharedPreferences sharedPref = getSharedPreferences("userSession",MODE_PRIVATE);
            String username = sharedPref.getString("username","");
            String email = sharedPref.getString("email","");

            Toast.makeText(this, username + " " + email, Toast.LENGTH_SHORT).show();
        }
    }
}
