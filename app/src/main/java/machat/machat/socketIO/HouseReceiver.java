package machat.machat.socketIO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import machat.machat.FavoriteItem;
import machat.machat.MachatApplication;
import machat.machat.Message;
import machat.machat.MissedMessage;
import machat.machat.SocketService;

/**
 * Created by Maxwell on 8/8/2015.
 */
public class HouseReceiver extends BroadcastReceiver implements OnNewMessage, OnCallbackSendMessage, OnNewMessageList, OnUndeliveredMessages{

    private SocketService mService;

    private Realm realm;

    private int houseId = 0;

    private MachatApplication application;

    public HouseReceiver(SocketService mService){
        this.mService = mService;
        this.application = (MachatApplication)mService.getApplication();
        realm = Realm.getInstance(mService);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra(SocketService.COMMAND);
        String data = intent.getStringExtra(SocketService.DATA);
        if (command.equals(SocketCommand.NEW_MESSAGE)) {
            SocketParse.parseMessage(data, this);
        }else if (command.equals(SocketCommand.SEND_MESSAGE)) {
            SocketParse.parseSendMessage(data, this);
        } else if (command.equals(SocketCommand.GET_NEW_MESSAGES)) {
            SocketParse.parseMessageList(SocketCommand.GET_NEW_MESSAGES, data, this);
        } else if(command.equals(SocketCommand.GET_OLD_MESSAGES)){
            SocketParse.parseMessageList(SocketCommand.GET_OLD_MESSAGES, data, this);
        } else if(command.equals(SocketCommand.GET_UNDELIVERED_MESSAGES)){
            SocketParse.parseUndeliveredMessages(data, this);
        } else if(command.equals(SocketCommand.LOGOUT)){
            clearAllMessages();
        }
    }

    public void setHouseId(int id){
        houseId = id;
    }

    @Override
    public void sendMessageSuccess(Message message) {

    }

    @Override
    public void sendMessageFailed(String err) {

    }

    public void clearAllMessages(){
        RealmResults<Message> results = realm.where(Message.class).findAll();
        realm.beginTransaction();
        for(int i = 0; i < results.size(); i++){
            results.get(i).removeFromRealm();
        }
        realm.commitTransaction();
    }

    @Override
    public void newMessage(Message message) {

        boolean mute = false;
        for(int i = 0; i < mService.favorites.getFavoritesList().size(); i++){
            FavoriteItem favoriteItem = mService.favorites.getFavoritesList().get(i);
            if(favoriteItem.getUserId() == message.getHouseId()){
                mute = favoriteItem.isMute();
            }
        }
        if(!application.isActivityVisible() && houseId != message.getHouseId() && !mute) {
            mService.machatNotificationManager.newMissedMessages(message.getHouseId(), mService.user.getMyProfile().getId(), message.getHouseName(), message.getName(), message.getMessage(), 1, true);
        }
        if(!mute) {
            mService.send.deliveredMessage(message.getId());
        }

        if(houseId == 0) {
            int newestMessageId = 0;
            RealmResults<Message> results = realm.where(Message.class).equalTo("houseId", message.getHouseId()).findAll();
            for (int i = 0; i < results.size(); i++) {
                Message dbMessage = results.get(i);
                if (dbMessage.getId() > newestMessageId) {
                    newestMessageId = dbMessage.getId();
                }
            }
            mService.send.getNewMessages(message.getHouseId(), newestMessageId);
        }

    }

    @Override
    public void addOldMessages(ArrayList<Message> messageList) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(messageList);
        realm.commitTransaction();
    }

    @Override
    public void addNewMessages(ArrayList<Message> messageList) {
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(messageList);
        realm.commitTransaction();
    }

    @Override
    public void getMessageListFailed(String err) {

    }

    @Override
    public void undeliveredMessages(ArrayList<MissedMessage> messages) {
        for (int i = 0; i < messages.size(); i++) {
            MissedMessage message = messages.get(i);
            if(!application.isActivityVisible() && houseId != message.getHouseId()) {
                mService.machatNotificationManager.newMissedMessages(message.getHouseId(), mService.user.getMyProfile().getId(), message.getHouseName(), message.getName(), message.getMessage(), message.getMissedMessages(), false);
            }
        }
    }
}
