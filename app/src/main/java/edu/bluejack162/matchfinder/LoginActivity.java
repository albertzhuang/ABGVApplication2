package edu.bluejack162.matchfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    static final int RC_SIGN_IN = 1;

    static final String TAG = "LOGIN_ACTIVITY";
    String idLogin;
    String mCustomeToken;
    String email;
    String newLink;
    String googlePictureLink;

    GoogleApiClient mGoogleApiClient;

    EditText usernameTxt, passwordTxt;

    TextView registerTxtBtn;

    Button loginBtn;

    DatabaseReference dataBaseReference;

    List<Users> userList;

    LoginButton loginWithFbBtn;

    CallbackManager callBackManager;

    SignInButton loginGoogleBtn;

    FirebaseAuth mAuth;

    FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseUser userGoogleAcc;
    FirebaseUser userFacebokAcc;

    String emailFacebook;
    String nameFacebook;

    Users userFacebook;
    Users userGoogle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        init();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Error exists", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    FirebaseUser userLogin = firebaseAuth.getCurrentUser();
                    if (userLogin != null) {
                        // User is signed in
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + userLogin.getUid());
                    } else {
                        // User is signed out
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                    }
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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

        loginWithFbBtn.setReadPermissions(Arrays.asList("email","public_profile","user_birthday"));

        loginGoogleBtn = (SignInButton) findViewById(R.id.loginGoogleBtnId);
        mAuth = FirebaseAuth.getInstance();

        //set Onclic Listener
        loginBtn.setOnClickListener(this);
        registerTxtBtn.setOnClickListener(this);
        loginGoogleBtn.setOnClickListener(this);

        loginWithFbBtn.registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Login Success
                setFacebookData(loginResult);
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
            final String username = usernameTxt.getText().toString();
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
                        else
                        {
                            for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                                Users user = userSnapShot.getValue(Users.class);
                                if (!password.equals(user.getPassword())) {
                                    Toast.makeText(LoginActivity.this, "Username and Password Invalid", Toast.LENGTH_SHORT).show();
                                } else {
                                    createSession(user.getUsername(), user.getEmail());
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                }
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
        else if(v == loginGoogleBtn)
        {
            signIn();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callBackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            userGoogleAcc = mAuth.getCurrentUser();

                            googlePictureLink = userGoogleAcc.getPhotoUrl().toString();

                            Toast.makeText(LoginActivity.this,"Hai : "+googlePictureLink, Toast.LENGTH_SHORT).show();
                            Log.d("test1",googlePictureLink);
                            final Query query = dataBaseReference.child("users").orderByChild("email").equalTo(userGoogleAcc.getEmail());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getChildrenCount() < 1)
                                    {
                                        //insertData to usersWithGoogle
                                        String id = dataBaseReference.push().getKey();
                                        userGoogle = new Users(userGoogleAcc.getDisplayName(),userGoogleAcc.getEmail(),"",googlePictureLink,"","");
                                        dataBaseReference.child("users").child(id).setValue(userGoogle);
                                    }
                                    else
                                    {
                                        //data already exists
                                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                                            userGoogle = userSnapShot.getValue(Users.class);
                                        }
                                    }
                                    createSession(userGoogle.getUsername(),userGoogle.getEmail());
                                    LoginManager.getInstance().logOut();
                                    Intent intent = new Intent(getApplicationContext(),UserNavigationActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setFacebookData(final LoginResult loginResult)
    {
        //Get facebook profileURL picture URL
        Bundle params = new Bundle();
        params.putString("fields", "id,email,gender,cover,picture.type(large)");
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me", params, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response != null) {
                            try {
                                JSONObject data = response.getJSONObject();
                                if (data.has("picture")) {
                                    newLink = data.getJSONObject("picture").getJSONObject("data").getString("url");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();


        //GET credentials facebook account
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.i("Response",response.toString());

                            emailFacebook = object.getString("email");
                            nameFacebook = object.getString("first_name") + " " + object.getString("last_name");
                            final String gender = response.getJSONObject().getString("gender");

                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            final String link = profile.getLinkUri().toString();
                            Toast.makeText(LoginActivity.this,"Link :  " +link, Toast.LENGTH_SHORT).show();
                            Log.i("Link",link);
                            if (Profile.getCurrentProfile()!=null)
                            {
                                Log.i("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                            }

                            final Query query = dataBaseReference.child("users").orderByChild("email").equalTo(emailFacebook);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.getChildrenCount() < 1)
                                    {
                                        //insertData to usersWithFaceboock
                                        String newId = dataBaseReference.push().getKey();
                                        userFacebook = new Users(nameFacebook,emailFacebook,"",newLink,gender,"");
                                        dataBaseReference.child("users").child(newId).setValue(userFacebook);
                                    }
                                    else
                                    {
                                        for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                                            userFacebook = userSnapShot.getValue(Users.class);
                                        }
                                    }
                                    createSession(userFacebook.getUsername(),userFacebook.getEmail());
                                    LoginManager.getInstance().logOut();
                                    Intent intent = new Intent(getApplicationContext(),UserNavigationActivity.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            //Sign Out Facebook account
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void createSession(String username,String email)
    {
        SharedPreferences sharedPref = getSharedPreferences("userSession",MODE_PRIVATE);

        SharedPreferences.Editor editor =  sharedPref.edit();
        editor.putString("username",username);
        editor.putString("email",email);
        editor.apply();
    }
}
