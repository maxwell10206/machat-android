package machat.machat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
 * Created by Admin on 7/8/2015.
 */
public class BlockListArrayAdapter extends ArrayAdapter {

    private ArrayList<User> blockList;

    private Activity activity;

    public BlockListArrayAdapter(Activity activity, ArrayList<User> blockList) {
        super(activity, -1, blockList);
        this.blockList = blockList;
        this.activity = activity;
    }

    @Override
    public User getItem (int position){
        return blockList.get(position);
    }

    public void removeById(int id){
        for(int i = 0; i < blockList.size(); i++){
            if(blockList.get(i).getUserId() == id){
                remove(blockList.get(i));
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_search, parent, false);

        User user = blockList.get(position);

        TextView usernameView = (TextView) rowView.findViewById(R.id.username);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        final ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar);

        usernameView.setText(user.getUsername());
        nameView.setText(user.getName());

        AvatarManager.getAvatar(user.getUserId(), new OnCallbackAvatar() {
            @Override
            public void newAvatar(int id, final Bitmap bitmap) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatar.setImageBitmap(bitmap);
                    }
                });
            }
        });

        return rowView;
    }

}
