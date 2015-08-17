package machat.machat.tools;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Date;

public class TimeConvert {

    private static Context context;

    public static void setContext(Context context) {
        TimeConvert.context = context;
    }

    public static String formatDate(long time) {
        //time = time * 1000;
        //String dateString = DateFormat.getDateTimeInstance().format(time);
        Date d = new Date();
        String s;
        if (d.getTime() - (1000 * 60 * 60 * 24) < time) {
            //s = DateFormat.format("MMM d, yyyy hh:mm a", time);
            s = android.text.format.DateFormat.getTimeFormat(context).format(time);
        } else {
            s = android.text.format.DateFormat.getTimeFormat(context).format(time);
            s += ", " + android.text.format.DateFormat.getDateFormat(context).format(time);
        }

        return s;
    }
}
