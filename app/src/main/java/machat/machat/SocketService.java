package machat.machat;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.ServiceReceiver;
import machat.machat.socketIO.TimeConvert;

/**
 * Created by Admin on 5/16/2015.
 */
public class SocketService extends Service {

    public SocketCommunication send;

    public MachatNotificationManager machatNotificationManager;

    private Socket socket;

    public ServiceReceiver user;

    private final IBinder mBinder = new LocalBinder();

    private final String address = "http://www.machat.us:3000";
    //http://www.machat.us:3000
    //http://192.168.1.127:3000/

    public static String ACTION = "machat.action.SERVER";

    public final static String COMMAND = "Command";
    public final static String DATA = "Data";

    public boolean isConnected(){ return socket.connected(); }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

    @Override
    public void onCreate (){
        super.onCreate();
        try {
            IO.Options opts = new IO.Options();
            socket = IO.socket(address, opts);
            send = new SocketCommunication(this, socket);
            machatNotificationManager = new MachatNotificationManager(this);
            user = new ServiceReceiver(this);
            AvatarManager.setSocketCommunication(send);
            TimeConvert.setContext(this);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(user, new IntentFilter(SocketService.ACTION));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        socket.disconnect();
        send.turnOffListeners();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(user);
    }

    public void sendBroadcast(String command, String data){
        Intent intent = new Intent(SocketService.ACTION);
        intent.putExtra(DATA, data);
        intent.putExtra(COMMAND, command);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }
}
