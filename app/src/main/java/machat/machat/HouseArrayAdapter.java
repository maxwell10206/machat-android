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
 * Created by Admin on 6/14/2015.
 */
public class HouseArrayAdapter extends ArrayAdapter {

    private ArrayList<Message> messageList = new ArrayList<>();

    private HouseActivity houseActivity;

    public HouseArrayAdapter(HouseActivity houseActivity, ArrayList<Message> messageList) {
        super(houseActivity, -1, messageList);
        this.messageList = messageList;
        this.houseActivity = houseActivity;
    }

    public void setBitmapById(int id, Bitmap bitmap){
        for(int i = 0; i < messageList.size(); i++){
            if(messageList.get(i).getUserId() == id){
                messageList.get(i).setAvatar(bitmap);
            }
        }
        notifyDataSetChanged();
    }

    public void replaceByLocalId(int localId, Message message){
        for(int i = 0; i < messageList.size(); i++){
            if(messageList.get(i).getLocalId() == localId){
                messageList.set(i, message);
            }
        }
        notifyDataSetChanged();
    }

    public void changeMessageStatus(int id, int status){
        for(int i = 0; i < messageList.size(); i++){
            if(messageList.get(i).getMessageId() == id){
                int oldStatus = messageList.get(i).getStatus();
                if(oldStatus < status) {
                    messageList.get(i).setStatus(status);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        //do your sorting here
        Collections.sort(messageList);

        super.notifyDataSetChanged();
    }

    @Override
    public Message getItem (int position){
        return messageList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) houseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        final Message message = messageList.get(position);
        if(houseActivity.myProfile().getId() == message.getUserId()){
            rowView = inflater.inflate(R.layout.message_right, parent, false);
            TextView statusView = (TextView) rowView.findViewById(R.id.status);
            statusView.setText(message.getStatusString());
        }else {
            rowView = inflater.inflate(R.layout.message_left, parent, false);
            TextView nameView = (TextView) rowView.findViewById(R.id.username);
            nameView.setText(message.getName());
            final ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar);
            AvatarManager.getAvatar(message.getUserId(), new OnCallbackAvatar() {
                @Override
                public void newAvatar(int id,final Bitmap bitmap) {
                    houseActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            avatar.setImageBitmap(bitmap);
                        }
                    });
                }
            });
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (message.getHouseId() != message.getUserId()) {
                        houseActivity.goToHouse(message.getUserId());
                    }
                }
            });

        }

        final TextView messageView = (TextView) rowView.findViewById(R.id.message);
        messageView.setText(message.getMessage());
        TextView timeView = (TextView) rowView.findViewById(R.id.time);
        timeView.setText(message.getTimeString());

        View messageWrapper = rowView.findViewById(R.id.messageWrapper);
        messageWrapper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                houseActivity.createMessageDialog(getItem(position));
                return true;
            }
        });

        return rowView;
    }

}
