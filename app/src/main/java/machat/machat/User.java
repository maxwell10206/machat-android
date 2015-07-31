package machat.machat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Admin on 6/12/2015.
 */
@SuppressWarnings("serial")
public class User extends RealmObject implements Serializable {

    @PrimaryKey
    private int id;

    private String username;

    private String name;

    private byte[] avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public static Bitmap getBitmapAvatar(byte[] avatar){
        return BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
    }
}
