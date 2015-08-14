package machat.machat.socketIO;

import machat.machat.BlockUser;

/**
 * Created by Admin on 6/21/2015.
 */
public interface OnCallbackBlock {

    void unBlocked(int id);

    void blocked(BlockUser blockUser);

}
