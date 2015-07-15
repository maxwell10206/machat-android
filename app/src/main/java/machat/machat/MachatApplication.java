package machat.machat;

import android.app.Application;
import android.content.Intent;

/**
 * Created by Admin on 5/27/2015.
 */
public class MachatApplication extends Application{

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static boolean activityVisible;

    @Override
    public void onCreate(){
        super.onCreate();
        startService(new Intent(this, SocketService.class));
    }

}
