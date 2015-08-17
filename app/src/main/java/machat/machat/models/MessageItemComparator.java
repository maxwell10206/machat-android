package machat.machat.models;

import java.util.Comparator;

public class MessageItemComparator implements Comparator<Message> {
    @Override
    public int compare(Message lhs, Message rhs) {
        return Long.valueOf(lhs.getTime()).compareTo(rhs.getTime());
    }
}
