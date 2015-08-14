package machat.machat;

import io.realm.RealmObject;

/**
 * Created by Admin on 7/7/2015.
 */
public class SearchItem extends RealmObject {

    private boolean block;

    private int id;

    private String username;

    private String name;

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

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }
}
