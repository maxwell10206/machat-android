package machat.machat;

import io.realm.RealmObject;

/**
 * Created by Admin on 6/20/2015.
 */
public class Profile extends RealmObject {

    private boolean blocked;

    private User user;

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public boolean isBlocked(){ return blocked; }

    public void setBlocked(boolean isBlocked){
        this.blocked = isBlocked;
    }
}
