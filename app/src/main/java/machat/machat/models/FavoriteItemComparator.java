package machat.machat.models;

import java.util.Comparator;

public class FavoriteItemComparator implements Comparator<FavoriteItem> {
    @Override
    public int compare(FavoriteItem lhs, FavoriteItem rhs) {
        if (lhs.isHeader()) return -1;
        if (rhs.isHeader()) return 1;
        return Long.valueOf(rhs.getTime()).compareTo(lhs.getTime());
    }
}