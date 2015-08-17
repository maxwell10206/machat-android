package machat.machat.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import machat.machat.util.SocketService;

public class StartServiceReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, SocketService.class);
        context.startService(startServiceIntent);
    }

}
