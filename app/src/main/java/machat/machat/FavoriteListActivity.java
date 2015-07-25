package machat.machat;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import machat.machat.socketIO.OnBlockedByUser;
import machat.machat.socketIO.OnCallbackAvatar;
import machat.machat.socketIO.OnCallbackFavorite;
import machat.machat.socketIO.OnCallbackSendMessage;
import machat.machat.socketIO.OnChangeName;
import machat.machat.socketIO.OnLoginListener;
import machat.machat.socketIO.OnNewFavoriteList;
import machat.machat.socketIO.OnNewMessage;
import machat.machat.socketIO.OnUserReadMessage;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

/**
 * Created by Admin on 6/7/2015.
 */
public class FavoriteListActivity extends ListActivity implements OnCallbackAvatar, OnBlockedByUser, OnUserReadMessage, OnCallbackSendMessage, OnChangeName, OnLoginListener, OnNewMessage, machat.machat.socketIO.OnReadHouse, SocketActivity.SocketListener, SwipeRefreshLayout.OnRefreshListener, OnCallbackFavorite, AdapterView.OnItemLongClickListener, OnNewFavoriteList, FavoriteItemDialogFragment.OnCompleteListener {

    SocketActivity socketActivity = new SocketActivity(this);

    private FavoriteListAdapter arrayAdapter;

    private boolean connected = false;

    private RelativeLayout loadingItem;

    private static final String DIALOG_BOX = "dialogBox";

    private SocketService mService;

    private MyProfile myProfile;

    private Menu menu;

    private SwipeRefreshLayout refreshLayout;

    Realm realm;

    @Override
    protected void onListItemClick (ListView l, View v, int position, long i){
        Intent intent = new Intent(this, HouseActivity.class);
        FavoriteItem favoriteItem = (FavoriteItem) getListView().getItemAtPosition(position);
        intent.putExtra(HouseActivity.EXTRA_ID, favoriteItem.getUser().getId());
        intent.putExtra(HouseActivity.MY_ID, myProfile.getId());
        intent.putExtra(HouseActivity.HOUSE_NAME, favoriteItem.getUser().getName());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        FavoriteItem favoriteItem = (FavoriteItem) getListView().getItemAtPosition(position);
        if(myProfile.getId() == favoriteItem.getUser().getId()){
            if(mService.isConnected()) {
                muteHouse(favoriteItem.getUser().getId(), !(favoriteItem.isMute()));
            }
        }else {
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
        setContentView(R.layout.activity_favorite_list);
        socketActivity.setOnSocketListener(this);
        getListView().setOnItemLongClickListener(this);
        loadingItem = (RelativeLayout) getLayoutInflater().inflate(R.layout.loading_item, null);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        refreshLayout.setOnRefreshListener(this);

        arrayAdapter = new FavoriteListAdapter(this, new ArrayList<FavoriteItem>());
        setListAdapter(arrayAdapter);

        /*/ WIP Offline Storage
        realm = Realm.getInstance(getApplicationContext());

        RealmQuery<FavoriteItem> query = realm.where(FavoriteItem.class);
        RealmResults<FavoriteItem> result = query.findAll();
        arrayAdapter.addAll(result);
        /*/
    }

    @Override
    protected void onResume(){
        super.onResume();
        ((MachatApplication) getApplication()).activityResumed();
        socketActivity.connect();
    }

    @Override
    protected void onPause(){
        super.onPause();
        ((MachatApplication) getApplication()).activityPaused();
        socketActivity.disconnect();
    }

    @Override
    public void onConnect(SocketService mService) {
        this.mService = mService;
        connected = true;
        myProfile = mService.user.getMyProfile();
        arrayAdapter.setMyId(myProfile.getId());
        if(mService.user.isLogin()) {
            onLoginSuccess(myProfile);
        }
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

    public void getHouses(){
        if(connected){
            mService.send.getFavoriteList();
            refreshLayout.setRefreshing(true);
        }
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
        if(item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, MyProfileActivity.class));
        }else{
            startActivity(new Intent(this, SearchActivity.class));
        }
        return true;
    }

