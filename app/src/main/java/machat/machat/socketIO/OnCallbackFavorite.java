package machat.machat.socketIO;

import machat.machat.FavoriteItem;

/**
 * Created by Admin on 6/21/2015.
 */
public interface OnCallbackFavorite {

    void removeFavorite(int id);

    void newFavorite(FavoriteItem favoriteItem);

    void favoriteError(String err);

}
