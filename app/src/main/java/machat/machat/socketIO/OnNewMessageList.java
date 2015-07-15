package machat.machat.socketIO;

import java.util.ArrayList;

import machat.machat.Message;

/**
 * Created by Admin on 6/14/2015.
 */
public interface OnNewMessageList {

    void addOldMessages(ArrayList<Message> messageList);

    void addNewMessages(ArrayList<Message> messageList);

    void getMessageListFailed(String err);
}
