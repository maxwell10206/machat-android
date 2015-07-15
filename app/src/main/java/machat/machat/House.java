package machat.machat;

/**
 * Created by Admin on 6/21/2015.
 */
public class House extends User {

    private boolean isFavorite;

    private boolean isMute;

    public House(int id, String username, String name, boolean isFavorite, boolean isMute){
        super(id, username, name);
        this.isFavorite = isFavorite;
        this.isMute = isMute;
    }

    public boolean isMute(){ return isMute; }

    public void setMute(boolean mute){ isMute = mute; }

    public boolean isFavorite(){ return isFavorite; }

    public void setFavorite(boolean isFavorite){
        this.isFavorite = isFavorite;
    }
}
