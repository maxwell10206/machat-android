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

    private int messageId;

    private String message;

    private int status;

    private String name;

    private long time;

    @PrimaryKey
    private int userId;

    private int messageUserId;

    public static String getTimeString(long time) {
        long now = System.currentTimeMillis();
        if (time == 0) {
            return "";
        } else if ((now - time * 1000) < 60000) {
            return "just now";
        } else {
            return DateUtils.getRelativeTimeSpanString(time * 1000, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
        }
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getMessageUserId() {
        return messageUserId;
    }

    public void setMessageUserId(int messageUserId) {
        this.messageUserId = messageUserId;
    }

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

}
