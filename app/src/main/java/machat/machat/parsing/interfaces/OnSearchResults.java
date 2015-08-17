package machat.machat.parsing.interfaces;

import java.util.ArrayList;

import machat.machat.models.SearchItem;

public interface OnSearchResults {

    void newSearchResults(ArrayList<SearchItem> users);

}
