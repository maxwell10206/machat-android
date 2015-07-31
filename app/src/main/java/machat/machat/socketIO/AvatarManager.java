package machat.machat.socketIO;

import android.graphics.Bitmap;

import java.util.ArrayList;

import machat.machat.SocketCommunication;

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

    private static class BitmapUser{

        private int id;

        private byte[] avatar;

        BitmapUser(int id, byte[] avatar){
            this.id = id;
            this.avatar = avatar;
        }

        public int getId(){ return id; }

        public byte[] getAvatar(){ return avatar; }
    }

    private static ArrayList<ImageViewUser> imageViewUsers = new ArrayList<>();

    private static ArrayList<BitmapUser> bitmapUsers = new ArrayList<>();

    private static SocketCommunication send;

    public static void setSocketCommunication(SocketCommunication newSend){
        send = newSend;
    }

    public static void getAvatar(int id, OnCallbackAvatar listener){
        byte[] avatar = null;
        boolean found = false;
        for(int i = 0; i < bitmapUsers.size(); i++){
            BitmapUser bitmapUser = bitmapUsers.get(i);
            if(bitmapUser.getId() == id){
                avatar = bitmapUser.getAvatar();
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
                send.getAvatar(id);
            }
            imageViewUsers.add(new ImageViewUser(id, listener));
        }else{
            listener.newAvatar(id, avatar);
        }
    }

    public static void reDownload(){
        for(int i = 0; i < imageViewUsers.size(); i++){
            ImageViewUser imageViewUser = imageViewUsers.get(i);
            send.getAvatar(imageViewUser.getId());
        }
    }

    public static void newAvatar(int id, byte[] avatar){
        bitmapUsers.add(new BitmapUser(id, avatar));
        for(int i = 0; i < imageViewUsers.size(); i++){
            ImageViewUser imageViewUser = imageViewUsers.get(i);
            if(imageViewUser.getId() == id){
                imageViewUser.getListener().newAvatar(id, avatar);
                imageViewUsers.remove(i);
            }
        }
    }
}
