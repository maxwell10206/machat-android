package machat.machat.main.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import io.socket.client.Socket;
import machat.machat.util.SocketService;

public class SocketActivity {

    public SocketService mService;
    Activity activity;
    boolean mBound = false;
    private SocketListener socketListener;
    private Receiver receiver = new Receiver();
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;

            if (socketListener != null) {
                socketListener.onConnect(mService);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
            if (socketListener != null) {
                socketListener.onDisconnect();
            }
        }
    };
    public SocketActivity(Activity activity) {
        this.activity = activity;
    }

    public void setOnSocketListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    public void connect() {
        activity.registerReceiver(receiver, new IntentFilter(SocketService.ACTION));
        Intent intent = new Intent(activity, SocketService.class);
        activity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void disconnect() {
        try {
            activity.unregisterReceiver(receiver);
        }catch(IllegalArgumentException e){
            e.printStackTrace();
        }
        if (mBound) {
            activity.unbindService(mConnection);
            mBound = false;
        }
    }

    public interface SocketListener {

        void onConnect(SocketService mService);

        void onDisconnect();

        void onReceive(String command, String data);
    }

    private class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra(SocketService.COMMAND);
            String data = intent.getStringExtra(SocketService.DATA);
            if (socketListener != null) {
                socketListener.onReceive(command, data);
            }
            if (command.equals(Socket.EVENT_DISCONNECT)) {
                Toast.makeText(activity, "Lost Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
