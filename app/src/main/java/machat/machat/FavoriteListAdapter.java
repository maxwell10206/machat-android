package machat.machat;

import android.content.Context;
import android.graphics.Bitmap;
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

    public void removeById(int id){
        for(int i = 0; i < favoriteItems.size(); i++){
            if(favoriteItems.get(i).getUserId() == id){
                remove(favoriteItems.get(i));
            }
        }
    }

    public void setMuteById(int id, boolean mute){
        for(int i = 0; i < favoriteItems.size(); i++) {
            if (favoriteItems.get(i).getUserId() == id) {
                favoriteItems.get(i).setMute(mute);
            }
        }
        notifyDataSetChanged();
    }

    public void setBitmapById(int id, Bitmap bitmap){
        for(int i = 0; i < favoriteItems.size(); i++){
            if(favoriteItems.get(i).getUserId() == id){
                favoriteItems.get(i).setAvatar(bitmap);
            }
        }
        notifyDataSetChanged();
    }

    public void setBlockById(int id, boolean block){
        for(int i = 0; i < favoriteItems.size(); i++){
            if(favoriteItems.get(i).getUserId() == id){
                favoriteItems.get(i).setBlock(block);
            }
        }
        notifyDataSetChanged();
    }

    public void changeNameById(int id, String name){
        for(int i = 0; i < favoriteItems.size(); i++){
            if(favoriteItems.get(i).getUserId() == id){
                favoriteItems.get(i).setName(name);
            }
        }
        notifyDataSetChanged();
    }

    public void setReadHouseById(int id, boolean read) {
        for(int i = 0; i < favoriteItems.size(); i++){
            Message message = favoriteItems.get(i).getMessage();
            if(message.getHouseId() == id){
                favoriteItems.get(i).setRead(read);
            }
        }
        notifyDataSetChanged();
    }

    public void setReadMessageById(int messageId) {
        for (int i = 0; i < favoriteItems.size(); i++) {
            Message message = favoriteItems.get(i).getMessage();
            if (message.getMessageId() == messageId) {
                favoriteItems.get(i).getMessage().setStatus(Message.READ);
            }
        }
        notifyDataSetChanged();
    }

    public void setMessageById(Message message){
        for(int i = 0; i < favoriteItems.size(); i++){
            if(favoriteItems.get(i).getUserId() == message.getHouseId()){
                FavoriteItem favoriteItem = favoriteItems.get(i);
                favoriteItem.setMessage(message);
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
        final ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar);

        FavoriteItem favoriteItem = favoriteItems.get(position);
        Message message = favoriteItem.getMessage();
        name.setText(favoriteItem.getName());
        if(message.getMessage().isEmpty()){
            lastMessage.setText("No messages");
        }else {
            lastMessage.setText(message.getMessage());
        }
        messageTime.setText(favoriteItem.getTimeString());

        ImageView status = (ImageView) rowView.findViewById(R.id.status);
        if (favoriteItem.getMessage().getUserId() == myId) {
            status.setImageResource(favoriteItem.getMessage().getStatusImageId());
        } else {
            status.setImageResource(R.drawable.ic_play_arrow_black_18dp);
        }

        if(favoriteItem.isBlock()){
            lastMessage.setText("BLOCKED");
        }

        if(!favoriteItem.isRead()){
            ImageView notRead = (ImageView) rowView.findViewById(R.id.notRead);
            notRead.setImageResource(R.drawable.blue_circle);
        }

        AvatarManager.getAvatar(favoriteItem.getUserId(), new OnCallbackAvatar() {
            @Override
            public void newAvatar(int id, final Bitmap bitmap) {
                favoriteListActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatar.setImageBitmap(bitmap);
                    }
                });
            }
        });

        if(favoriteItem.isMute()){
            mute.setImageResource(R.drawable.ic_volume_mute_black_24dp);
        }else{
            mute.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }

        return rowView;
    }

}
