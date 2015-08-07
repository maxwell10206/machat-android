package machat.machat.socketIO;

import android.graphics.Bitmap;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import machat.machat.SocketCommunication;
import machat.machat.SocketService;

/**
 * Created by Admin on 6/25/2015.
 */
public class AvatarManager {

    private static class ImageViewUser {

        private int id;

        private OnCallbackAvatar listener;

        ImageViewUser(int id, OnCallbackAvatar listener){
            this.id = id;
            this.listener = listener;
        }

        public OnCallbackAvatar getListener(){
            return listener;
        }

        public int getId(){ return id; }
    }

    private static ArrayList<ImageViewUser> imageViewUsers = new ArrayList<>();

    private static ArrayList<BitmapUser> bitmapUsers = new ArrayList<>();

    private static SocketService mService;

    private static Realm realm;

    public static void setSocketService(SocketService mService){
        AvatarManager.mService = mService;
        realm = Realm.getInstance(mService.getApplicationContext());
        RealmResults<BitmapUser> results = realm.where(BitmapUser.class).findAll();
        bitmapUsers.clear();
        bitmapUsers.addAll(results);
    }

    public static void checkForUpdates(){
        for(BitmapUser bitmapUser: bitmapUsers){
            mService.send.updateAvatar(bitmapUser.getId(), bitmapUser.getTime());
        }
    }

    public static void getAvatar(int id, OnCallbackAvatar listener){
        byte[] avatar = null;
        long time = 0;
        boolean found = false;
        for(int i = 0; i < bitmapUsers.size(); i++){
            BitmapUser bitmapUser = bitmapUsers.get(i);
            if(bitmapUser.getId() == id){
                avatar = bitmapUser.getAvatar();
                time = bitmapUser.getTime();
                found = true;
            }
        }
        if(!found) {
            boolean download = true;
            for(int i = 0; i < imageViewUsers.size(); i++){
                ImageViewUser imageViewUser = imageViewUsers.get(i);
                if(imageViewUser.getId() == id){
                    download = false;
                }
            }
            if(download) {
                mService.send.getAvatar(id);
            }
            imageViewUsers.add(new ImageViewUser(id, listener));
        }else{
            listener.newAvatar(id, avatar, time);
        }
    }

    public static byte[] getAvatar(int id){
        byte[] avatar = new byte[0];
        for(int i = 0; i < bitmapUsers.size(); i++){
            BitmapUser bitmapUser = bitmapUsers.get(i);
            if(bitmapUser.getId() == id){
                avatar = bitmapUser.getAvatar();
            }
        }
        return avatar;
    }

    public static void reDownload(){
        for(int i = 0; i < imageViewUsers.size(); i++){
            ImageViewUser imageViewUser = imageViewUsers.get(i);
            mService.send.getAvatar(imageViewUser.getId());
        }
    }

    public static void newAvatar(int id, byte[] avatar, long time){
        BitmapUser bitmapUser = new BitmapUser();
        bitmapUser.setAvatar(avatar);
        bitmapUser.setId(id);
        bitmapUser.setTime(time);
        bitmapUsers.add(bitmapUser);
        Realm realm = Realm.getInstance(mService.getApplicationContext());
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(bitmapUser);
        realm.commitTransaction();
        for(int i = 0; i < imageViewUsers.size(); i++){
            ImageViewUser imageViewUser = imageViewUsers.get(i);
            if(imageViewUser.getId() == id){
                imageViewUser.getListener().newAvatar(id, avatar, time);
                imageViewUsers.remove(i);
            }
        }
    }
}
