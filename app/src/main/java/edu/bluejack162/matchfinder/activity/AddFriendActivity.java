package edu.bluejack162.matchfinder.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack162.matchfinder.model.Friend;
import edu.bluejack162.matchfinder.adapter.ListFriendAdapter;
import edu.bluejack162.matchfinder.R;
import edu.bluejack162.matchfinder.model.Users;

public class AddFriendActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener, ListFriendAdapter.customButtonListener {

    CircleImageView profileImage;
    Button addBtn;
    EditText searchTxt;
    DatabaseReference databaseReference;
    Users userSearch,userLogin;
    TextView usernameTxt;
    ListView friendListView;
    ListFriendAdapter listFriendAdapter;
    ArrayList<String> friendIds;
    int staticPosition;
    String deletedFriendId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        init();
    }

    public void init()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userSearch = new Users();
        userLogin = new Users();
        searchTxt = (EditText) findViewById(R.id.searchTxtId);
        profileImage = (CircleImageView) findViewById(R.id.profileImageId);
        addBtn = (Button) findViewById(R.id.addFriendBtnId);
        usernameTxt = (TextView) findViewById(R.id.usernameTxtId);
        friendListView = (ListView) findViewById(R.id.friendListId);
        friendIds = new ArrayList<String>();

        //setListener
        searchTxt.setOnKeyListener(this);
        addBtn.setOnClickListener(this);

        checkSession();

        listFriendAdapter = new ListFriendAdapter(getApplicationContext());
        getAllFriend(userLogin.getUserId());
    }

    public void getAllFriend(String search)
    {
        final Query query = databaseReference.child("friend").orderByChild("userAddId").equalTo(search);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    friendListView.setAdapter(null);
                }
                else
                {
                    for(DataSnapshot friendSnapShot : dataSnapshot.getChildren())
                    {
                        Friend friend = friendSnapShot.getValue(Friend.class);

                        friendIds.add(friend.getFriendId());

                        ListFriendAdapter newFriendAdapter = new ListFriendAdapter(getApplicationContext());
                        newFriendAdapter.add(friend.getFrienName(),friend.getFriendProfile());

                        newFriendAdapter.setCustomButtonListner(AddFriendActivity.this);
                        friendListView.setAdapter(newFriendAdapter);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddFriendActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllFriendAfterDelete(String search, final String isDeleteId)
    {
        final Query query = databaseReference.child("friend").orderByChild("userAddId").equalTo(search);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    friendListView.setAdapter(null);
                }
                else
                {
                    for(DataSnapshot friendSnapShot : dataSnapshot.getChildren())
                    {
                        Friend friend = friendSnapShot.getValue(Friend.class);
                        if(friend.getFriendId().equals(isDeleteId))
                        {

                        }
                        else
                        {
                            friendIds.add(friend.getFriendId());
                            ListFriendAdapter newFriendAdapter = new ListFriendAdapter(getApplicationContext());
                            newFriendAdapter.add(friend.getFrienName(), friend.getFriendProfile());
                            newFriendAdapter.setCustomButtonListner(AddFriendActivity.this);
                            friendListView.setAdapter(newFriendAdapter);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddFriendActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(event.getAction() != KeyEvent.ACTION_DOWN)
        {
            return true;
        }
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_ENTER:
                String search = searchTxt.getText().toString().trim();
                selectUser(search,userLogin.getUserId());
                break;
            case KeyEvent.KEYCODE_BACK:
                Intent intent = new Intent(getApplicationContext(),UserNavigationActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    public void selectUser(String search, final String userLoginId)
    {
        final Query query = databaseReference.child("users").orderByChild("username").equalTo(search);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    usernameTxt.setText("User Not Found");
                    searchTxt.setText("");
                    addBtn.setVisibility(View.INVISIBLE);
                    profileImage.setVisibility(View.INVISIBLE);
                    usernameTxt.setVisibility(View.VISIBLE);
                }
                else
                {
                    for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                    {
                        if(userLoginId.equals(userSnapShot.getKey()))
                        {
                            usernameTxt.setText("User Not Found");
                            searchTxt.setText("");
                            addBtn.setVisibility(View.INVISIBLE);
                            profileImage.setVisibility(View.INVISIBLE);
                            usernameTxt.setVisibility(View.VISIBLE);
                            break;
                        }
                        else
                        {
                            userSearch = userSnapShot.getValue(Users.class);
                            usernameTxt.setText(userSearch.getUsername());
                            userSearch.setUserId(userSnapShot.getKey());
                            new AddFriendActivity.DownLoadImageTask(profileImage).execute(userSearch.getProfileImage());
                            addBtn.setVisibility(View.VISIBLE);
                            profileImage.setVisibility(View.VISIBLE);
                            usernameTxt.setVisibility(View.VISIBLE);
                            searchTxt.setText("");
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddFriendActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteFriendById(final String friendId)
    {
        final Query query = databaseReference.child("friend").orderByChild("friendId").equalTo(friendId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                       Toast.makeText(AddFriendActivity.this, "delete User Bot Found", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for(DataSnapshot friendSnapShot : dataSnapshot.getChildren())
                    {
                        Friend friend = friendSnapShot.getValue(Friend.class);
                        deletedFriendId = friend.getFriendId();
                        databaseReference.child("friend").child(friendSnapShot.getKey()).setValue(null);
                        getAllFriendAfterDelete(userLogin.getUserId(),deletedFriendId);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddFriendActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == addBtn)
        {
            String id = databaseReference.push().getKey();
            Friend friend = new Friend(userLogin.getUserId(),userSearch.getUserId(),userSearch.getUsername(),userSearch.getProfileImage());
            databaseReference.child("friend").child(id).setValue(friend);
            getAllFriend(userLogin.getUserId());

            addBtn.setVisibility(View.INVISIBLE);
            profileImage.setVisibility(View.INVISIBLE);
            usernameTxt.setVisibility(View.INVISIBLE);
        }
    }

    public void checkSession()
    {
        userLogin = new Users();
        SharedPreferences userSession = this.getSharedPreferences("userSession", Context.MODE_PRIVATE);
        String userId = userSession.getString("userID","");
        String username = userSession.getString("username","");
        String email = userSession.getString("email","");
        userLogin.setUserId(userId);
        userLogin.setUsername(username);
        userLogin.setEmail(email);
    }

    @Override
    public void onButtonClickListner(int position, String value) {
        //Delete Listener
        deleteFriendById(friendIds.get(position));
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
