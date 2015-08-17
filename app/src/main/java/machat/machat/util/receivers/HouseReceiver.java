package machat.machat.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import machat.machat.models.FavoriteItem;
import machat.machat.main.MachatApplication;
import machat.machat.models.Message;
import machat.machat.models.MissedMessage;
import machat.machat.util.SocketService;
import machat.machat.parsing.interfaces.OnCallbackSendMessage;
import machat.machat.parsing.interfaces.OnNewMessage;
import machat.machat.parsing.interfaces.OnNewMessageList;
import machat.machat.parsing.interfaces.OnUndeliveredMessages;
import machat.machat.conf.SocketCommand;
import machat.machat.parsing.SocketParse;

public class HouseReceiver extends BroadcastReceiver implements OnNewMessage, OnCallbackSendMessage, OnNewMessageList, OnUndeliveredMessages {

    private SocketService mService;

    private Realm realm;

    private int houseId = 0;

    private MachatApplication application;

    public HouseReceiver(SocketService mService) {
        this.mService = mService;
        this.application = (MachatApplication) mService.getApplication();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra(SocketService.COMMAND);
        String data = intent.getStringExtra(SocketService.DATA);
        switch(command){
            case SocketCommand.NEW_MESSAGE:
                SocketParse.parseMessage(data, this);
                return;
            case SocketCommand.SEND_MESSAGE:
                SocketParse.parseSendMessage(data, this);
                return;
            case SocketCommand.GET_NEW_MESSAGES:
                SocketParse.parseMessageList(SocketCommand.GET_NEW_MESSAGES, data, this);
                return;
            case SocketCommand.GET_OLD_MESSAGES:
                SocketParse.parseMessageList(SocketCommand.GET_OLD_MESSAGES, data, this);
                return;
            case SocketCommand.GET_UNDELIVERED_MESSAGES:
                SocketParse.parseUndeliveredMessages(data, this);
                return;
            case SocketCommand.LOGOUT:
                clearAllMessages();
                return;
        }
    }

    public void setHouseId(int id) {
        houseId = id;
    }

    @Override
    public void sendMessageSuccess(Message message) {

    }

    @Override
    public void sendMessageFailed(String err) {

    }

    public void clearAllMessages() {
        RealmResults<Message> results = realm.where(Message.class).findAll();
        realm.beginTransaction();
        results.clear();
        realm.commitTransaction();
    }

    @Override
    public void newMessage(Message message) {

        boolean mute = false;
        for (int i = 0; i < mService.favorites.getFavoritesList().size(); i++) {
            FavoriteItem favoriteItem = mService.favorites.getFavoritesList().get(i);
            if (favoriteItem.getUserId() == message.getHouseId()) {
                mute = favoriteItem.isMute();
            }
        }
        if (!application.isActivityVisible() && houseId != message.getHouseId() && !mute) {
            mService.machatNotificationManager.newMissedMessages(message.getHouseId(), mService.user.getMyProfile().getId(), message.getHouseName(), message.getName(), message.getMessage(), 1, true);
        }
        if (!mute) {
            mService.send.deliveredMessage(message.getId());
        }

        if (houseId == 0) {
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
        if(messageList.size() >= 20){
            RealmResults<Message> results = realm.where(Message.class).equalTo("houseId", messageList.get(0).getHouseId()).findAll();
            results.clear();
        }
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
            if (!application.isActivityVisible() && houseId != message.getHouseId()) {
                mService.machatNotificationManager.newMissedMessages(message.getHouseId(), mService.user.getMyProfile().getId(), message.getHouseName(), message.getName(), message.getMessage(), message.getMissedMessages(), false);
            }
        }
    }
}
