package machat.machat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.OnCallbackAvatar;

/**
 * Created by Admin on 6/7/2015.
 */
public class FavoriteListAdapter extends ArrayAdapter {

    private FavoriteListActivity favoriteListActivity;

    private ArrayList<FavoriteItem> favoriteItems;

    private int myId = 0;

    public void setMyId(int id){ myId = id;}

    @Override
    public FavoriteItem getItem (int position){
        return favoriteItems.get(position);
    }

    public FavoriteListAdapter(FavoriteListActivity favoriteListActivity, ArrayList<FavoriteItem> favoriteItems) {
        super(favoriteListActivity, -1, favoriteItems);
        this.favoriteListActivity = favoriteListActivity;
        this.favoriteItems = favoriteItems;
    }

    public void setBitmapById(int id, byte[] avatar){
        for(int i = 0; i < favoriteItems.size(); i++){
            if(favoriteItems.get(i).getUserId() == id) {
                favoriteItems.get(i).setAvatar(avatar);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        //do your sorting here
        Collections.sort(favoriteItems, new FavoriteItemComparator());

        super.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) favoriteListActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_favorite, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.username);
        TextView lastMessage = (TextView) rowView.findViewById(R.id.last_message);
        TextView messageTime = (TextView) rowView.findViewById(R.id.message_time);
        ImageView mute = (ImageView) rowView.findViewById(R.id.mute);
        final ImageView avatarView = (ImageView) rowView.findViewById(R.id.avatar);

        final FavoriteItem favoriteItem = favoriteItems.get(position);
        name.setText(favoriteItem.getName());

        if(favoriteItem.getMessage().isEmpty()){
            lastMessage.setText("No messages");
        }else {
            lastMessage.setText(favoriteItem.getMessage());
        }
        messageTime.setText(FavoriteItem.getTimeString(favoriteItem.getTime()));

        ImageView status = (ImageView) rowView.findViewById(R.id.status);
        if (favoriteItem.getMessageUserId() == myId) {
            status.setImageResource(Message.getStatusImageId(favoriteItem.getStatus()));
        } else {
            status.setImageResource(R.drawable.ic_play_arrow_black_18dp);
        }

        if (favoriteItem.isBlock()){
            lastMessage.setText("BLOCKED");
        }

        if(!favoriteItem.isRead()){
            ImageView notRead = (ImageView) rowView.findViewById(R.id.notRead);
            notRead.setImageResource(R.drawable.blue_circle);
        }

        byte[] imageByteArray = favoriteItem.getAvatar();
        if(imageByteArray != null && imageByteArray.length != 0) {
            avatarView.setImageBitmap(User.getBitmapAvatar(imageByteArray));
        }
        AvatarManager.getAvatar(favoriteItem.getUserId(), new OnCallbackAvatar() {
            @Override
            public void newAvatar(int id, final byte[] avatar, long time) {
                favoriteListActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatarView.setImageBitmap(User.getBitmapAvatar(avatar));
                    }
                });
            }
        });

        if(favoriteItem.isMute()){
            mute.setImageResource(R.drawable.ic_volume_mute_black_24dp);
        } else {
            mute.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }

        return rowView;
    }

}
