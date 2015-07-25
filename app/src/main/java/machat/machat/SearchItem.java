package machat.machat;

import io.realm.RealmObject;

/**
 * Created by Admin on 7/7/2015.
 */
public class SearchItem extends RealmObject {

    private boolean block;

    private User user;

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    public void setBlock(boolean block){ this.block = block; }

    public boolean isBlock(){ return block; }
}
