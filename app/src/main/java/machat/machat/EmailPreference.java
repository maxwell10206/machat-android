package machat.machat;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Admin on 7/7/2015.
 */
public class EmailPreference extends DialogPreference {

    public class EmailChange{

        private String password;

        private String email;

        public EmailChange(String password, String email){
            this.password = password;
            this.email = email;
        }

        public String getPassword(){ return password; }

        public String getEmail(){ return email; }
    }

    private EditText passwordText;

    private EditText emailText;

    private String email;

    public void setEmail(String email){
        this.email = email;
    }

    public EmailPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.email_preference_dialog);
    }

    @Override
    protected void onBindDialogView (View view){
        super.onBindDialogView(view);

        passwordText = (EditText) view.findViewById(R.id.password);
        emailText = (EditText) view.findViewById(R.id.email);
        emailText.setText(email);
    }

    @Override
    protected void onDialogClosed (boolean positiveResult){
        if(positiveResult){
            callChangeListener(new EmailChange(passwordText.getText().toString(), emailText.getText().toString()));
        }
    }
}
