package machat.machat.socketIO;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Date;

/**
 * Created by Admin on 10/20/2014.
 */
public class TimeConvert {

    private static Context context;

    public static void setContext(Context context){
        TimeConvert.context = context;
    }

    public static String toDate(long time){
        String formattedDate = DateUtils.getRelativeTimeSpanString(time * 1000, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        return formattedDate;
    }

    public static String formatDate(long time){
        //time = time * 1000;
        //String dateString = DateFormat.getDateTimeInstance().format(time);
        Date d = new Date();
        String s;
        if(d.getTime() - (1000 * 60 * 60 * 24) < time) {
            //s = DateFormat.format("MMM d, yyyy hh:mm a", time);
            s = android.text.format.DateFormat.getTimeFormat(context).format(time).toString();
        }else{
            s = android.text.format.DateFormat.getTimeFormat(context).format(time).toString();
            s += ", " + android.text.format.DateFormat.getDateFormat(context).format(time).toString();
        }

        return s.toString();
    }
}
