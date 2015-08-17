package machat.machat.main.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

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