    @Override
    public void onReceive(String command, String data) {
        if(command.equals(SocketCommand.GET_FAVORITE_LIST)) {
            SocketParse.parseFavoriteList(data, this);
        }else if (command.equals(Socket.EVENT_CONNECT)) {
            getHouses();
        }else if(command.equals(SocketCommand.FAVORITE_HOUSE)){
            SocketParse.parseFavoriteHouse(data, this);
        }else if(command.equals(SocketCommand.GET_AVATAR)){
            SocketParse.parseGetAvatar(data, this);
        }else if(command.equals(SocketCommand.NEW_MESSAGE)){
            SocketParse.parseMessage(data, this);
        }else if(command.equals(SocketCommand.SEND_MESSAGE)){
            SocketParse.parseSendMessage(data, this);
        }else if(command.equals(SocketCommand.READ_HOUSE)){
            SocketParse.parseReadHouse(data, this);
        }else if(command.equals(SocketCommand.LOGIN)){
            SocketParse.parseLogin(data, this);
        }else if(command.equals(SocketCommand.CHANGE_NAME)){
            SocketParse.parseChangeName(data, this);
        }else if(command.equals(SocketCommand.BLOCKED_BY_USER)){
            SocketParse.parseBlockedByUser(data, this);
        } else if (command.equals(SocketCommand.READ_MESSAGE)) {
            SocketParse.parseUserReadMessage(data, this);
        }
    }

    @Override
    public void newFavoriteList(ArrayList<FavoriteItem> favoriteItems) {
        arrayAdapter.clear();
        for(int i = 0; i < favoriteItems.size(); i++){
            FavoriteItem favoriteItem = favoriteItems.get(i);
            if(favoriteItem.getUser().getId() == myProfile.getId()){
                favoriteItem.setHeader(true);
            }
        }
        /*/ WIP Offline Storage
        RealmQuery<FavoriteItem> query = realm.where(FavoriteItem.class);
        RealmResults<FavoriteItem> result = query.findAll();
        realm.beginTransaction();
        for(int i = 0; i < result.size(); i++){
            FavoriteItem favoriteItem = result.get(i);
            favoriteItem.removeFromRealm();
        }
        realm.copyToRealmOrUpdate(favoriteItems);
        realm.commitTransaction();
        /*/
        arrayAdapter.addAll(favoriteItems);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void getFavoriteListFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
    }

    @Override
    public void muteHouse(int id, boolean mute) {
        if(connected && mService.isConnected()){
            mService.send.muteHouse(id, mute);
            arrayAdapter.setMuteById(id, mute);
        }
    }

    @Override
    public void unFavoriteHouse(int id) {
        if(connected && mService.isConnected()){
            mService.send.favoriteHouse(id, false);
            mService.send.leaveHouse(id);
        }
    }

    @Override
    public void openProfile(int id) {
        startActivity(new Intent(this, ProfileActivity.class).putExtra(ProfileActivity.BUNDLE_ID, id));
    }

    @Override
    public void onRefresh() {
        getHouses();
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void newAvatar(int id, Bitmap bitmap) {
        arrayAdapter.setBitmapById(id, bitmap);
    }

    @Override
    public void newMessage(Message message) {
        arrayAdapter.setMessageById(message);
        arrayAdapter.setReadHouseById(message.getHouseId(), false);
    }

    @Override
    public void removeFavorite(int id) {
        arrayAdapter.removeById(id);
    }

    @Override
    public void newFavorite(FavoriteItem favoriteItem) {
        arrayAdapter.add(favoriteItem);
    }

    @Override
    public void favoriteError(String err) {}

    @Override
    public void readHouse(int houseId) {
        arrayAdapter.setReadHouseById(houseId, true);
    }

    @Override
    public void changeNameSuccess(String name) {
        arrayAdapter.changeNameById(myProfile.getId(), name);
    }

    @Override
    public void changeNameFailed(String err) {
    }

    @Override
    public void sendMessageSuccess(Message message) {
        arrayAdapter.setMessageById(message);
    }

    @Override
    public void sendMessageFailed(String err) {
    }

    @Override
    public void blockedBy(int userId) {
        arrayAdapter.setBlockById(userId, true);
    }

    @Override
    public void unBlockedBy(int userId) {
        arrayAdapter.setBlockById(userId, false);
    }

    @Override
    public void userReadMessage(int id) {
        arrayAdapter.setReadMessageById(id);
    }
}


