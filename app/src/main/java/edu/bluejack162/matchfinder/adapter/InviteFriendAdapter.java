package edu.bluejack162.matchfinder.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import edu.bluejack162.matchfinder.R;
import edu.bluejack162.matchfinder.model.Friend;

/**
 * Created by giono on 7/7/2017.
 */

public class InviteFriendAdapter extends BaseAdapter{

    private Context mContext;
    private Vector<Friend> friends;

    //Init
    public InviteFriendAdapter(Context mContext, Vector<Friend> friends){
        this.mContext = mContext;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Object getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.friend_list, null);
        TextView fName = (TextView) v.findViewById(R.id.tvFriendId);

        //Set Data
        fName.setText(friends.get(position).getFrienName());

        //Save friend id to tag
        v.setTag(friends.get(position).getFriendId());

        return v;
    }
}
