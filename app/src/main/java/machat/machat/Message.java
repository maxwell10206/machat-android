package machat.machat;

import android.graphics.Bitmap;

import machat.machat.socketIO.TimeConvert;

/**
 * Created by Admin on 6/14/2015.
 */
public class Message extends User implements Comparable<Message>{

    public static final int NOT_SENT = 0;
    public static final int SENT = 1;
    public static final int DELIVERED = 2;
    public static final int READ = 3;

    private String message;

    private long time;

    private int id;

    private int houseId;

    private Bitmap avatar;

    //0 = not sent, 1 = sent, 2 = delivered, 3 = seen
    private int status = 0;

    private int localId = -1;

    private String houseName;

    public Message(int houseId, int userId, int id, String username, String name, String message, long time, int status, String houseName){
        super(userId, username, name);
        this.message = message;
        this.houseId = houseId;
        this.time = time;
        this.id = id;
        this.status = status;
        this.houseName = houseName;
    }

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

    public String getHouseName(){ return houseName; }

    public int getLocalId(){ return localId; }

    public void setLocalId(int id){ localId = id; }

    public int getStatus(){ return status; }

    public void setStatus(int status){ this.status = status; }

    public void setMessage(String message){
        this.message = message;
    }

    public void setTime(long time){
        this.time = time;
    }

    public void setAvatar(Bitmap bitmap){ this.avatar = bitmap; }

    public Bitmap getAvatar(){ return avatar; }

    public int getHouseId(){ return houseId; }

    public int getMessageId(){ return id; }

    public String getMessage(){ return message; }

    public long getTime(){ return time; }

    public String getTimeString(){
        return TimeConvert.formatDate(time * 1000);
    }

    @Override
    public int compareTo(Message another) {
        return Integer.valueOf(id).compareTo(Integer.valueOf(another.id));
    }
}
