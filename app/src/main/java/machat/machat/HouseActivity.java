package machat.machat;

import android.app.ListActivity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import machat.machat.socketIO.OnCallbackAvatar;
import machat.machat.socketIO.OnCallbackFavorite;
import machat.machat.socketIO.OnCallbackMessageStatus;
import machat.machat.socketIO.OnDeliveredMessage;
import machat.machat.socketIO.OnJoinHouse;
import machat.machat.socketIO.OnLoginListener;
import machat.machat.socketIO.OnNewHouse;
import machat.machat.socketIO.OnNewMessage;
import machat.machat.socketIO.OnNewMessageList;
import machat.machat.socketIO.OnUserReadMessage;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

/**
 * Created by Admin on 6/13/2015.
 */
public class HouseActivity extends ListActivity implements SocketActivity.SocketListener, OnCallbackMessageStatus, machat.machat.socketIO.OnBlockedByUser, MessageDialogFragment.Action, OnLoginListener, OnJoinHouse, OnUserReadMessage, OnDeliveredMessage, OnCallbackAvatar, machat.machat.socketIO.OnCallbackSendMessage, OnNewMessageList, OnCallbackFavorite, ListView.OnScrollListener, OnNewMessage, View.OnClickListener {

    public final static String EXTRA_ID = "houseId";

    public final static String HOUSE_NAME = "houseName";

    public final static String MY_ID = "myId";

    public final static String FAVORITE = "favorite";

    public final static String MUTE = "mute";

    private SocketActivity socketActivity = new SocketActivity(this);

    private HouseArrayAdapter arrayAdapter;

    private boolean mute;

    private boolean favorite;

    private String houseName;

    private int houseId;

    private int myId;

    public int getMyId(){ return myId; }

    private int localId = 0;

    private SocketService mService;

    private ImageButton sendMessage;

    private EditText inputMessage;

    private boolean connected = false;

    private boolean waitingForOldMessages = false;

    private boolean waitingForNewMessages = false;

    private boolean waitingForFavorite = false;

    private boolean olderMessages = true;

    private boolean loadOfflineMessages = true;

    private int oldestMessageId;

    private int newestMessageId;

    private RelativeLayout loadingView;

    private MenuItem favoriteMenuItem;

    private MenuItem muteMenuItem;

    private Menu menu;

    private MyProfile myProfile;

    public Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        oldestMessageId = Integer.MAX_VALUE;
        newestMessageId = 0;
        setContentView(R.layout.activity_house);
        getListView().setOnScrollListener(this);
        houseId = getIntent().getExtras().getInt(EXTRA_ID);
        myId = getIntent().getExtras().getInt(MY_ID);
        houseName = getIntent().getExtras().getString(HOUSE_NAME);
        favorite = getIntent().getExtras().getBoolean(FAVORITE);
        mute = getIntent().getExtras().getBoolean(MUTE);
        getActionBar().setTitle(houseName);
        arrayAdapter = new HouseArrayAdapter(this, new ArrayList<Message>());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        socketActivity.setOnSocketListener(this);
        sendMessage = (ImageButton) findViewById(R.id.sendMessage);
        inputMessage = (EditText) findViewById(R.id.inputMessage);
        inputMessage.setOnClickListener(this);
        loadingView = (RelativeLayout) getLayoutInflater().inflate(R.layout.loading_item, null);
        getListView().addHeaderView(loadingView); //poor android 4.4 design.
        setListAdapter(arrayAdapter);
        loadingView.setVisibility(View.INVISIBLE);

        ListView l = getListView();
        l.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        l.setStackFromBottom(true);

        realm = Realm.getInstance(getApplicationContext());

