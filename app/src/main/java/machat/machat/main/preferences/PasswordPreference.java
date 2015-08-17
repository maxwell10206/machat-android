package machat.machat.main.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import machat.machat.R;

public class PasswordPreference extends DialogPreference {

    private TextView oldPassword;
    private TextView newPassword;
    private TextView retypePassword;

    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.password_preference_dialog);
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);
        oldPassword = (TextView) view.findViewById(R.id.oldPassword);
        newPassword = (TextView) view.findViewById(R.id.newPassword);
        retypePassword = (TextView) view.findViewById(R.id.retypePassword);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            callChangeListener(new PasswordChange(oldPassword.getText().toString(), newPassword.getText().toString(), retypePassword.getText().toString()));
        }
    }

    public class PasswordChange {

        private String oldPassword;

        private String newPassword;

        private String retypePassword;

        PasswordChange(String oldPassword, String newPassword, String retypePassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.retypePassword = retypePassword;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public String getRetypePassword() {
            return retypePassword;
        }
    }

}
