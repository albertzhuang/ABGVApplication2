package edu.bluejack162.matchfinder.activity;


import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack162.matchfinder.fragment.CreateEventFragment;
import edu.bluejack162.matchfinder.fragment.ProfileFragment;
import edu.bluejack162.matchfinder.R;
import edu.bluejack162.matchfinder.model.Users;

public class UserNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ClipData.Item friendIcon;
    TextView usernameTxt,emailTxt;
    Users userLogin;
    CircleImageView profileImage;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sportastig");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userLogin = new Users();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();




        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View navView = navigationView.getHeaderView(0);
        usernameTxt = (TextView) navView.findViewById(R.id.navUsernameTxtId);
        emailTxt = (TextView) navView.findViewById(R.id.navEmailtxtId);
        profileImage = (CircleImageView) navView.findViewById(R.id.navProfileImageId);
        checkSession();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_friend)
        {
            Intent intent = new Intent(getApplicationContext(),AddFriendActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void displaySelectedScreen(int id)
    {
        Fragment fragment = null;
        switch(id)
        {
            case R.id.nav_profile :
                fragment = new ProfileFragment();
                break;
            case R.id.nav_event:
                fragment = new CreateEventFragment();
                break;
            case R.id.nav_logout:
                clearSession();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
        }

        if(fragment != null)
        {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main,fragment);
            ft.commit();
        }
        DrawerLayout drawer =  (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedScreen(id);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void checkSession()
    {
        SharedPreferences userSession = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        String username = userSession.getString("username","");
        String email = userSession.getString("email","");
        String profileImageString = userSession.getString("profileImage","");
        userLogin.setEmail(email);
        userLogin.setUsername(username);
        userLogin.setProfileImage(profileImageString);

        if(!username.equals("") || !email.equals(""))
        {
            usernameTxt.setText(userLogin.getUsername());
            emailTxt.setText(userLogin.getEmail());
            new DownLoadImageTask(profileImage).execute(userLogin.getProfileImage());
        }
        else
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    public void clearSession()
    {
        SharedPreferences userSession = getSharedPreferences("userSession", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  userSession.edit();
        editor.clear();
        editor.apply();
    }


    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        CircleImageView imageView;

        public DownLoadImageTask(CircleImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream((InputStream) new URL(urldisplay).getContent());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }

}
