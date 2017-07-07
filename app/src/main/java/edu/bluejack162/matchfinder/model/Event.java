package edu.bluejack162.matchfinder.model;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Vector;

/**
 * Created by giono on 7/7/2017.
 */

public class Event {
    public String eventName;
    public Double lat;
    public Double lng;
    public String creator;
    public Vector<String> members = new Vector<String>();
    public String date;

    public Event(){

    }

    public Event(String eventName, Double lat, Double lng, String creator){
        this.lat = lat;
        this.lng = lng;
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        date = sdf.format(now);
        //date = "Senin";
        this.creator = creator;
        members.add(creator);
    }

    public void addMember(String member){
        members.add(member);
    }
}
