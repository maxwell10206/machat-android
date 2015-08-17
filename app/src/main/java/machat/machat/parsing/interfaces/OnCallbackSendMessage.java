package machat.machat.parsing.interfaces;

import machat.machat.models.Message;

public interface OnCallbackSendMessage {

    void sendMessageSuccess(Message message);

    void sendMessageFailed(String err);

}
