package machat.machat;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import machat.machat.socketIO.OnCallbackBlock;
import machat.machat.socketIO.OnCallbackBlockList;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

/**
 * Created by Admin on 7/8/2015.
 */
public class BlockListActivity extends ListActivity implements SocketActivity.SocketListener, OnCallbackBlockList, BlockItemDialog.BlockChange, OnCallbackBlock{

    public static String TITLE = "Block List";

    private SocketActivity socketActivity = new SocketActivity(this);

    private SocketService mService;

    BlockListArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        socketActivity.setOnSocketListener(this);
        getActionBar().setTitle(TITLE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_block_list);
        arrayAdapter = new BlockListArrayAdapter(this, new ArrayList<User>());
        setListAdapter(arrayAdapter);
        getListView().setEmptyView(findViewById(R.id.empty_view));
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long i){
        User blockUser = arrayAdapter.getItem(position);
        BlockItemDialog blockItemDialog = new BlockItemDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(BlockItemDialog.ID, blockUser.getId());
        bundle.putString(BlockItemDialog.NAME, blockUser.getName());
        blockItemDialog.setArguments(bundle);
        blockItemDialog.show(getFragmentManager(), "dialog");
    }


    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        socketActivity.connect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        socketActivity.disconnect();
    }

    @Override
    public void onConnect(SocketService mService) {
        this.mService = mService;
        mService.send.getBlockList();
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onReceive(String command, String data) {
        if(command.equals(SocketCommand.GET_BLOCK_LIST)){
            SocketParse.parseBlockList(data, this);
        }else if(command.equals(SocketCommand.BLOCK_USER)){
            SocketParse.parseBlockUser(data, this);
        }
    }

    @Override
    public void newBlockList(ArrayList<User> blockedUsers) {
        arrayAdapter.clear();
        arrayAdapter.addAll(blockedUsers);
    }

    @Override
    public void unBlock(int id) {
        mService.send.blockUser(id, false);
    }

    @Override
    public void callbackBlock(int id, boolean block) {
        if(!block){
            arrayAdapter.removeById(id);
        }
    }
}
