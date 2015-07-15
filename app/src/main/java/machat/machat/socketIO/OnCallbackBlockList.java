package machat.machat.socketIO;

import java.util.ArrayList;

import machat.machat.User;

/**
 * Created by Admin on 7/8/2015.
 */
public interface OnCallbackBlockList {

    void newBlockList(ArrayList<User> blockedUsers);
}
