package machat.machat;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import machat.machat.socketIO.OnLoginListener;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

public class FavoriteListActivity extends ListActivity implements OnLoginListener, SocketActivity.SocketListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener, FavoriteItemDialogFragment.OnCompleteListener {

    private static final String DIALOG_BOX = "dialogBox";
    public static String MY_ID = "myId";
    public Realm realm;
    private SocketActivity socketActivity = new SocketActivity(this);
    private FavoriteListAdapter arrayAdapter;
    private boolean connected = false;
    private RelativeLayout loadingItem;
    private SocketService mService;
    private Menu menu;
    private SwipeRefreshLayout refreshLayout;
    private int myId;
    private RealmChangeListener realmListener;

    @Override
    protected void onListItemClick(ListView l, View v, int position, long i) {
        Intent intent = new Intent(this, HouseActivity.class);
        FavoriteItem favoriteItem = (FavoriteItem) getListView().getItemAtPosition(position);
        intent.putExtra(HouseActivity.HOUSE_ID, favoriteItem.getUserId());
        intent.putExtra(HouseActivity.MY_ID, myId);
        intent.putExtra(HouseActivity.HOUSE_NAME, favoriteItem.getName());
        intent.putExtra(HouseActivity.HOUSE_USERNAME, favoriteItem.getUsername());
        intent.putExtra(HouseActivity.FAVORITE, mService.favorites.getFavorite(favoriteItem.getUserId()));
        intent.putExtra(HouseActivity.MUTE, mService.favorites.getMute(favoriteItem.getUserId()));
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        FavoriteItem favoriteItem = (FavoriteItem) getListView().getItemAtPosition(position);
        if (myId == favoriteItem.getUserId()) {
            if (mService.isConnected()) {
                muteHouse(favoriteItem.getUserId(), !(favoriteItem.isMute()));
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable(FavoriteItemDialogFragment.FAVORITE_ITEM, favoriteItem);

            FavoriteItemDialogFragment dialog = new FavoriteItemDialogFragment();
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), DIALOG_BOX);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myId = getIntent().getExtras().getInt(MY_ID);
        setContentView(R.layout.activity_favorite_list);
        socketActivity.setOnSocketListener(this);
        getListView().setOnItemLongClickListener(this);
        loadingItem = (RelativeLayout) getLayoutInflater().inflate(R.layout.loading_item, null);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        arrayAdapter = new FavoriteListAdapter(this, new ArrayList<FavoriteItem>());
        setListAdapter(arrayAdapter);
        arrayAdapter.setMyId(myId);

        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ((MachatApplication) getApplication()).activityResumed();

        realmListener = new RealmChangeListener() {
            @Override
            public void onChange() {
                arrayAdapter.clear();
                RealmResults<FavoriteItem> result = realm.where(FavoriteItem.class).findAll();
                arrayAdapter.addAll(result);
                refreshLayout.setRefreshing(false);
            }
        };

        RealmQuery<FavoriteItem> query = realm.where(FavoriteItem.class);
        RealmResults<FavoriteItem> result = query.findAll();
        arrayAdapter.clear();
        arrayAdapter.addAll(result);

        realm.addChangeListener(realmListener);
        socketActivity.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ((MachatApplication) getApplication()).activityPaused();
        realm.removeChangeListener(realmListener);
        socketActivity.disconnect();
    }

    @Override
    public void onConnect(SocketService mService) {
        this.mService = mService;
        connected = true;
        onLoginSuccess(mService.user.getMyProfile());
    }

    @Override
    public void onLoginSuccess(MyProfile myProfile) {
        getHouses();
    }

    @Override
    public void onLoginFailed(String err) {
        Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnect() {
        connected = false;
    }

    public void getHouses() {
        mService.send.getFavoriteList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_favorite_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, MyProfileActivity.class));
        } else {
            startActivity(new Intent(this, SearchActivity.class));
        }
        return true;
    }

    @Override
    public void onReceive(String command, String data) {
        if (command.equals(SocketCommand.LOGIN)) {
            SocketParse.parseLogin(data, this);
        }
    }

    @Override
    public void muteHouse(int id, boolean mute) {
        if (connected && mService.isConnected()) {
            mService.send.muteHouse(id, mute);
        }else{
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void unFavoriteHouse(int id) {
        if (connected && mService.isConnected()) {
            mService.send.favoriteHouse(id, false);
            mService.send.leaveHouse(id);
        }else{
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void openProfile(int id, String name, String username) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_ID, id);
        intent.putExtra(ProfileActivity.NAME, name);
        intent.putExtra(ProfileActivity.USERNAME, username);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        getHouses();
    }

}


