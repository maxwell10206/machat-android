package machat.machat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Admin on 5/22/2015.
 */
public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, SocketService.class);
        context.startService(startServiceIntent);
    }

}
