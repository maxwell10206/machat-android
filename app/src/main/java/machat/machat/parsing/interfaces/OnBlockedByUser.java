package machat.machat.parsing.interfaces;

public interface OnBlockedByUser {

    void blockedBy(int userId);

    void unBlockedBy(int userId);

}
