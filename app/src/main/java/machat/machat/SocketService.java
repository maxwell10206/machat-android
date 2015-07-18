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
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.ServiceReceiver;
import machat.machat.socketIO.TimeConvert;

/**
 * Created by Admin on 5/16/2015.
 */
public class SocketService extends Service {

    public SocketCommunication send;

    public MachatNotificationManager machatNotificationManager;

    private final String address = "https://www.machat.us:443/";
    //https://www.machat.us:443
    //https://192.168.1.127:443/

    private IO.Options opts;

    private Socket mSocket;

    public ServiceReceiver user;

    private final IBinder mBinder = new LocalBinder();

    public static String ACTION = "machat.action.SERVER";

    public final static String COMMAND = "Command";
    public final static String DATA = "Data";

    public boolean isConnected() {
        return mSocket.connected();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }

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

    public static class RelaxedHostNameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    @Override
    public void onCreate (){
        super.onCreate();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            IO.setDefaultSSLContext(sc);
            HttpsURLConnection.setDefaultHostnameVerifier(new RelaxedHostNameVerifier());
            opts = new IO.Options();
            opts.sslContext = sc;
            opts.secure = true;
            mSocket = IO.socket(address, opts);
            send = new SocketCommunication(this, mSocket);
            machatNotificationManager = new MachatNotificationManager(this);
            user = new ServiceReceiver(this);
            AvatarManager.setSocketCommunication(send);
            TimeConvert.setContext(this);
            mSocket.connect();
            LocalBroadcastManager.getInstance(this).registerReceiver(user, new IntentFilter(SocketService.ACTION));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSocket.disconnect();
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
