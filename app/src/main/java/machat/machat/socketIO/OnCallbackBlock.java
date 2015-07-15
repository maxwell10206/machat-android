package machat.machat.socketIO;

/**
 * Created by Admin on 6/21/2015.
 */
public interface OnCallbackBlock {

    public final static String blockId = "blockId";

    public final static String block = "block";

    void callbackBlock(int id, boolean block);

}
