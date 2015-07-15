package machat.machat.socketIO;

import machat.machat.Message;

/**
 * Created by Admin on 6/14/2015.
 */
public interface OnNewMessage {

    void newMessage(Message message);
}
