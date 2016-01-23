package machat.machat.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import machat.machat.R;
import machat.machat.conf.SocketCommand;
import machat.machat.parsing.SocketParse;
import machat.machat.parsing.interfaces.OnCallbackRegister;
import machat.machat.util.SocketService;

public class RegisterActivity extends Activity implements View.OnClickListener, SocketActivity.SocketListener, TextWatcher, OnCallbackRegister {

    private EditText usernameView;

    private EditText passwordView;

    private EditText retypePasswordView;

    private EditText emailView;

    private Button registerButton;

    private TextView errorView;

    private ProgressBar progressBar;

    private SocketActivity socketActivity = new SocketActivity(this);

    private SocketService mService;

    private boolean connected = false;

    private String PASSWORD_MISMATCH = "Password and retype password do not match";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        socketActivity.setOnSocketListener(this);

        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        retypePasswordView = (EditText) findViewById(R.id.retypePassword);
        emailView = (EditText) findViewById(R.id.email);
        registerButton = (Button) findViewById(R.id.registerButton);
        errorView = (TextView) findViewById(R.id.error);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        registerButton.setOnClickListener(this);
        passwordView.addTextChangedListener(this);
        retypePasswordView.addTextChangedListener(this);
        usernameView.addTextChangedListener(this);
        emailView.addTextChangedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        socketActivity.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        socketActivity.disconnect();
    }

    @Override
    public void onClick(View v) {
        if (connected && mService.isConnected()) {
            String username = usernameView.getText().toString();
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            errorView.setText("");
            registerButton.setText("");
            registerButton.setEnabled(false);
            mService.send.registerAccount(username, email, password);
        }
    }

    @Override
    public void onConnect(SocketService mService) {
        connected = true;
        this.mService = mService;
    }

    @Override
    public void onDisconnect() {
        connected = false;
    }

    @Override
    public void onReceive(String command, String data) {
        if (command.equals(SocketCommand.REGISTER)) {
            SocketParse.parseRegister(data, this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String retypePassword = retypePasswordView.getText().toString();
        String password = passwordView.getText().toString();
        String username = usernameView.getText().toString();
        String email = emailView.getText().toString();
        if (password.equals(retypePassword)) {
            errorView.setText("");
            if (!username.isEmpty() && !email.isEmpty() && !password.isEmpty() && !retypePassword.isEmpty()) {
                registerButton.setVisibility(View.VISIBLE);
            }
        } else if (!retypePassword.isEmpty()) {
            errorView.setText(PASSWORD_MISMATCH);
            registerButton.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void registerSuccess() {
        Toast.makeText(this, "Registered Account", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void registerFailed(String err) {
        progressBar.setVisibility(View.INVISIBLE);
        registerButton.setText("Register");
        registerButton.setEnabled(true);
        errorView.setText(err);
    }
}
