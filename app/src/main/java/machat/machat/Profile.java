package machat.machat;

/**
 * Created by Admin on 6/20/2015.
 */
public class Profile extends User {

    private boolean isBlocked;

    public Profile(int id, String username, String name, boolean isBlocked){
        super(id, username, name);
        this.isBlocked = isBlocked;
    }

    public Boolean isBlocked(){ return isBlocked; }

    public void setBlocked(boolean isBlocked){
        this.isBlocked = isBlocked;
    }
}
