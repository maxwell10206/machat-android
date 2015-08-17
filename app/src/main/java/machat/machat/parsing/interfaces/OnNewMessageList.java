package machat.machat.parsing.interfaces;

import java.util.ArrayList;

import machat.machat.models.Message;

public interface OnNewMessageList {

    void addOldMessages(ArrayList<Message> messageList);

    void addNewMessages(ArrayList<Message> messageList);

    void getMessageListFailed(String err);
}
