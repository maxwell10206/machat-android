package machat.machat.parsing.interfaces;

import machat.machat.models.Message;

public interface OnNewMessage {

    void newMessage(Message message);
}
