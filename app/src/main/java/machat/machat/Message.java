package machat.machat;

import android.graphics.Bitmap;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import machat.machat.socketIO.TimeConvert;

/**
 * Created by Admin on 6/14/2015.
 */
public class Message extends RealmObject{

    public static final int NOT_SENT = 0;
    public static final int SENT = 1;
    public static final int DELIVERED = 2;
    public static final int READ = 3;

    private String message;

    private long time;

    private int id;

    private int houseId;

    @Ignore
    private Bitmap avatar;

    //0 = not sent, 1 = sent, 2 = delivered, 3 = seen
    private int status = 0;

    private int localId = -1;

    private String houseName;

    private User user;

    public User getUser(){ return user; }

    public void setUser(User user){ this.user = user; }

    public static int getStatusImageId(int status) {
        switch(status){
            case SENT:
                return R.drawable.ic_done_black_18dp;
            case DELIVERED:
                return R.drawable.ic_done_black_18dp;
            case READ:
                return R.drawable.ic_done_all_black_18dp;
            default:
                return R.drawable.ic_access_time_black_18dp;
        }
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHouseId() {
        return houseId;
    }

    public void setHouseId(int houseId) {
        this.houseId = houseId;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLocalId() {
        return localId;
    }

    public void setLocalId(int localId) {
        this.localId = localId;
    }

    public static String getTimeString(long time){
        return TimeConvert.formatDate(time * 1000);
    }

}
