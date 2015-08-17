package machat.machat.parsing.interfaces;

import machat.machat.models.FavoriteItem;

public interface OnCallbackFavorite {

    void removeFavorite(int id);

    void newFavorite(FavoriteItem favoriteItem);

    void favoriteError(String err);

}
