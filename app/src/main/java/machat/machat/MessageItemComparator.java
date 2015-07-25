package machat.machat;

import java.util.Comparator;

/**
 * Created by Admin on 7/24/2015.
 */
public class MessageItemComparator implements Comparator<Message> {
    @Override
    public int compare(Message lhs, Message rhs) {
        return Long.valueOf(lhs.getTime()).compareTo(Long.valueOf(rhs.getTime()));
    }
}
