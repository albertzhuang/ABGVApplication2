package edu.bluejack162.matchfinder;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by alber on 27/06/2017.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    Button buttonUpdate;
    Users userLogIn;
    DatabaseReference databaseReference;
    EditText usernameTxt,emailTxt;
    CircleImageView profileImage;
    Spinner genderSpinner,favoriteSportSpinner;
    ArrayAdapter<CharSequence> genderAdapter, favoriteSportAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        checkSession();
    }

    public void checkSession()
    {
        userLogIn = new Users();
        SharedPreferences userSession = this.getActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE);
        String username = userSession.getString("username","");
        String email = userSession.getString("email","");
        userLogIn.setEmail(email);
        userLogIn.setUsername(username);

        if(!username.equals("") || !email.equals(""))
        {
            getUserData(email);
        }
    }

    public void setData(Users user)
    {
        emailTxt.setText(user.getEmail());
        usernameTxt.setText(user.getUsername());
        if(user.getGender().equals("") || user.getGender().equals("Gender"))
        {
            genderSpinner.setSelection(0);
        }
        else if(user.getGender().equals("male"))
        {
            genderSpinner.setSelection(1);
        }
        else
        {
            genderSpinner.setSelection(2);
        }

        if(user.getFavoriteSport().equals("") || user.getFavoriteSport().equals("favorite Sport"))
        {
            favoriteSportSpinner.setSelection(0);
        }
        else if(user.getFavoriteSport().equals("soccer"))
        {
            favoriteSportSpinner.setSelection(1);
        }
        else if(user.getFavoriteSport().equals("basket"))
        {
            favoriteSportSpinner.setSelection(2);
        }
        else
        {
            favoriteSportSpinner.setSelection(3);
        }
    }



    public void init()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        usernameTxt = (EditText) getActivity().findViewById(R.id.usernameTxtId);
        emailTxt = (EditText) getActivity().findViewById(R.id.emailTxtId);
        profileImage = (CircleImageView) getActivity().findViewById(R.id.profileImageId);
        genderSpinner = (Spinner) getActivity().findViewById(R.id.genderSpinnerId);
        favoriteSportSpinner = (Spinner) getActivity().findViewById(R.id.favoriteSportSpinnerId);
        genderAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.gender_array,android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        favoriteSportAdapter = ArrayAdapter.createFromResource(getActivity(),R.array.favorite_sport_array,android.R.layout.simple_spinner_item);
        favoriteSportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        favoriteSportSpinner.setAdapter(favoriteSportAdapter);

        buttonUpdate = (Button) getActivity().findViewById(R.id.updateBtnId);

        //set Listener
        usernameTxt.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        genderSpinner.setOnItemSelectedListener(this);
        favoriteSportSpinner.setOnItemSelectedListener(this);

        //onStart
        start();
    }
    public void start()
    {
        emailTxt.setEnabled(false);
        buttonUpdate.setEnabled(false);
    }

    public  void getUserData(String email)
    {
        final Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    Toast.makeText(getActivity(), "Wrong user", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                    {
                        userLogIn = userSnapShot.getValue(Users.class);
                        setData(userLogIn);
                        new DownLoadImageTask(profileImage).execute(userLogIn.getProfileImage());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == usernameTxt || v == emailTxt)
        {
            buttonUpdate.setEnabled(true);
        }
        else if(v == buttonUpdate)
        {
            //update Profile
            String email = emailTxt.getText().toString();
            final Query query = databaseReference.child("users").orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getChildrenCount() < 1)
                    {
                        Toast.makeText(getActivity(), "Select Failed", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                        {
                            userLogIn = userSnapShot.getValue(Users.class);
                            userLogIn.setUsername(usernameTxt.getText().toString());
                            userLogIn.setGender(genderSpinner.getSelectedItem().toString());
                            userLogIn.setFavoriteSport(favoriteSportSpinner.getSelectedItem().toString());
                            Users user = new Users(userLogIn);
                            Map<String, Object> postValues =user.toMap();
                            Map<String, Object> childUpdates = new HashMap<>();
                            childUpdates.put("users/" + userSnapShot.getKey(), postValues);
                            databaseReference.updateChildren(childUpdates);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                }
            });
            buttonUpdate.setEnabled(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        buttonUpdate.setEnabled(true);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
