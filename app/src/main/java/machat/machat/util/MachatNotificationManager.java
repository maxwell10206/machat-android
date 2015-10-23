package machat.machat.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import machat.machat.R;
import machat.machat.main.activities.FavoriteListActivity;
import machat.machat.main.activities.HouseActivity;
import machat.machat.models.MissedMessage;

public class MachatNotificationManager {

    private SocketService service;

    private ArrayList<MissedMessage> messageList = new ArrayList<>();

    private NotificationManager mNotificationManager;

    public MachatNotificationManager(SocketService service) {
        this.service = service;
        this.mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void clearNotification(int id) {
        mNotificationManager.cancel(id);
        ArrayList<MissedMessage> tempMessages = new ArrayList<>();
        for (int i = 0; i < messageList.size(); i++) {
            MissedMessage message = messageList.get(i);
            if (message.getHouseId() == id) {
                tempMessages.add(message);
            }
        }
        messageList.removeAll(tempMessages);
    }

    public void newMissedMessages(int houseId, int myId, String houseName, String name, String message, int missedMessages, boolean add) {
        boolean exist = false;
        MissedMessage missedMessage = new MissedMessage(houseId, houseName, name, message, missedMessages);
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).getHouseId() == houseId) {
                if (add) {
                    missedMessages += messageList.get(i).getMissedMessages();
                }
                missedMessage.setMissedMessages(missedMessages);
                messageList.remove(i);
                messageList.add(missedMessage);
                exist = true;
            }
        }
        if (!exist) {
            messageList.add(missedMessage);
        }
        if (missedMessages > 1) {
            createMultiMessageNotify(houseId, myId, houseName, name, missedMessages, message);
        } else {
            createMessageNotify(houseId, myId, houseName, name, message);
        }
    }

    private void createMessageNotify(int houseId, int myId, String houseName, String name, String message) {
        Notification mBuilder = new Notification.Builder(service)
                .setContentText(name.substring(0, Math.min(12, name.length())) + ": " + message)
                .setContentTitle(houseName)
                .setSmallIcon(R.drawable.ic_home_white_48pt_3x)
                .getNotification();

        createHouseNotification(houseId, myId, houseName, mBuilder);
    }

    private void createMultiMessageNotify(int houseId, int myId, String houseName, String name, int missedMessages, String message) {
        Notification mBuilder = new Notification.Builder(service)
                .setContentText(missedMessages + " messages: ..." + message)
                .setContentTitle(houseName)
                .setSmallIcon(R.drawable.ic_home_white_48pt_3x)
                .getNotification();

        createHouseNotification(houseId, myId, houseName, mBuilder);
    }

    private void createHouseNotification(int houseId, int myId, String houseName, Notification notification) {
        Intent resultIntent = new Intent(service, HouseActivity.class)
                .putExtra(HouseActivity.HOUSE_ID, houseId)
                .putExtra(HouseActivity.MY_ID, myId)
                .putExtra(HouseActivity.HOUSE_NAME, houseName)
                .putExtra(HouseActivity.FAVORITE, service.favorites.getFavorite(houseId))
                .putExtra(HouseActivity.MUTE, service.favorites.getMute(houseId));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service);

        Intent favoriteListIntent = new Intent(service, FavoriteListActivity.class);
        favoriteListIntent.putExtra(FavoriteListActivity.MY_ID, myId);
        stackBuilder.addNextIntent(favoriteListIntent);

// Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

// Gets a PendingIntent containing the entire back stack
        notification.contentIntent = stackBuilder.getPendingIntent(houseId, PendingIntent.FLAG_ONE_SHOT);

        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(service);
        boolean soundNotify = sharedPreferences.getBoolean(service.getString(R.string.pref_key_sound), true);
        boolean vibrateNotify = sharedPreferences.getBoolean(service.getString(R.string.pref_key_vibrate), true);
        boolean blinkLED = sharedPreferences.getBoolean(service.getString(R.string.pref_key_blinkLED), true);
        if (soundNotify) {
            notification.defaults |= Notification.DEFAULT_SOUND;
        }
        if (vibrateNotify) {
            notification.defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (blinkLED) {
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
            notification.ledARGB = 0xffFFA500;
            notification.ledOnMS = 300;
            notification.ledOffMS = 1000;
        }

        boolean notify = sharedPreferences.getBoolean(service.getString(R.string.pref_key_receive_notifications), true);
        if (notify) {
            mNotificationManager.notify(houseId, notification);
        }

    }

}
