package machat.machat;

import android.graphics.Bitmap;

/**
 * Created by Admin on 6/22/2015.
 */
public class MyProfile{

    private int id;

    private String username;

    private String name;

    private String email;

    private String sessionId;

    private Bitmap avatar;

    public MyProfile(int id, String username, String name, String email, String sessionId){
        this.id = id;
        this.username = username;
        this.name = name;
        this.sessionId = sessionId;
        this.email = email;
    }

    public Bitmap getAvatar(){ return avatar;}

    public void setAvatar(Bitmap avatar){ this.avatar = avatar; }

    public int getId(){ return id; }

    public String getName(){ return name;}

    public void setName(String name){ this.name = name;}

    public String getUsername(){ return username; }

    public String getSessionId(){ return sessionId; }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
