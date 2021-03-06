package machat.machat.util;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class BitmapUser extends RealmObject {

    @PrimaryKey
    private int id;

    private byte[] avatar;

    private long time;

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

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
}
