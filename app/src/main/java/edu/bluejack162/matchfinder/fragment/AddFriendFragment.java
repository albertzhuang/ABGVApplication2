package edu.bluejack162.matchfinder.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack162.matchfinder.R;
import edu.bluejack162.matchfinder.activity.AddFriendActivity;
import edu.bluejack162.matchfinder.activity.UserNavigationActivity;
import edu.bluejack162.matchfinder.adapter.ListFriendAdapter;
import edu.bluejack162.matchfinder.adapter.ListUserAdapter;
import edu.bluejack162.matchfinder.model.Friend;
import edu.bluejack162.matchfinder.model.Users;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendFragment extends Fragment implements View.OnKeyListener, View.OnClickListener ,ListUserAdapter.customButtonListener{

    CircleImageView profileImage;
    Button addBtn;
    EditText searchTxt;
    DatabaseReference databaseReference;
    Users userSearch,userLogin;
    TextView usernameTxt;
    ListView userListView;
    ListFriendAdapter listFriendAdapter;
    ArrayList<String> friendIds;
    int staticPosition;
    String deletedFriendId;
    ArrayList<Users> users;
    ArrayList<Friend> friends;
    int index = 0;
    int count = 0;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    public void init()
    {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userSearch = new Users();
        userLogin = new Users();
        searchTxt = (EditText) getActivity().findViewById(R.id.searchTxtId);
        profileImage = (CircleImageView) getActivity().findViewById(R.id.profileImageId);
        addBtn = (Button) getActivity().findViewById(R.id.addFriendBtnId);
        usernameTxt = (TextView) getActivity().findViewById(R.id.usernameTxtId);
        userListView = (ListView) getActivity().findViewById(R.id.friendListId);
        friendIds = new ArrayList<String>();
        users = new ArrayList<Users>();
        friends = new ArrayList<Friend>();

        //setListener
        searchTxt.setOnKeyListener(this);
        addBtn.setOnClickListener(this);

        listFriendAdapter = new ListFriendAdapter(getActivity().getApplicationContext());

        //onStart
        checkSession();
        selectByUserAndFriend();
        //selectAllUser();
    }


    public void checkSession()
    {
        userLogin = new Users();
        SharedPreferences userSession = getActivity().getSharedPreferences("userSession", Context.MODE_PRIVATE);
        String userId = userSession.getString("userID","");
        String username = userSession.getString("username","");
        String email = userSession.getString("email","");
        userLogin.setUserId(userId);
        userLogin.setUsername(username);
        userLogin.setEmail(email);
    }

    public void selectUser(String username,final String userLoginId)
    {
        int flag = 0;
        index = 0;
        if(userLoginId.equals(username))
        {
            usernameTxt.setText("User Not Found");
        }
        else
        {
            for(int i=0 ;i < users.size() ;i++)
            {
                if(username.toLowerCase().equals(users.get(i).getUsername().toLowerCase()))
                {
                    flag = 1;
                    index = i;
                    break;
                }
                else
                {
                    if(users.get(i).getUsername().toLowerCase().contains(username.toLowerCase()))
                    {
                        flag = 2;
                        index = i;
                        break;
                    }
                }
            }
            if(flag == 1)
            {
                usernameTxt.setText(users.get(index).getUsername());
                new DownLoadImageTask(profileImage).execute(users.get(index).getProfileImage());
                addBtn.setVisibility(View.VISIBLE);
                profileImage.setVisibility(View.VISIBLE);
                usernameTxt.setVisibility(View.VISIBLE);
                searchTxt.setText("");
            }
            else if(flag == 2)
            {
                usernameTxt.setText(users.get(index).getUsername());
                new DownLoadImageTask(profileImage).execute(users.get(index).getProfileImage());
                addBtn.setVisibility(View.VISIBLE);
                profileImage.setVisibility(View.VISIBLE);
                usernameTxt.setVisibility(View.VISIBLE);
                searchTxt.setText("");
            }
            else if(flag == 0)
            {
                addBtn.setVisibility(View.INVISIBLE);
                profileImage.setVisibility(View.INVISIBLE);
                usernameTxt.setVisibility(View.VISIBLE);
                usernameTxt.setText("User Not Found");
            }
        }
    }

    public void seletAllUserAfterAdd()
    {
        userListView.setAdapter(null);
        ListUserAdapter listUserAdapter = new ListUserAdapter(getActivity().getApplicationContext());
        for(int i=0;i< users.size();i++)
        {
            listUserAdapter.add(users.get(i).getUsername(),users.get(i).getProfileImage());
        }
        userListView.setAdapter(listUserAdapter);
    }

    public void selectAllUser()
    {
        final Query queryUsers = databaseReference.child("users");
        queryUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "User Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ListUserAdapter listUserAdapter = new ListUserAdapter(getActivity().getApplicationContext());
                    for(DataSnapshot userSnapShot : dataSnapshot.getChildren())
                    {
                        Users user = userSnapShot.getValue(Users.class);
                        user.setUserId(userSnapShot.getKey());
                        users.add(user);
                        listUserAdapter.add(user.getUsername(),user.getProfileImage());
                    }
                    userListView.setAdapter(listUserAdapter);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void selectAllFriend()
    {
        final Query queryFriend = databaseReference.child("friend").orderByChild("userAddId").equalTo(userLogin.getUserId());
        queryFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Friend Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for(DataSnapshot friendSnapShot : dataSnapshot.getChildren())
                    {
                        Friend friend = friendSnapShot.getValue(Friend.class);
                        friends.add(friend);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addFriendById(String userId)
    {
        for(int i=0;i<users.size();i++)
        {
            if(users.get(i).getUserId().equals(userId))
            {
                String id = databaseReference.push().getKey();
                Friend friend = new Friend(userLogin.getUserId(),users.get(i).getUserId(),users.get(i).getUsername(),users.get(i).getProfileImage());
                databaseReference.child("friend").child(id).setValue(friend);
                users.remove(i);
                userListView.setAdapter(null);
                selectAfterAddIcon();
                break;
            }
        }
    }

    public void selectAfterAddIcon()
    {
        ListUserAdapter listUserAdapter = new ListUserAdapter(getActivity().getApplicationContext());
       for(int i=0;i<users.size();i++)
       {
           listUserAdapter.add(users.get(i).getUsername(),users.get(i).getProfileImage());
       }
       userListView.setAdapter(listUserAdapter);
    }

    public void selectByUserAndFriend()
    {
        count = 0;
        final Query queryFriend = databaseReference.child("friend").orderByChild("userAddId").equalTo(userLogin.getUserId());
        queryFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() < 1)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "Friend Empty", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for(DataSnapshot friendSnapShot : dataSnapshot.getChildren()) {

                        final Friend friend = friendSnapShot.getValue(Friend.class);
                        friends.add(friend);
                        final Query queryUser = databaseReference.child("users");
                        queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount() < 1) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Friend Empty", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (DataSnapshot userSnapShot : dataSnapshot.getChildren()) {
                                        if (count == 0) {
                                            Users user = userSnapShot.getValue(Users.class);
                                            user.setUserId(userSnapShot.getKey());
                                            users.add(user);
                                        }
                                    }
                                    if(count == 0)
                                    {
                                        for(int i=0;i<friends.size();i++)
                                        {
                                            for(int j=0;j<users.size();j++)
                                            {
                                                if(friends.get(i).getFriendId().equals(users.get(j).getUserId()))
                                                {
                                                    users.remove(j);
                                                }
                                            }
                                        }
                                        //setToListVIew
                                        ListUserAdapter listUserAdapter = new ListUserAdapter(getActivity().getApplicationContext());
                                        for(int i=0;i<users.size();i++)
                                        {
                                            listUserAdapter.add(users.get(i).getUsername(),users.get(i).getProfileImage());
                                            listUserAdapter.setCustomButtonListner(new ListFriendAdapter.customButtonListener() {
                                                @Override
                                                public void onButtonClickListner(int position, String value) {
                                                    addFriendById(users.get(position).getUserId());
                                                }
                                            });
                                        }

                                        userListView.setAdapter(listUserAdapter);
                                    }
                                    count++;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onButtonClickListner(int position, String value) {
        Toast.makeText(getActivity().getApplicationContext(), value, Toast.LENGTH_SHORT).show();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
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
                Intent intent = new Intent(getActivity(),UserNavigationActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v)
    {
        if(v == addBtn)
        {
            String id = databaseReference.push().getKey();
            Friend friend = new Friend(userLogin.getUserId(),users.get(index).getUserId(),users.get(index).getUsername(),users.get(index).getProfileImage());
            databaseReference.child("friend").child(id).setValue(friend);
            users.remove(index);
            index = 0;
            addBtn.setVisibility(View.INVISIBLE);
            profileImage.setVisibility(View.INVISIBLE);
            usernameTxt.setVisibility(View.INVISIBLE);
            seletAllUserAfterAdd();
        }
    }
}
