package edu.bluejack162.matchfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLStreamHandlerFactory;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    static final int IO_BUFFER_SIZE = 4*1024;

    private static final int RESULT_LOAD_IMAGE = 1;

    ImageView profileImage;

    Button uploadBtn;

    DatabaseReference databaseReference;

    Users userLogin;

    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        profileImage = (ImageView) findViewById(R.id.profileImageId);
        uploadBtn = (Button) findViewById(R.id.uploadBtnId);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        uploadBtn.setOnClickListener(this);

        if(checkSession())
        {

            //imageUrl= "https://scontent.xx.fbcdn.net/v/t1.0-1/c218.42.525.525/s200x200/935636_632221070139234_247860387_n.jpg?oh=c26181b3d31aa36ce4df3da36aa9c5a5&oe=59DE78DF";
            //Toast.makeText(this, userLogin.getEmail(), Toast.LENGTH_SHORT).show();
            //imageUrl = userLogin.getProfileImage();
            //imageUrl = "http://graph.facebook.com/1744223575605639/picture?type=large";

        }
    }

    @Override
    public void onClick(View v) {
        if(v == uploadBtn)
        {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,RESULT_LOAD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK)
        {
            //Uri selectedImage = data.getData();
            //profileImage.setImageURI(selectedImage);
        }
    }



    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
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
                //mIcon11 = BitmapFactory.decodeStream(in);
                mIcon11 = BitmapFactory.decodeStream((InputStream)new URL(urldisplay).getContent());
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            //pd.dismiss();
            imageView.setImageBitmap(result);
        }
    }

    public boolean checkSession()
    {
        SharedPreferences sharedPref = getSharedPreferences("userSession",MODE_PRIVATE);
        String username = sharedPref.getString("username","");
        String email = sharedPref.getString("email","");

        if(username.equals("") || email.equals(""))
        {
            return false;
        }
        Toast.makeText(this, username + " " + email, Toast.LENGTH_SHORT).show();
        selectLoginUser(username,email);
        return true;
    }


    public void selectLoginUser(String username,String email)
    {
        final Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    Toast.makeText(HomeActivity.this, "Select Failed", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                        userLogin = userSnapShot.getValue(Users.class);
                        imageUrl = userLogin.getProfileImage();
                        new DownLoadImageTask(profileImage).execute(imageUrl);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
