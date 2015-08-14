package machat.machat.socketIO;

import java.util.ArrayList;

import machat.machat.BlockUser;

/**
 * Created by Admin on 7/8/2015.
 */
public interface OnCallbackBlockList {

    void newBlockList(ArrayList<BlockUser> blockedUsers);
}
