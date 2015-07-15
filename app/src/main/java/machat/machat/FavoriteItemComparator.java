package machat.machat;

import java.util.Comparator;

/**
 * Created by Admin on 6/29/2015.
 */
public class FavoriteItemComparator implements Comparator<FavoriteItem> {
    @Override
    public int compare(FavoriteItem lhs, FavoriteItem rhs) {
        if (lhs.isHeader()) return -1;
        if (rhs.isHeader()) return 1;
        return Long.valueOf(rhs.getMessage().getTime()).compareTo(Long.valueOf(lhs.getMessage().getTime()));
    }
}