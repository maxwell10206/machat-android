package machat.machat;

import android.text.format.DateUtils;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Admin on 6/13/2015.
 */
@SuppressWarnings("serial")
public class FavoriteItem extends RealmObject implements Serializable {

    private boolean mute;

    private boolean header = false;

    private boolean read = true;

    private boolean block;

    private Message message;

    @PrimaryKey
    private int primaryKey;

    public void setPrimaryKey(int primaryKey) {
        this.primaryKey = primaryKey;
    }

    public int getPrimaryKey(){ return primaryKey; }

    private User user;

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static String getTimeString(long time){
        long now = System.currentTimeMillis();
        if(time == 0) {
            return "";
        }else if((now - time*1000) < 60000) {
            return "just now";
        }else{
            return DateUtils.getRelativeTimeSpanString(time * 1000, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
        }
    }

}
