package machat.machat.socketIO;

import machat.machat.Message;

/**
 * Created by Admin on 6/29/2015.
 */
public interface OnCallbackSendMessage {

    void sendMessageSuccess(Message message);

    void sendMessageFailed(String err);

}
