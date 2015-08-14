package machat.machat;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by Admin on 7/1/2015.
 */
public class LogoutDialogPreference extends DialogPreference {

    public LogoutDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            callChangeListener(null);
        }
    }
}
