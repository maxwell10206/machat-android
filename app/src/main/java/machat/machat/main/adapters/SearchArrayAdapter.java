package machat.machat.main.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import machat.machat.R;
import machat.machat.models.SearchItem;
import machat.machat.util.AvatarManager;
import machat.machat.parsing.interfaces.OnCallbackAvatar;

public class SearchArrayAdapter extends ArrayAdapter<SearchItem> {

    ArrayList<SearchItem> users;

    Activity activity;

    public SearchArrayAdapter(Activity activity, ArrayList<SearchItem> users) {
        super(activity, -1, users);
        this.users = users;
        this.activity = activity;
    }


    @Override
    public SearchItem getItem(int position) {
        return users.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_search, parent, false);

        SearchItem searchItem = users.get(position);

        TextView usernameView = (TextView) rowView.findViewById(R.id.username);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        final ImageView avatarView = (ImageView) rowView.findViewById(R.id.avatar);

        usernameView.setText(searchItem.getUsername());
        nameView.setText(searchItem.getName());

        AvatarManager.getAvatar(searchItem.getId(), new OnCallbackAvatar() {
            @Override
            public void newAvatar(int id, final byte[] avatar, long time) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatarView.setImageBitmap(BitmapFactory.decodeByteArray(avatar, 0, avatar.length));
                    }
                });
            }
        });

        return rowView;
    }

}
