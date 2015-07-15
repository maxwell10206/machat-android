package machat.machat.socketIO;

import java.util.ArrayList;

import machat.machat.MissedMessage;

/**
 * Created by Admin on 7/6/2015.
 */
public interface OnUndeliveredMessages {

    void undeliveredMessages(ArrayList<MissedMessage> messages);

}
