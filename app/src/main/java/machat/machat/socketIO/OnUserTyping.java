package machat.machat.socketIO;

import machat.machat.User;

/**
 * Created by Admin on 7/4/2015.
 */
public interface OnUserTyping {

    void newUserTyping(int houseId, User user);

    void userStoppedTyping(int houseId, User user);
}