        RealmQuery<Message> query = realm.where(Message.class).equalTo("houseId", houseId);
        RealmResults<Message> result = query.findAll();
        arrayAdapter.addAll(result);

    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_profile:
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra(ProfileActivity.BUNDLE_ID, houseId);
                startActivity(intent);
                return true;
            case R.id.action_favorite:
                if(connected && mService.isConnected()) {
                    if(!waitingForFavorite) {
                        mService.send.favoriteHouse(houseId, (!favorite));
                        waitingForFavorite = true;
                    }
                }else{
                    Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_block_list:
                startActivity(new Intent(this, BlockListActivity.class));
                return true;
            case R.id.action_mute:
                muteHouse(!(mute));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createMessageDialog(Message message){
        MessageDialogFragment messageDialogFragment = new MessageDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(MessageDialogFragment.ID, message.getUserId());
        bundle.putString(MessageDialogFragment.NAME, message.getName());
        bundle.putInt(MessageDialogFragment.MY_ID, myId);
        bundle.putInt(MessageDialogFragment.HOUSE_ID, houseId);
        bundle.putInt(MessageDialogFragment.MESSAGE_ID, message.getId());
        bundle.putString(MessageDialogFragment.MESSAGE, message.getMessage());
        messageDialogFragment.setArguments(bundle);
        messageDialogFragment.show(getFragmentManager(), "messageOptions");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        if(this.menu == null) {
            socketActivity.connect();
        }
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        if(myId == houseId){
            inflater.inflate(R.menu.menu_my_house, menu);
        }else {
            inflater.inflate(R.menu.menu_house, menu);
            favoriteMenuItem = menu.findItem(R.id.action_favorite);
        }
        muteMenuItem = menu.findItem(R.id.action_mute);

        sendMessage.setOnClickListener(this);
        if(myId != houseId) {
            favoriteMenuItem.setActionView(null).setEnabled(true);
            if (favorite) {
                favoriteMenuItem.setIcon(R.drawable.ic_favorite_black_24dp);
            } else {
                favoriteMenuItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
            }
        }
        if (favorite) {
            if (mute) {
                muteMenuItem.setIcon(R.drawable.ic_volume_mute_black_24dp);
            } else {
                muteMenuItem.setIcon(R.drawable.ic_volume_up_black_24dp);
            }
            muteMenuItem.setVisible(true);
        } else {
            muteMenuItem.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(menu != null){
            socketActivity.connect();
        }
        if(connected){
            mService.machatNotificationManager.clearNotification(houseId);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(connected) {
            mService.user.setHouseId(houseId);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(connected) {
            mService.user.setHouseId(0);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        socketActivity.disconnect();
        if(!favorite) {
            mService.send.leaveHouse(houseId);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        realm.beginTransaction();
        RealmQuery<Message> query = realm.where(Message.class).equalTo("houseId", houseId);
        RealmResults<Message> result = query.findAll();
        result.sort("id", false);
        for(int i = 20; i < result.size(); i++){
            result.remove(i);
        }
        realm.commitTransaction();
    }

    private void getOldMessages(){
        if(connected && !waitingForOldMessages && olderMessages && mService.isConnected()){
            mService.send.getOldMessages(houseId, oldestMessageId);
            waitingForOldMessages = true;
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    private void getNewMessages(){
        if(connected && !waitingForNewMessages && mService.isConnected()){
            mService.send.getNewMessages(houseId, newestMessageId);
            waitingForNewMessages = true;
        }
    }

    @Override
    public void onLoginSuccess(MyProfile myProfile) {
        if(connected){
            mService.send.readHouse(houseId);
            mService.send.joinHouse(houseId);
            mService.send.getHouse(houseId);
            mService.user.setHouseId(houseId);
            mService.machatNotificationManager.clearNotification(houseId);
            updateMessages();
        }
    }

    @Override
    public void onLoginFailed(String err) {

    }

    private void updateMessages(){
        for(int i = 0; i < arrayAdapter.getCount(); i++){
            Message message = arrayAdapter.getItem(i);
            int messageId = message.getId();
            if(messageId > newestMessageId){
                newestMessageId = messageId;
            }
            if(messageId < oldestMessageId){
                oldestMessageId = messageId;
            }
            if(message.getStatus() < Message.READ){
                mService.send.getMessageStatus(messageId);
            }
        }
    }

    @Override
    public void onConnect(SocketService mService) {
        this.mService = mService;
        this.myProfile = mService.user.getMyProfile();
        connected = true;
        updateMessages();
        onLoginSuccess(myProfile);
    }

    @Override
    public void onDisconnect() {
        connected = false;
    }

    @Override
    public void onReceive(String command, String data) {
        if(command.equals(SocketCommand.GET_OLD_MESSAGES)) {
            SocketParse.parseMessageList(SocketCommand.GET_OLD_MESSAGES, data, this);
        }else if(command.equals(SocketCommand.GET_NEW_MESSAGES)){
            SocketParse.parseMessageList(SocketCommand.GET_NEW_MESSAGES, data, this);
        }else if(command.equals(SocketCommand.NEW_MESSAGE)){
            SocketParse.parseMessage(data, this);
        }else if(command.equals(SocketCommand.FAVORITE_HOUSE)){
            SocketParse.parseFavoriteHouse(data, this);
        }else if(command.equals(SocketCommand.SEND_MESSAGE)){
            SocketParse.parseSendMessage(data, this);
        }else if(command.equals(SocketCommand.READ_MESSAGE)){
            SocketParse.parseUserReadMessage(data, this);
        }else if(command.equals(SocketCommand.DELIVERED_MESSAGE)){
            SocketParse.parseDeliveredMessage(data, this);
        }else if(command.equals(SocketCommand.JOIN_HOUSE)){
            SocketParse.parseJoinHouse(data, this);
        }else if(command.equals(SocketCommand.LOGIN)){
            SocketParse.parseLogin(data, this);
        }else if(command.equals(SocketCommand.BLOCKED_BY_USER)){
            SocketParse.parseBlockedByUser(data, this);
        }else if(command.equals(SocketCommand.GET_MESSAGE_STATUS)){
            SocketParse.parseGetMessageStatus(data, this);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(firstVisibleItem == 0 && (firstVisibleItem != 0 || totalItemCount >= 10)){
            getOldMessages();
        }
        if(arrayAdapter != null && arrayAdapter.getCount() != 0) {
            for (int i = firstVisibleItem + getListView().getHeaderViewsCount(); i < firstVisibleItem + visibleItemCount; i++) {
                Message message = (Message) getListView().getItemAtPosition(i);
                if (message.getStatus() != Message.READ && connected && message.getUserId() != myProfile.getId()) {
                    mService.send.readMessage(message.getId());
                }
            }
        }
    }

    @Override
    public void newMessage(Message message) {
        if(message.getHouseId() == houseId) {
            arrayAdapter.add(message);
            mService.send.readHouse(houseId);
            newestMessageId = message.getId();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.sendMessage) {
            String messageString = inputMessage.getText().toString().trim();
            if (connected && mService.isConnected()) {
                if (!messageString.isEmpty()) {
                    Message message = new Message();
                    message.setHouseId(houseId);
                    message.setId(newestMessageId + 1);
                    message.setName(myProfile.getName());
                    message.setUserId(myProfile.getId());
                    message.setLocalId(localId);
                    message.setMessage(messageString);
                    message.setTime(System.currentTimeMillis() / 1000);
                    message.setHouseName(houseName);
                    mService.send.sendMessage(localId, houseId, messageString);
                    arrayAdapter.add(message);
                    localId++;
                    if (mute) {
                        muteHouse(false);
                    }
                    if(!favorite){
                        mService.send.favoriteHouse(houseId, true);
                        favorite = true;
                    }
                }
                inputMessage.setText("");
            }else{
                Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
            }
        }else if(v.getId() == R.id.inputMessage){
            getListView().smoothScrollBy(0, 0);
            getListView().setSelection(getListView().getCount() - 1);
        }
    }

    @Override
    public void newAvatar(int id, byte[] avatar, long time) {
        arrayAdapter.setBitmapById(id, avatar);
    }

    @Override
    public void removeFavorite(int id) {
        if (id == houseId) {
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_border_black_24dp);
            favorite = false;
            waitingForFavorite = false;
            muteMenuItem.setVisible(false);
            mute = true;
        }
    }

    @Override
    public void newFavorite(FavoriteItem favoriteItem) {
        if (favoriteItem.getUserId() == houseId) {
            favoriteMenuItem.setIcon(R.drawable.ic_favorite_black_24dp);
            favorite = true;
            waitingForFavorite = false;
            muteMenuItem.setVisible(true);
            muteMenuItem.setIcon(R.drawable.ic_volume_up_black_24dp);
            mute = false;
        }
    }

    public void muteHouse(boolean mute) {
        if (connected && mService.isConnected()) {
            if (mute) {
                muteMenuItem.setIcon(R.drawable.ic_volume_mute_black_24dp);
            } else {
                muteMenuItem.setIcon(R.drawable.ic_volume_up_black_24dp);
            }
            mService.send.muteHouse(houseId, mute);
            this.mute = mute;
        } else {
            Toast.makeText(this, "Not connected to server", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void favoriteError(String err) {
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        waitingForFavorite = false;
    }

    @Override
    public void deliveredMessage(int id) {
        arrayAdapter.changeMessageStatus(id, Message.DELIVERED);
    }

    @Override
    public void userReadMessage(int id) {
        arrayAdapter.changeMessageStatus(id, Message.READ);
    }

    @Override
    public void addOldMessages(ArrayList<Message> messageList) {
        int headerViewCount = getListView().getHeaderViewsCount();
        final int positionToSave = getListView().getFirstVisiblePosition() + messageList.size() + headerViewCount;
        View v = getListView().getChildAt(headerViewCount);
        final int top = (v == null) ? 0 : v.getTop();
        saveMessages(messageList);
        getListView().setSelectionFromTop(positionToSave, top);
        waitingForOldMessages = false;
        loadingView.setVisibility(View.INVISIBLE);
        for(int i = 0; i < messageList.size(); i++){
            Message message = messageList.get(i);
                int messageId = message.getId();
                if(messageId > newestMessageId){
                    newestMessageId = message.getId();
                }
                if(messageId < oldestMessageId){
                    oldestMessageId = messageId;
                }
        }
        if(messageList.size() == 0){
            olderMessages = false;
            getListView().removeHeaderView(loadingView);
        }
    }

    @Override
    public void addNewMessages(ArrayList<Message> messageList) {
        waitingForNewMessages = false;
        for(int i = 0; i < messageList.size(); i++){
            Message message = messageList.get(i);
            int messageId = message.getId();
            if(messageId > newestMessageId){
                newestMessageId = messageId;
            }
            if(messageId < oldestMessageId){
                oldestMessageId = messageId;
            }
        }
        if(messageList.size() >= 20){
            arrayAdapter.clear();
        }
        saveMessages(messageList);

        mService.send.readHouse(houseId);
    }

    private void saveMessages(ArrayList<Message> messageList){
        realm.beginTransaction();
        arrayAdapter.addAll(realm.copyToRealmOrUpdate(messageList));
        realm.commitTransaction();
    }

    @Override
    public void getMessageListFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
    }

    @Override
    public void joinedHouseSuccess() {
        getNewMessages();
    }

    @Override
    public void joinedHouseFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public void goToProfile(int userId) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.BUNDLE_ID, userId);
        startActivity(intent);
    }

    @Override
    public void goToHouse(int houseId, String name) {
        Intent intent = new Intent(this, HouseActivity.class);
        intent.putExtra(HouseActivity.MY_ID, myId);
        intent.putExtra(HouseActivity.EXTRA_ID, houseId);
        intent.putExtra(HouseActivity.HOUSE_NAME, name);
        intent.putExtra(HouseActivity.FAVORITE, mService.user.getFavorite(houseId));
        intent.putExtra(HouseActivity.MUTE, mService.user.getMute(houseId));
        startActivity(intent);
    }

    @Override
    public void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("machat_message", text);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public void sendMessageSuccess(Message message) {
        int localId = message.getLocalId();
        arrayAdapter.replaceByLocalId(localId, message);
        newestMessageId = message.getId();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(message);
        realm.commitTransaction();
    }

    @Override
    public void sendMessageFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_LONG).show();
    }

    @Override
    public void blockedBy(int userId) {
        Toast.makeText(this, "You have been blocked from this house", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

    @Override
    public void unBlockedBy(int userId) {

    }

    @Override
    public void updateMessageStatus(int id, int status) {
        arrayAdapter.changeMessageStatus(id, status);
    }

}
