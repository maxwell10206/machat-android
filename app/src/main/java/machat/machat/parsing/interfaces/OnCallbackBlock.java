package machat.machat.parsing.interfaces;

import machat.machat.models.BlockUser;

public interface OnCallbackBlock {

    void unBlocked(int id);

    void blocked(BlockUser blockUser);

}
