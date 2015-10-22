package machat.machat.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import machat.machat.util.SocketService;

/**
 * Created by Maxwell on 10/21/2015.
 */
public class ScreenOnReceiver extends BroadcastReceiver {

    private SocketService mService;

    public ScreenOnReceiver(SocketService mService) {
        this.mService = mService;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            mService.forceReconnect();
        }

    }
}
