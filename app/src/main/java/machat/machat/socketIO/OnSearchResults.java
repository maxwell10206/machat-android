package machat.machat.socketIO;

import java.util.ArrayList;

import machat.machat.SearchItem;

/**
 * Created by Admin on 6/26/2015.
 */
public interface OnSearchResults {

    void newSearchResults(ArrayList<SearchItem> users);

}
