package machat.machat.parsing.interfaces;

import java.util.ArrayList;

import machat.machat.models.MissedMessage;

public interface OnUndeliveredMessages {

    void undeliveredMessages(ArrayList<MissedMessage> messages);

}
