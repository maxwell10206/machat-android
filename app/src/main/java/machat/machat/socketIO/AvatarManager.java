package machat.machat.socketIO;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import machat.machat.FavoriteItem;
import machat.machat.SocketService;

/**
 * Created by Admin on 6/25/2015.
 */
public class AvatarManager {

    private static ArrayList<ImageViewUser> imageViewUsers = new ArrayList<>();
    private static ArrayList<BitmapUser> bitmapUsers = new ArrayList<>();
    private static SocketService mService;
    private static Realm realm;

    public static void setSocketService(SocketService mService) {
        AvatarManager.mService = mService;
        realm = Realm.getDefaultInstance();
        RealmResults<BitmapUser> results = realm.where(BitmapUser.class).findAll();
        bitmapUsers.clear();

        realm.beginTransaction();
        for (int i = 0; i < results.size(); i++) {
            if (!mService.favorites.getFavorite(results.get(i).getId())) {
                results.get(i).removeFromRealm();
            }
        }
        realm.commitTransaction();
        bitmapUsers.addAll(results);
    }

    public static void checkForUpdates() {
        mService.send.updateAvatars(bitmapUsers);
    }

    public static void getAvatar(int id, OnCallbackAvatar listener) {
        byte[] avatar = null;
        long time = 0;
        boolean found = false;
        for (int i = 0; i < bitmapUsers.size(); i++) {
            BitmapUser bitmapUser = bitmapUsers.get(i);
            if (bitmapUser.getId() == id) {
                avatar = bitmapUser.getAvatar();
                time = bitmapUser.getTime();
                found = true;
            }
        }
        if (!found) {
            boolean download = true;
            for (int i = 0; i < imageViewUsers.size(); i++) {
                ImageViewUser imageViewUser = imageViewUsers.get(i);
                if (imageViewUser.getId() == id) {
                    download = false;
                }
            }
            if (download) {
                mService.send.getAvatar(id);
            }
            imageViewUsers.add(new ImageViewUser(id, listener));
        } else {
            if (avatar.length > 0) {
                listener.newAvatar(id, avatar, time);
            }
        }
    }

    public static byte[] getAvatar(int id) {
        byte[] avatar = new byte[0];
        for (int i = 0; i < bitmapUsers.size(); i++) {
            BitmapUser bitmapUser = bitmapUsers.get(i);
            if (bitmapUser.getId() == id) {
                avatar = bitmapUser.getAvatar();
            }
        }
        return avatar;
    }

    public static void reDownload() {
        List<Integer> downloadedIDs = new ArrayList<>();
        for (int i = 0; i < imageViewUsers.size(); i++) {
            ImageViewUser imageViewUser = imageViewUsers.get(i);
            boolean download = true;
            for (int j = 0; j < downloadedIDs.size(); j++) {
                if (downloadedIDs.get(j) == imageViewUser.getId()) {
                    download = false;
                }
            }
            if (download) {
                mService.send.getAvatar(imageViewUser.getId());
                downloadedIDs.add(imageViewUser.getId());
            }
        }
    }

    public static void newAvatar(int id, byte[] avatar, long time) {
        BitmapUser bitmapUser = new BitmapUser();
        bitmapUser.setAvatar(avatar);
        bitmapUser.setId(id);
        bitmapUser.setTime(time);
        bitmapUsers.add(bitmapUser);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FavoriteItem> results = realm.where(FavoriteItem.class).findAll();
        boolean favorite = false;
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).getUserId() == id) {
                favorite = true;
            }
        }
        if (favorite) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(bitmapUser);
            realm.commitTransaction();
        }

        ArrayList<ImageViewUser> temp = new ArrayList<>();
        for (int i = 0; i < imageViewUsers.size(); i++) {
            ImageViewUser imageViewUser = imageViewUsers.get(i);
            if (imageViewUser.getId() == id) {
                imageViewUser.getListener().newAvatar(id, avatar, time);
                temp.add(imageViewUser);
            }
        }
        imageViewUsers.removeAll(temp);
    }

    private static class ImageViewUser {

        private int id;

        private OnCallbackAvatar listener;

        ImageViewUser(int id, OnCallbackAvatar listener) {
            this.id = id;
            this.listener = listener;
        }

        public OnCallbackAvatar getListener() {
            return listener;
        }

        public int getId() {
            return id;
        }
    }
}
