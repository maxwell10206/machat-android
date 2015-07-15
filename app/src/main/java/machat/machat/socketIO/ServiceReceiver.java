package machat.machat.socketIO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;

import machat.machat.FavoriteItem;
import machat.machat.MachatApplication;
import machat.machat.Message;
import machat.machat.MissedMessage;
import machat.machat.MyProfile;
import machat.machat.SavedPrefs;
import machat.machat.SocketService;

/**
 * Created by Admin on 6/28/2015.
 */
public class ServiceReceiver extends BroadcastReceiver implements OnNewMessage, OnChangeEmail, OnChangeName, OnUndeliveredMessages, OnLoginListener, OnCallbackFavorite, OnNewFavoriteList{

    private SocketService mService;

    private MachatApplication application;

    private SharedPreferences sharedPref;

    private MyProfile myProfile;

    private boolean login = false;

    private int houseId = 0;

    public void setHouseId(int id){
        houseId = id;
    }

    private ArrayList<FavoriteItem> favoritesList = new ArrayList<>();

    public ArrayList<FavoriteItem> getFavoritesList(){ return favoritesList; }

    public boolean isLogin() {
        return login;
    }

    public ServiceReceiver(SocketService mService){
        this.application = (MachatApplication)mService.getApplication();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mService);
        this.mService = mService;
    }

    public MyProfile getMyProfile() {
        if (myProfile == null) {
            int id = sharedPref.getInt(SavedPrefs.id, 0);
            String username = sharedPref.getString(SavedPrefs.username, "");
            String sessionId = sharedPref.getString(SavedPrefs.sessionId, "");
            String name = sharedPref.getString(SavedPrefs.name, "");
            String email = sharedPref.getString(SavedPrefs.email, "");

            return new MyProfile(id, username, name, email, sessionId);
        } else {
            return myProfile;
        }
    }

    @Override
    public void onLoginSuccess(MyProfile myProfile) {
        login = true;
        this.myProfile = myProfile;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SavedPrefs.username, myProfile.getUsername());
        editor.putString(SavedPrefs.sessionId, myProfile.getSessionId());
        editor.putString(SavedPrefs.name, myProfile.getName());
        editor.putInt(SavedPrefs.id, myProfile.getId());
        editor.putString(SavedPrefs.email, myProfile.getEmail());
        editor.commit();
        mService.send.getUndeliveredMessages();
        mService.send.getFavoriteList();
    }

    @Override
    public void onLoginFailed(String err) {
        login = false;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(SavedPrefs.sessionId);
        editor.commit();
    }

    public void logout(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(SavedPrefs.sessionId);
        editor.commit();
        login = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra(SocketService.COMMAND);
        String data = intent.getStringExtra(SocketService.DATA);
        if(command.equals(SocketCommand.NEW_MESSAGE)){
            SocketParse.parseMessage(data, this);
        }else if(command.equals(SocketCommand.GET_UNDELIVERED_MESSAGES)){
            SocketParse.parseUndeliveredMessages(data, this);
        }else if(command.equals(SocketCommand.LOGOUT)){
            logout();
        }else if(command.equals(Socket.EVENT_CONNECT)){
            String sessionId = sharedPref.getString(SavedPrefs.sessionId, "");
            if (!sessionId.isEmpty()) {
                mService.send.loginSession(sessionId);
            }
        }else if(command.equals(SocketCommand.LOGIN)){
            SocketParse.parseLogin(data, this);
        }else if(command.equals(SocketCommand.FAVORITE_HOUSE)){
            SocketParse.parseFavoriteHouse(data, this);
        }else if(command.equals(SocketCommand.GET_FAVORITE_LIST)){
            SocketParse.parseFavoriteList(data, this);
        }else if(command.equals(SocketCommand.LOGIN)){
            SocketParse.parseLogin(data, this);
        }else if(command.equals(SocketCommand.CHANGE_NAME)){
            SocketParse.parseChangeName(data, this);
        }else if(command.equals(SocketCommand.CHANGE_EMAIL)){
            SocketParse.parseChangeEmail(data, this);
        }
    }

    @Override
    public void newMessage(Message message) {
        boolean mute = false;
        for(int i = 0; i < favoritesList.size(); i++){
            FavoriteItem favoriteItem = favoritesList.get(i);
            if(favoriteItem.getUserId() == message.getHouseId()){
                mute = favoriteItem.isMute();
            }
        }
        if(!application.isActivityVisible() && houseId != message.getHouseId() && !mute) {
            mService.machatNotificationManager.newMissedMessages(message.getHouseId(), getMyProfile().getId(), message.getHouseName(), message.getName(), message.getMessage(), 1, true);
        }
        if(!mute) {
            mService.send.deliveredMessage(message.getMessageId());
        }
    }

    @Override
    public void undeliveredMessages(ArrayList<MissedMessage> messages) {
        for (int i = 0; i < messages.size(); i++) {
            MissedMessage message = messages.get(i);
            if(!application.isActivityVisible() && houseId != message.getHouseId()) {
                mService.machatNotificationManager.newMissedMessages(message.getHouseId(), getMyProfile().getId(), message.getHouseName(), message.getName(), message.getMessage(), message.getMissedMessages(), false);
            }
        }
    }

    @Override
    public void newFavoriteList(ArrayList<FavoriteItem> favoriteItems) {
        favoritesList = favoriteItems;
    }

    @Override
    public void getFavoriteListFailed(String err) {

    }

    @Override
    public void removeFavorite(int id) {
        for(int i = 0; i < favoritesList.size(); i++){
            FavoriteItem favoriteItem = favoritesList.get(i);
            if(favoriteItem.getUserId() == id){
                favoritesList.remove(i);
            }
        }
    }

    @Override
    public void newFavorite(FavoriteItem favoriteItem) {
        favoritesList.add(favoriteItem);
    }

    @Override
    public void favoriteError(String err) {

    }

    public void setFavoriteMute(int id, boolean mute) {
        for(int i = 0; i < favoritesList.size(); i++){
            FavoriteItem favoriteItem = favoritesList.get(i);
            if(favoriteItem.getUserId() == id){
                favoritesList.get(i).setMute(mute);
            }
        }
    }

    @Override
    public void changeEmailSuccess(String email) {
        myProfile.setEmail(email);
    }

    @Override
    public void changeEmailFailed(String err) {

    }

    @Override
    public void changeNameSuccess(String name) {
        myProfile.setName(name);
    }

    @Override
    public void changeNameFailed(String err) {

    }
}
