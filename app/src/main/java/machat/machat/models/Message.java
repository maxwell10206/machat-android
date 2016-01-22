package machat.machat.models;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import machat.machat.R;
import machat.machat.tools.TimeConvert;

public class Message extends RealmObject implements Serializable {

    public static final int NOT_SENT = 0;
    public static final int SENT = 1;
    public static final int DELIVERED = 2;
    public static final int READ = 3;
    public static final int FAILED_TO_SEND = 4;

    private String message;

    private long time;

    private int id;

    private int houseId;

    //0 = not sent, 1 = sent, 2 = delivered, 3 = seen
    private int status = 0;

    @PrimaryKey
    private int dbId = -1;

    private String houseName;

    private int userId;

    private String name;

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static int getStatusImageId(int status) {
        switch (status) {
            case SENT:
                return R.drawable.ic_done_black_18dp;
            case DELIVERED:
                return R.drawable.ic_done_black_18dp;
            case READ:
                return R.drawable.ic_done_all_black_18dp;
            case FAILED_TO_SEND:
                return R.drawable.ic_report_problem_black_18dp;
            default:
                return R.drawable.ic_access_time_black_18dp;
        }
    }

    public static String getTimeString(long time) {
        return TimeConvert.formatDate(time * 1000);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDbId() {
        return dbId;
    }

    public void setDbId(int dbId) {
        this.dbId = dbId;
    }

}
