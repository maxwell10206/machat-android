package machat.machat.socketIO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import machat.machat.BlockUser;
import machat.machat.SocketService;

/**
 * Created by Maxwell on 8/13/2015.
 */
public class BlockReceiver extends BroadcastReceiver implements OnCallbackBlock, OnCallbackBlockList {

    private SocketService mService;

    private Realm realm;

    private ArrayList<BlockUser> blockUserList = new ArrayList<>();

    public BlockReceiver(SocketService mService) {
        this.mService = mService;
        this.realm = Realm.getDefaultInstance();
        this.blockUserList.addAll(realm.where(BlockUser.class).findAll());
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra(SocketService.COMMAND);
        String data = intent.getStringExtra(SocketService.DATA);
        if (command.equals(SocketCommand.BLOCK_USER)) {
            SocketParse.parseBlockUser(data, this);
        }
    }

    @Override
    public void unBlocked(int id) {
        for (int i = 0; i < blockUserList.size(); i++) {
            BlockUser blockUser = blockUserList.get(i);
            if (blockUser.getId() == id) {
                realm.beginTransaction();
                blockUser.removeFromRealm();
                blockUserList.remove(i);
                realm.commitTransaction();
            }
        }
    }

    @Override
    public void blocked(BlockUser blockUser) {
        realm.beginTransaction();
        blockUserList.add(realm.copyToRealmOrUpdate(blockUser));
        realm.commitTransaction();
    }

    @Override
    public void newBlockList(ArrayList<BlockUser> newBlockList) {
        realm.beginTransaction();
        RealmResults<BlockUser> results = realm.where(BlockUser.class).findAll();
        results.clear();
        blockUserList.clear();
        blockUserList.addAll(realm.copyToRealmOrUpdate(newBlockList));
        realm.commitTransaction();
    }
}
