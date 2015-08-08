package machat.machat.socketIO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.nkzawa.socketio.client.Socket;

import java.util.ArrayList;

import io.realm.Realm;
import machat.machat.FavoriteItem;
import machat.machat.MachatApplication;
import machat.machat.Message;
import machat.machat.MissedMessage;
import machat.machat.MyProfile;
import machat.machat.SavedPrefs;
import machat.machat.SocketService;

/**
 * Created by Admin on 6/28/2015.
 */
public class ServiceReceiver extends BroadcastReceiver implements OnChangeEmail, OnChangeName, OnLoginListener{

    private SocketService mService;

    private MachatApplication application;

    private SharedPreferences sharedPref;

    private MyProfile myProfile;

    private boolean login = false;

    private Realm realm;

    public boolean isLogin() {
        return login;
    }

    public ServiceReceiver(SocketService mService){
        this.application = (MachatApplication)mService.getApplication();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(mService);
        this.mService = mService;
    }

    public MyProfile getMyProfile() {
        if (myProfile == null) {
            int id = sharedPref.getInt(SavedPrefs.id, 0);
            String username = sharedPref.getString(SavedPrefs.username, "");
            String sessionId = sharedPref.getString(SavedPrefs.sessionId, "");
            String name = sharedPref.getString(SavedPrefs.name, "");
            String email = sharedPref.getString(SavedPrefs.email, "");

            return new MyProfile(id, username, name, email, sessionId);
        } else {
            return myProfile;
        }
    }

    @Override
    public void onLoginSuccess(MyProfile myProfile) {

        login = true;
        this.myProfile = myProfile;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SavedPrefs.username, myProfile.getUsername());
        editor.putString(SavedPrefs.sessionId, myProfile.getSessionId());
        editor.putString(SavedPrefs.name, myProfile.getName());
        editor.putInt(SavedPrefs.id, myProfile.getId());
        editor.putString(SavedPrefs.email, myProfile.getEmail());
        editor.commit();
        mService.send.getUndeliveredMessages();
        mService.send.getFavoriteList();
        AvatarManager.checkForUpdates();
    }

    @Override
    public void onLoginFailed(String err) {
        login = false;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(SavedPrefs.sessionId);
        editor.commit();
    }

    public void logout(){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(SavedPrefs.sessionId);
        editor.commit();
        login = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra(SocketService.COMMAND);
        String data = intent.getStringExtra(SocketService.DATA);
        if(command.equals(SocketCommand.LOGOUT)){
            logout();
        }else if(command.equals(Socket.EVENT_CONNECT)){
            String sessionId = sharedPref.getString(SavedPrefs.sessionId, "");
            if (!sessionId.isEmpty()) {
                mService.send.loginSession(sessionId);
            }
        }else if(command.equals(SocketCommand.LOGIN)){
            SocketParse.parseLogin(data, this);
            AvatarManager.reDownload();
        }else if(command.equals(SocketCommand.LOGIN)){
            SocketParse.parseLogin(data, this);
        }else if(command.equals(SocketCommand.CHANGE_NAME)){
            SocketParse.parseChangeName(data, this);
        }else if(command.equals(SocketCommand.CHANGE_EMAIL)){
            SocketParse.parseChangeEmail(data, this);
        }
    }

    @Override
    public void changeEmailSuccess(String email) {
        myProfile.setEmail(email);
    }

    @Override
    public void changeEmailFailed(String err) {

    }

    @Override
    public void changeNameSuccess(String name) {
        myProfile.setName(name);
    }

    @Override
    public void changeNameFailed(String err) {

    }
}
