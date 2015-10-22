package machat.machat.main.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import machat.machat.main.activities.HouseActivity;
import machat.machat.models.Message;
import machat.machat.models.MessageItemComparator;
import machat.machat.R;
import machat.machat.util.AvatarManager;
import machat.machat.parsing.interfaces.OnCallbackAvatar;

public class HouseArrayAdapter extends ArrayAdapter<Message> {

    private ArrayList<Message> messageList = new ArrayList<>();

    private HouseActivity houseActivity;

    public HouseArrayAdapter(HouseActivity houseActivity, ArrayList<Message> messageList) {
        super(houseActivity, -1, messageList);
        this.messageList = messageList;
        this.houseActivity = houseActivity;
    }

    public void replaceByLocalId(int localId, Message message) {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getLocalId() == localId) {
                messageList.set(i, message);
            }
        }
        notifyDataSetChanged();
    }

    public void changeMessageStatus(int id, int status) {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getId() == id) {
                int oldStatus = messageList.get(i).getStatus();
                if (oldStatus < status) {
                    houseActivity.realm.beginTransaction();
                    messageList.get(i).setStatus(status);
                    houseActivity.realm.commitTransaction();
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        //do your sorting here
        Collections.sort(messageList, new MessageItemComparator());

        super.notifyDataSetChanged();
    }

    @Override
    public Message getItem(int position) {
        return messageList.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) houseActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;

        final Message message = messageList.get(position);
        if (houseActivity.getMyId() == message.getUserId()) {
            rowView = inflater.inflate(R.layout.message_right, parent, false);
            ProgressBar statusView = (ProgressBar) rowView.findViewById(R.id.status);
            int status = message.getStatus();
            if(status != Message.NOT_SENT) {
                statusView.setIndeterminateDrawable(houseActivity.getResources().getDrawable(Message.getStatusImageId(status)));
            }
        } else {
            rowView = inflater.inflate(R.layout.message_left, parent, false);
            TextView nameView = (TextView) rowView.findViewById(R.id.username);
            nameView.setText(message.getName());
            final ImageView avatarView = (ImageView) rowView.findViewById(R.id.avatar);

            AvatarManager.getAvatar(message.getUserId(), new OnCallbackAvatar() {
                @Override
                public void newAvatar(int id, final byte[] avatar, long time) {
                    houseActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            avatarView.setImageBitmap(BitmapFactory.decodeByteArray(avatar, 0, avatar.length));
                        }
                    });
                }
            });
            avatarView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (message.getHouseId() != message.getUserId()) {
                        houseActivity.goToHouse(message.getUserId(), message.getName(), message.getUsername());
                    }
                }
            });

        }

        final TextView messageView = (TextView) rowView.findViewById(R.id.message);
        messageView.setText(message.getMessage());
        TextView timeView = (TextView) rowView.findViewById(R.id.time);
        timeView.setText(Message.getTimeString(message.getTime()));

        View messageWrapper = rowView.findViewById(R.id.messageWrapper);
        messageWrapper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                houseActivity.createMessageDialog(getItem(position));
                return true;
            }
        });

        if(message.getStatus() == Message.FAILED_TO_SEND) {
            messageWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    houseActivity.createMessageResendDialog(getItem(position));
                }
            });
        }

        return rowView;
    }

}
