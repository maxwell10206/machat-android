package machat.machat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.util.ArrayList;

/**
 * Created by Admin on 6/26/2015.
 */
public class MachatNotificationManager {

    private SocketService service;

    private ArrayList<MissedMessage> messageList = new ArrayList<>();

    private NotificationManager mNotificationManager;

    public MachatNotificationManager(SocketService service){
        this.service = service;
        this.mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void clearNotification(int id){
        mNotificationManager.cancel(id);
        ArrayList<MissedMessage> tempMessages = new ArrayList<>();
        for(int i = 0; i < messageList.size(); i++){
            MissedMessage message = messageList.get(i);
            if(message.getHouseId() == id){
                tempMessages.add(message);
            }
        }
        messageList.removeAll(tempMessages);
    }

    public void clearAllNotifications(){
        mNotificationManager.cancelAll();
        messageList.clear();
    }

    public void newMissedMessages(int houseId, int myId, String houseName, String name, String message, int missedMessages, boolean add){
        boolean exist = false;
        MissedMessage missedMessage = new MissedMessage(houseId, houseName, name, message, missedMessages);
        for(int i = 0; i < messageList.size(); i++){
            if(messageList.get(i).getHouseId() == houseId){
                if(add) {
                    missedMessages += messageList.get(i).getMissedMessages();
                }
                missedMessage.setMissedMessages(missedMessages);
                messageList.remove(i);
                messageList.add(missedMessage);
                exist = true;
            }
        }
        if(!exist){
            messageList.add(missedMessage);
        }
        if(missedMessages > 1){
            createMultiMessageNotify(houseId, myId, houseName, name, missedMessages, message);
        }else{
            createMessageNotify(houseId, myId, houseName, name, message);
        }
    }

    private void createMessageNotify(int houseId, int myId, String houseName, String name, String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(service)
                .setContentText(name.substring(0, Math.min(12, name.length())) + ": " + message)
                .setContentTitle(houseName)
                .setSmallIcon(R.drawable.ic_home_white_48pt_3x);

        createHouseNotification(houseId, myId, houseName, mBuilder);
    }

    private void createMultiMessageNotify(int houseId, int myId, String houseName, String name, int missedMessages, String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(service)
                .setContentText(missedMessages + " messages: ..." + message)
                .setContentTitle(houseName)
                .setSmallIcon(R.drawable.ic_home_white_48pt_3x);

        createHouseNotification(houseId, myId, houseName, mBuilder);
    }

    private void createHouseNotification(int houseId, int myId, String houseName, NotificationCompat.Builder mBuilder) {
        Intent resultIntent = new Intent(service, HouseActivity.class)
                .putExtra(HouseActivity.EXTRA_ID, houseId)
                .putExtra(HouseActivity.MY_ID, myId)
                .putExtra(HouseActivity.HOUSE_NAME, houseName);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(service);
// Adds the back stack
        stackBuilder.addParentStack(HouseActivity.class);
// Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
// Gets a PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(houseId, PendingIntent.FLAG_ONE_SHOT);

        mBuilder.setContentIntent(resultPendingIntent);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(service);
            boolean soundNotify = sharedPreferences.getBoolean(service.getString(R.string.pref_key_sound), true);
            boolean vibrateNotify = sharedPreferences.getBoolean(service.getString(R.string.pref_key_vibrate), true);
            boolean blinkLED = sharedPreferences.getBoolean(service.getString(R.string.pref_key_blinkLED), false);
            if (soundNotify) {
                notification.defaults |= Notification.DEFAULT_SOUND;
            }
            if (vibrateNotify) {
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }
            if (blinkLED) {
                notification.flags |= Notification.FLAG_SHOW_LIGHTS;
                notification.ledARGB = 0xff00ff00;
                notification.ledOnMS = 300;
                notification.ledOffMS = 1000;
            }

        boolean notify = sharedPreferences.getBoolean(service.getString(R.string.pref_key_receive_notifications), true);
        if(notify){
            mNotificationManager.notify(houseId, notification);
        }

    }

}
