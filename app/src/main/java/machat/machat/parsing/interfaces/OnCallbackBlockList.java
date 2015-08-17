package machat.machat.parsing.interfaces;

import java.util.ArrayList;

import machat.machat.models.BlockUser;

public interface OnCallbackBlockList {

    void newBlockList(ArrayList<BlockUser> blockedUsers);
}
