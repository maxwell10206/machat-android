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
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.BlockReceiver;
import machat.machat.socketIO.FavoriteReceiver;
import machat.machat.socketIO.HouseReceiver;
import machat.machat.socketIO.ServiceReceiver;
import machat.machat.socketIO.TimeConvert;

public class SocketService extends Service {

    public final static String COMMAND = "Command";
    public final static String DATA = "Data";
    public static String ACTION = "machat.action.SERVER";
    //http://www.machat.us:3000
    //http://192.168.1.125:3000/
    private final String address = "http://www.machat.us:3000";
    private final IBinder mBinder = new LocalBinder();
    public SocketCommunication send;
    public MachatNotificationManager machatNotificationManager;
    public ServiceReceiver user;
    public FavoriteReceiver favorites;
    public HouseReceiver houseReceiver;
    public BlockReceiver blockReceiver;
    private IO.Options opts;
    private Socket mSocket;
    private TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    }};

    public boolean isConnected() {
        return mSocket.connected();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            /*/ SSL doesn't work cause nkzawa sucks?
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            IO.setDefaultSSLContext(sc);
            HttpsURLConnection.setDefaultHostnameVerifier(new RelaxedHostNameVerifier());
            /*/
            opts = new IO.Options();
            //opts.sslContext = sc;
            //opts.secure = true;
            mSocket = IO.socket(address, opts);
            send = new SocketCommunication(this, mSocket);
            machatNotificationManager = new MachatNotificationManager(this);
            user = new ServiceReceiver(this);
            favorites = new FavoriteReceiver(this);
            houseReceiver = new HouseReceiver(this);
            blockReceiver = new BlockReceiver(this);
            AvatarManager.setSocketService(this);
            TimeConvert.setContext(this);
            mSocket.connect();
            LocalBroadcastManager.getInstance(this).registerReceiver(user, new IntentFilter(ACTION));
            LocalBroadcastManager.getInstance(this).registerReceiver(favorites, new IntentFilter(ACTION));
            LocalBroadcastManager.getInstance(this).registerReceiver(houseReceiver, new IntentFilter(ACTION));
            LocalBroadcastManager.getInstance(this).registerReceiver(blockReceiver, new IntentFilter(ACTION));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        send.turnOffListeners();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(user);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(favorites);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(houseReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(blockReceiver);
    }

    public void sendBroadcast(String command, String data) {
        Intent intent = new Intent(SocketService.ACTION);
        intent.putExtra(DATA, data);
        intent.putExtra(COMMAND, command);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public static class RelaxedHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SocketService.this;
        }
    }
}
