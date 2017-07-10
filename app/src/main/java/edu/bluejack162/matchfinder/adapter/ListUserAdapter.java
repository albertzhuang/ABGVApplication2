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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack162.matchfinder.R;

/**
 * Created by alber on 09/07/2017.
 */

public class ListUserAdapter extends BaseAdapter{

    Context context;
    ArrayList<String> usernames;
    ArrayList<String> profileImages;
    ListFriendAdapter.customButtonListener customListener;

    public ListUserAdapter(Context context) {
        this.context = context;
        usernames = new ArrayList<String>();
        profileImages = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return usernames.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void add(String name,String profile)
    {
        usernames.add(name);
        profileImages.add(profile);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.row_user,parent,false);

        CircleImageView rowProfilePicture = (CircleImageView) convertView.findViewById(R.id.rowProfilePictureId);
        TextView rowUsername = (TextView) convertView.findViewById(R.id.rowUsernameId);
        ImageView deleteIcon = (ImageView) convertView.findViewById(R.id.rowIconDeleteFriendId);

        new DownLoadImageTask(rowProfilePicture).execute(profileImages.get(position));
        rowUsername.setText(usernames.get(position));

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customListener != null)
                {
                    customListener.onButtonClickListner(position,"position");
                }
            }
        });
        return convertView;
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

    public void setCustomButtonListner(ListFriendAdapter.customButtonListener listener) {
        this.customListener = listener;
    }

    public interface customButtonListener
    {
        public void onButtonClickListner(int position,String value);
    }
}
