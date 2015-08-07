package machat.machat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.OnCallbackAvatar;

/**
 * Created by Admin on 6/28/2015.
 */
public class SearchArrayAdapter extends ArrayAdapter {

    ArrayList<SearchItem> users;

    Activity activity;

    public SearchArrayAdapter(Activity activity, ArrayList<SearchItem> users) {
        super(activity, -1, users);
        this.users = users;
        this.activity = activity;
    }

    public void setBitmapById(int id, byte[] avatar){
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUser().getId() == id){
                users.get(i).getUser().setAvatar(avatar);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public SearchItem getItem (int position){
        return users.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_search, parent, false);

        User user = users.get(position).getUser();

        TextView usernameView = (TextView) rowView.findViewById(R.id.username);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        final ImageView avatarView = (ImageView) rowView.findViewById(R.id.avatar);

        usernameView.setText(user.getUsername());
        nameView.setText(user.getName());

        AvatarManager.getAvatar(user.getId(), new OnCallbackAvatar() {
            @Override
            public void newAvatar(int id, final byte[] avatar, long time) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatarView.setImageBitmap(User.getBitmapAvatar(avatar));
                    }
                });
            }
        });

        return rowView;
    }

}
