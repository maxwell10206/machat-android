package machat.machat.socketIO;

import java.util.ArrayList;

import machat.machat.FavoriteItem;

/**
 * Created by Admin on 6/12/2015.
 */
public interface OnNewFavoriteList {

    void newFavoriteList(ArrayList<FavoriteItem> favoriteItems);

    void getFavoriteListFailed(String err);

}
