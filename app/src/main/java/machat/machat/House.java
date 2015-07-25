package machat.machat;

import io.realm.RealmObject;

/**
 * Created by Admin on 6/21/2015.
 */
public class House extends RealmObject {

    private boolean favorite;

    private boolean mute;

    private User user;

    public void setUser(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }
    public boolean isMute(){ return mute; }

    public void setMute(boolean mute){ this.mute = mute; }

    public boolean isFavorite(){ return favorite; }

    public void setFavorite(boolean favorite){
        this.favorite = favorite;
    }
}
