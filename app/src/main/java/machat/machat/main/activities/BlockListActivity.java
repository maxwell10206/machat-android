package machat.machat.main.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import machat.machat.main.adapters.BlockListArrayAdapter;
import machat.machat.main.dialogs.BlockItemDialog;
import machat.machat.models.BlockUser;
import machat.machat.models.MyProfile;
import machat.machat.R;
import machat.machat.util.SocketService;
import machat.machat.parsing.interfaces.OnLoginListener;
import machat.machat.conf.SocketCommand;
import machat.machat.parsing.SocketParse;

public class BlockListActivity extends ListActivity implements SocketActivity.SocketListener, BlockItemDialog.BlockChange, OnLoginListener {

    public static String TITLE = "Block List";

    private SocketActivity socketActivity = new SocketActivity(this);

    private SocketService mService;

    private BlockListArrayAdapter arrayAdapter;

    private RealmChangeListener realmChangeListener;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socketActivity.setOnSocketListener(this);
        getActionBar().setTitle(TITLE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_block_list);
        arrayAdapter = new BlockListArrayAdapter(this, new ArrayList<BlockUser>());
        setListAdapter(arrayAdapter);
        getListView().setEmptyView(findViewById(R.id.empty_view));
        realm = Realm.getDefaultInstance();

        RealmResults<BlockUser> users = realm.where(BlockUser.class).findAll();
        arrayAdapter.addAll(users);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long i) {
        BlockUser blockUser = arrayAdapter.getItem(position);
        BlockItemDialog blockItemDialog = new BlockItemDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(BlockItemDialog.ID, blockUser.getId());
        bundle.putString(BlockItemDialog.NAME, blockUser.getName());
        blockItemDialog.setArguments(bundle);
        blockItemDialog.show(getFragmentManager(), "dialog");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        socketActivity.connect();
        realmChangeListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                arrayAdapter.clear();
                RealmResults<BlockUser> result = realm.where(BlockUser.class).findAll();
                arrayAdapter.addAll(result);
            }
        };
        realm.addChangeListener(realmChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        socketActivity.disconnect();
        realm.removeChangeListener(realmChangeListener);
    }

    @Override
    public void onConnect(SocketService mService) {
        this.mService = mService;
        onLoginSuccess(mService.user.getMyProfile());
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onReceive(String command, String data) {
        if (command.equals(SocketCommand.LOGIN)) {
            SocketParse.parseLogin(data, this);
        }
    }

    @Override
    public void unBlock(int id) {
        if (mService.isConnected()) {
            mService.send.blockUser(id, false);
        } else {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoginSuccess(MyProfile myProfile) {
        mService.send.getBlockList();
    }

    @Override
    public void onLoginFailed(String err) {

    }
}
