package machat.machat;

/**
 * Created by Admin on 7/7/2015.
 */
public class SearchItem extends User {

    private boolean block;

    public SearchItem(int id, String username, String name, boolean block){
        super(id, username, name);
        this.block = block;
    }

    public boolean isBlock(){ return block; }
}
