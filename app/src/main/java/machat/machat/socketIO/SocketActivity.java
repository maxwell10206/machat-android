package machat.machat.socketIO;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import machat.machat.SocketService;

public class SocketActivity{

    private SocketListener socketListener;

    public interface SocketListener{

        void onConnect(SocketService mService);

        void onDisconnect();

        void onReceive(String command, String data);
    }

    public void setOnSocketListener(SocketListener socketListener){
        this.socketListener = socketListener;
    }

    Activity activity;

    public SocketActivity(Activity activity){
        this.activity = activity;
    }

    public SocketService mService;
    boolean mBound = false;

    private Receiver receiver = new Receiver();

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if(socketListener != null) {
                socketListener.onConnect(mService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            if(socketListener != null) {
                socketListener.onDisconnect();
            }
        }
    };

    public void connect() {
        LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, new IntentFilter(SocketService.ACTION));

        Intent intent = new Intent(activity, SocketService.class);
        activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnect(){
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
        if(mBound) {
            activity.unbindService(mConnection);
            mBound = false;
        }
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra(SocketService.COMMAND);
            String data = intent.getStringExtra(SocketService.DATA);
            if(socketListener != null) {
                socketListener.onReceive(command, data);
            }
            if(command.equals(Socket.EVENT_DISCONNECT)){
                Toast.makeText(activity, "Lost Connection", Toast.LENGTH_SHORT).show();
            } else if (command.equals(Socket.EVENT_ERROR)) {
                Toast.makeText(activity, "Error connecting to server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
