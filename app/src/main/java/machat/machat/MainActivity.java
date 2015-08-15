package machat.machat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import machat.machat.socketIO.OnLoginListener;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

public class MainActivity extends Activity implements SocketActivity.SocketListener, OnLoginListener {

    boolean finished = false;
    private SocketActivity socketActivity = new SocketActivity(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socketActivity.setOnSocketListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        socketActivity.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        socketActivity.disconnect();
    }

    @Override
    public void onConnect(SocketService mService) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sessionId = sharedPref.getString(SavedPrefs.sessionId, "");

        if (mService.user.isLogin() || (!sessionId.isEmpty() && !mService.isConnected())) {
            startFavoriteListActivity(mService.user.getMyProfile().getId());
        } else if (sessionId.isEmpty()) {
            startLoginOrRegisterActivity();
        }
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
    public void onLoginSuccess(MyProfile myProfile) {
        startFavoriteListActivity(myProfile.getId());
    }

    @Override
    public void onLoginFailed(String err) {
        startLoginOrRegisterActivity();
    }

    public void startLoginOrRegisterActivity() {
        startActivity(new Intent(this, LoginOrRegisterActivity.class));
        finish();
        finished = true;
    }

    public void startFavoriteListActivity(int myId) {
        startActivity(new Intent(this, FavoriteListActivity.class).putExtra(FavoriteListActivity.MY_ID, myId));
        finish();
        finished = true;
    }
}
