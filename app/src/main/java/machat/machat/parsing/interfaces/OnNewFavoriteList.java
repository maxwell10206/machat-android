package machat.machat.parsing.interfaces;

import java.util.ArrayList;

import machat.machat.models.FavoriteItem;

public interface OnNewFavoriteList {

    void newFavoriteList(ArrayList<FavoriteItem> favoriteItems);

    void getFavoriteListFailed(String err);

}
