package machat.machat;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Admin on 6/12/2015.
 */
@SuppressWarnings("serial")
public class User implements Serializable {

    private int id;

    private String username;

    private String name;

    private Bitmap avatar;

    public User(int id, String username, String name){
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public Bitmap getAvatar(){ return avatar; }
    public void setAvatar(Bitmap bitmap){ this.avatar = bitmap;}

    public int getUserId(){ return id; }
    public String getUsername(){ return username; }
    public String getName(){ return name; }

    public void setName(String name){
        this.name = name;
    }
}
