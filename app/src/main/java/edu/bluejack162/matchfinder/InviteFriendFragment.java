package edu.bluejack162.matchfinder;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Vector;

import edu.bluejack162.matchfinder.adapter.InviteFriendAdapter;
import edu.bluejack162.matchfinder.adapter.ListFriendAdapter;
import edu.bluejack162.matchfinder.model.Friend;
import edu.bluejack162.matchfinder.model.Users;


public class InviteFriendFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView lvInviteFriend;
    private InviteFriendAdapter adapter;
    private Vector<Friend> friends;
    private FirebaseDatabase fireRoot;
    private DatabaseReference databaseReference;
    Users userLogIn;


    public void init(){
        fireRoot = FirebaseDatabase.getInstance();
        friends = new Vector<Friend>();
        adapter = new InviteFriendAdapter(getContext(), friends);
        lvInviteFriend = (ListView) getActivity().findViewById(R.id.listFriendId);
        lvInviteFriend.setAdapter(adapter);

        //setListener
        lvInviteFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite_friend, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getFriendData(){
        DatabaseReference ref = fireRoot.getReference("friendList");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    Toast.makeText(getContext(), ds.getValue().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
