package edu.bluejack162.matchfinder.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.bluejack162.matchfinder.R;
import edu.bluejack162.matchfinder.model.Event;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateEventFragment extends Fragment implements View.OnClickListener{

    EditText locationTxt;
    EditText eventNameTxt;
    Button searchBtn;
    Button addEventBtn;
    Button inviteFriendBtn;
    int PLACE_PICKER_REQUEST = 1;
    private Double lat, lng;
    private DatabaseReference db;

    private Event event;

    public void init()
    {
        locationTxt = (EditText) getActivity().findViewById(R.id.locationTxtId);
        eventNameTxt = (EditText) getActivity().findViewById(R.id.eventName);
        searchBtn = (Button) getActivity().findViewById(R.id.searchBtnId);
        db = FirebaseDatabase.getInstance().getReference();
        addEventBtn = (Button) getActivity().findViewById(R.id.addEventBtnId);
        inviteFriendBtn = (Button) getActivity().findViewById(R.id.inviteFriendBtnId);

        //setListener
        searchBtn.setOnClickListener(this);
        addEventBtn.setOnClickListener(this);
        inviteFriendBtn.setOnClickListener(this);
    }


    public CreateEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_event, container, false);
    }


    @Override
    public void onClick(View v) {
        if(v == searchBtn)
        {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
        else if(v == addEventBtn){
            if(!eventNameTxt.getText().toString().equals("") && !locationTxt.getText().toString().equals("")){
                addCurrentEvent(eventNameTxt.getText().toString(), lat, lng, LoginActivity.getUserKey().toString());
                Toast.makeText(getContext(), "Adding event", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(), "Add Event Error", Toast.LENGTH_SHORT).show();

        }
        else if(v == inviteFriendBtn){
            Toast.makeText(getContext(), "invite Friend", Toast.LENGTH_SHORT).show();
            //String = db.child("friendList").
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == PLACE_PICKER_REQUEST)
        {
            Place place = PlacePicker.getPlace(data, getActivity());
            locationTxt.setText(place.getName());
            //Latitude Longitude
            lat =  place.getLatLng().latitude;
            lng = place.getLatLng().longitude;
            Toast.makeText(getContext(), "Lat : "+lat+" Lng : "+lng, Toast.LENGTH_SHORT).show();
        }
    }

    private void addCurrentEvent(String eventName, Double lat, Double lng, String creator){
        try{
            String key = db.child("events").push().getKey();
            event = new Event(eventName, lat, lng, creator);
            db.child("events").child(key).setValue(event);
        }catch(Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();}

    }



    private void inviteFriend(){

    }
}
