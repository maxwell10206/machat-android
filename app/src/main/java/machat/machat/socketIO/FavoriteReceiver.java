package machat.machat.socketIO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import machat.machat.FavoriteItem;
import machat.machat.Message;
import machat.machat.SocketService;

/**
 * Created by Maxwell on 8/8/2015.
 */
public class FavoriteReceiver extends BroadcastReceiver implements OnNewFavoriteList, OnBlockedByUser, OnUserReadMessage, OnReadHouse, OnCallbackFavorite, OnNewMessage, OnCallbackSendMessage {


    private SocketService mService;

    private Realm realm;

    private ArrayList<FavoriteItem> favoritesList = new ArrayList<>();

    public FavoriteReceiver(SocketService mService) {
        this.mService = mService;
        this.realm = Realm.getDefaultInstance();
        this.favoritesList.addAll(realm.where(FavoriteItem.class).findAll());
    }

    public boolean getMute(int userId) {
        for (FavoriteItem favoriteItem : favoritesList) {
            if (userId == favoriteItem.getUserId()) {
                return favoriteItem.isMute();
            }
        }
        return false;
    }

    public boolean getFavorite(int userId) {
        for (int i = 0; i < favoritesList.size(); i++) {
            if (userId == favoritesList.get(i).getUserId()) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<FavoriteItem> getFavoritesList() {
        return favoritesList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra(SocketService.COMMAND);
        String data = intent.getStringExtra(SocketService.DATA);
        if (command.equals(SocketCommand.GET_FAVORITE_LIST)) {
            SocketParse.parseFavoriteList(data, this);
        } else if (command.equals(SocketCommand.FAVORITE_HOUSE)) {
            SocketParse.parseFavoriteHouse(data, this);
        } else if (command.equals(SocketCommand.SEND_MESSAGE)) {
            SocketParse.parseSendMessage(data, this);
        } else if (command.equals(SocketCommand.NEW_MESSAGE)) {
            SocketParse.parseMessage(data, this);
        } else if (command.equals(SocketCommand.READ_MESSAGE)) {
            SocketParse.parseUserReadMessage(data, this);
        } else if (command.equals(SocketCommand.READ_HOUSE)) {
            SocketParse.parseReadHouse(data, this);
        } else if (command.equals(SocketCommand.LOGOUT)) {
            clearAllFavorites();
        }
    }

    @Override
    public void removeFavorite(int id) {
        for (int i = 0; i < favoritesList.size(); i++) {
            FavoriteItem favoriteItem = favoritesList.get(i);
            if (favoriteItem.getUserId() == id) {
                realm.beginTransaction();
                favoriteItem.removeFromRealm();
                favoritesList.remove(i);
                realm.commitTransaction();
            }
        }
    }

    public void setFavoriteMute(int id, boolean mute) {
        for (int i = 0; i < favoritesList.size(); i++) {
            FavoriteItem favoriteItem = favoritesList.get(i);
            if (favoriteItem.getUserId() == id) {
                realm.beginTransaction();
                favoritesList.get(i).setMute(mute);
                realm.commitTransaction();
            }
        }
    }

    @Override
    public void newFavorite(FavoriteItem favoriteItem) {
        realm.beginTransaction();
        favoritesList.add(realm.copyToRealmOrUpdate(favoriteItem));
        realm.commitTransaction();
    }

    @Override
    public void favoriteError(String err) {

    }

    @Override
    public void sendMessageSuccess(Message message) {
        realm.beginTransaction();
        for (int i = 0; i < favoritesList.size(); i++) {
            FavoriteItem favoriteItem = favoritesList.get(i);
            if (favoriteItem.getUserId() == message.getHouseId()) {
                favoriteItem.setMessage(message.getMessage());
                favoriteItem.setMessageId(message.getId());
                favoriteItem.setMessageUserId(message.getUserId());
                favoriteItem.setStatus(message.getStatus());
                favoriteItem.setTime(message.getTime());
            }
        }
        realm.commitTransaction();
    }

    @Override
    public void sendMessageFailed(String err) {

    }

    public void clearAllFavorites() {
        RealmResults<FavoriteItem> results = realm.where(FavoriteItem.class).findAll();
        realm.beginTransaction();
        for (int i = 0; i < results.size(); i++) {
            results.get(i).removeFromRealm();
        }
        realm.commitTransaction();


    }

    @Override
    public void newFavoriteList(ArrayList<FavoriteItem> favoriteItems) {
        realm.beginTransaction();
        for (int i = 0; i < favoriteItems.size(); i++) {
            FavoriteItem favoriteItem = favoriteItems.get(i);
            if (favoriteItem.getUserId() == mService.user.getMyProfile().getId()) {
                favoriteItem.setHeader(true);
            }
        }
        RealmResults<FavoriteItem> results = realm.where(FavoriteItem.class).findAll();
        for (int i = 0; i < results.size(); i++) {
            results.get(i).removeFromRealm();
        }
        List<FavoriteItem> realmFavoriteItems = realm.copyToRealmOrUpdate(favoriteItems);
        realm.commitTransaction();

        favoritesList.clear();
        favoritesList.addAll(realmFavoriteItems);
    }

    @Override
    public void getFavoriteListFailed(String err) {

    }

    @Override
    public void newMessage(Message message) {
        realm.beginTransaction();
        for (FavoriteItem favoriteItem : favoritesList) {
            if (favoriteItem.getUserId() == message.getHouseId()) {
                favoriteItem.setMessage(message.getMessage());
                favoriteItem.setMessageId(message.getId());
                favoriteItem.setMessageUserId(message.getUserId());
                favoriteItem.setStatus(message.getStatus());
                favoriteItem.setTime(message.getTime());
                favoriteItem.setRead(false);
            }
        }
        realm.commitTransaction();
    }

    @Override
    public void readHouse(int houseId) {
        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).getUserId() == houseId) {
                realm.beginTransaction();
                favoritesList.get(i).setRead(true);
                realm.commitTransaction();
            }
        }
    }

    @Override
    public void userReadMessage(int messageId) {
        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).getMessageId() == messageId) {
                realm.beginTransaction();
                favoritesList.get(i).setStatus(Message.READ);
                realm.commitTransaction();
            }
        }
    }

    @Override
    public void blockedBy(int userId) {
        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).getUserId() == userId) {
                realm.beginTransaction();
                favoritesList.get(i).setBlock(true);
                realm.commitTransaction();
            }
        }
    }

    @Override
    public void unBlockedBy(int userId) {
        for (int i = 0; i < favoritesList.size(); i++) {
            if (favoritesList.get(i).getUserId() == userId) {
                realm.beginTransaction();
                favoritesList.get(i).setBlock(false);
                realm.commitTransaction();
            }
        }
    }
}
