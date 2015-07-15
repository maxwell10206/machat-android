package machat.machat.socketIO;

/**
 * Created by Admin on 7/9/2015.
 */
public interface OnBlockedByUser {

    void blockedBy(int userId);

    void unBlockedBy(int userId);

}
