package machat.machat;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import machat.machat.R;
import machat.machat.db.FavoriteListDbAdapter;
import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.OnCallbackAvatar;

/**
 * Created by Admin on 7/17/2015.
 */
public class FavoriteListCursorAdapter extends CursorAdapter {

    private FavoriteListActivity favoriteListActivity;

    private int myId = 0;

    public void setMyId(int id){ myId = id;}

    public FavoriteListCursorAdapter(FavoriteListActivity favoriteListActivity, Cursor cursor){
        super(favoriteListActivity, cursor, 0);
        this.favoriteListActivity = favoriteListActivity;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
    }

    private String getTimeString(long time){
        long now = System.currentTimeMillis();
        if(time == 0) {
            return "";
        }else if((now - time*1000) < 60000) {
            return "just now";
        }else{
            return DateUtils.getRelativeTimeSpanString(time * 1000, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
        }
    }

    @Override
    public void bindView(View rowView, Context context, Cursor cursor) {
        TextView nameView = (TextView) rowView.findViewById(R.id.username);
        TextView lastMessageView = (TextView) rowView.findViewById(R.id.last_message);
        TextView messageTimeView = (TextView) rowView.findViewById(R.id.message_time);
        ImageView muteView = (ImageView) rowView.findViewById(R.id.mute);
        final ImageView avatarView = (ImageView) rowView.findViewById(R.id.avatar);

        int houseId = cursor.getInt(cursor.getColumnIndex(FavoriteListDbAdapter.COLUMN_NAME_HOUSE_ID));
        String name = cursor.getString(cursor.getColumnIndex(FavoriteListDbAdapter.COLUMN_NAME_NAME));
        String message = cursor.getString(cursor.getColumnIndex(FavoriteListDbAdapter.COLUMN_NAME_MESSAGE));
        long time = cursor.getLong(cursor.getColumnIndex(FavoriteListDbAdapter.COLUMN_NAME_TIME));
        int status = cursor.getInt(cursor.getColumnIndex(FavoriteListDbAdapter.COLUMN_NAME_MSG_STATUS));
        int userId = cursor.getInt(cursor.getColumnIndex(FavoriteListDbAdapter.COLUMN_USER_ID));

        AvatarManager.getAvatar(houseId, new OnCallbackAvatar() {
            @Override
            public void newAvatar(int id, final Bitmap bitmap) {
                favoriteListActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatarView.setImageBitmap(bitmap);
                    }
                });
            }
        });

        nameView.setText(name);
        if(message.isEmpty()){
            lastMessageView.setText("No messages");
        }else {
            lastMessageView.setText(message);
        }

        messageTimeView.setText(getTimeString(time));

        ImageView statusView = (ImageView) rowView.findViewById(R.id.status);
        if (userId == myId) {
            statusView.setImageResource(Message.getStatusImageId(status));
        } else {
            statusView.setImageResource(R.drawable.ic_play_arrow_black_18dp);
        }

        /*/
        if(favoriteItem.isBlock()){
            lastMessage.setText("BLOCKED");
        }

        if(!favoriteItem.isRead()){
            ImageView notRead = (ImageView) rowView.findViewById(R.id.notRead);
            notRead.setImageResource(R.drawable.blue_circle);
        }

        if(favoriteItem.isMute()){
            mute.setImageResource(R.drawable.ic_volume_mute_black_24dp);
        }else{
            mute.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
        /*/
    }
}
