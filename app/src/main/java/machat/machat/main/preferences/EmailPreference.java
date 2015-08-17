package machat.machat.main.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import machat.machat.R;

public class EmailPreference extends DialogPreference {

    private EditText passwordText;
    private EditText emailText;
    private String email;

    public EmailPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.email_preference_dialog);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    protected void onBindDialogView(@NonNull View view) {
        super.onBindDialogView(view);

        passwordText = (EditText) view.findViewById(R.id.password);
        emailText = (EditText) view.findViewById(R.id.email);
        emailText.setText(email);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            callChangeListener(new EmailChange(passwordText.getText().toString(), emailText.getText().toString()));
        }
    }

    public class EmailChange {

        private String password;

        private String email;

        public EmailChange(String password, String email) {
            this.password = password;
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public String getEmail() {
            return email;
        }
    }
}
