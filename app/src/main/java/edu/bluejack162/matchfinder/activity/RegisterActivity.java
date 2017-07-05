package edu.bluejack162.matchfinder.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.bluejack162.matchfinder.R;
import edu.bluejack162.matchfinder.model.Users;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    EditText usernameTxt,emailTxt,passwordTxt;

    Button registerBtn;

    FirebaseAuth fireBaseAuth;

    DatabaseReference dataBaseReference;

    Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    public void init()
    {
        usernameTxt = (EditText) findViewById(R.id.usernameTxtId);
        emailTxt = (EditText) findViewById(R.id.emailTxtId);
        passwordTxt = (EditText) findViewById(R.id.passwordTxtId);
        registerBtn = (Button) findViewById(R.id.registerBtnId);
        fireBaseAuth = fireBaseAuth.getInstance();
        dataBaseReference = FirebaseDatabase.getInstance().getReference();

        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == registerBtn)
        {
            String username = usernameTxt.getText().toString();
            String email = emailTxt.getText().toString();
            String password = passwordTxt.getText().toString();
            if(username.equals(""))
            {
                Toast.makeText(this, "username must be filled", Toast.LENGTH_SHORT).show();
            }
            else if(email.equals(""))
            {
                Toast.makeText(this, "email must be filled", Toast.LENGTH_SHORT).show();
            }
            else if(password.equals(""))
            {
                Toast.makeText(this, "password must be filled", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String id =dataBaseReference.push().getKey();
                user = new Users(username,email,password,"","","");
                dataBaseReference.child("users").child(id).setValue(user);
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
    }

    public boolean checkEmpty()
    {
        if(usernameTxt.getText().toString().trim().equals(""))
        {
            Toast.makeText(this, "username must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(emailTxt.getText().equals(""))
        {
            Toast.makeText(this, "email must be filled", Toast.LENGTH_SHORT).show();
            return  false;
        }
        else if(passwordTxt.getText().equals(""))
        {
            Toast.makeText(this, "password must be filled", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
