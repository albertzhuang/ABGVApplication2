package edu.bluejack162.matchfinder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack162.matchfinder.R;

/**
 * Created by alber on 04/07/2017.
 */
public class ListFriendAdapter extends BaseAdapter{

    Context context;
    ArrayList<String> username;
    ArrayList<String> profileImage;
    customButtonListener customListener;

    public ArrayList<String> getUsername() {
        return username;
    }

    public void setUsername(ArrayList<String> username) {
        this.username = username;
    }

    public ArrayList<String> getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ArrayList<String> profileImage) {
        this.profileImage = profileImage;
    }

    public interface customButtonListener
    {
        public void onButtonClickListner(int position,String value);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListener = listener;
    }

    public ListFriendAdapter(Context context)
    {
        this.context = context;
        username = new ArrayList<String>();
        profileImage = new ArrayList<String>();
    }

    public void deleteItem(int position)
    {
        username.remove(position);
        profileImage.remove(position);
    }

    @Override
    public int getCount()
    {
        return username.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.row_friend,parent,false);

        CircleImageView rowProfilePicture = (CircleImageView) convertView.findViewById(R.id.rowProfilePictureId);
        TextView rowUsername = (TextView) convertView.findViewById(R.id.rowUsernameId);
        ImageView deleteIcon = (ImageView) convertView.findViewById(R.id.rowIconDeleteFriendId);

        new DownLoadImageTask(rowProfilePicture).execute(profileImage.get(position));
        rowUsername.setText(username.get(position));

        //setListener
        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customListener != null)
                {
                    customListener.onButtonClickListner(position,"test");
                    username.remove(position);
                    profileImage.remove(position);
                }
            }
        });
        return convertView;
    }

    public void add(String name,String profile)
    {
        username.add(name);
        profileImage.add(profile);
    }

    public class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        public CircleImageView imageView;

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
