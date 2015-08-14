package machat.machat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import machat.machat.socketIO.OnLoginListener;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

/**
 * Created by Admin on 5/25/2015.
 */
public class LoginActivity extends Activity implements OnLoginListener, SocketActivity.SocketListener, TextWatcher, View.OnClickListener {

    TextView errorText;

    EditText usernameView;

    EditText password;

    Button loginButton;

    ProgressBar progressBar;

    SocketService service;

    SocketActivity socketActivity = new SocketActivity(this);

    public void onConnect(SocketService service) {
        this.service = service;

        loginButton.setOnClickListener(this);
    }

    public void onDisconnect() {

    }

    public void onReceive(String command, String data){
        if(command.equals(SocketCommand.LOGIN)){
            SocketParse.parseLogin(data, this);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        socketActivity.disconnect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        socketActivity.setOnSocketListener(this);

        errorText = (TextView) findViewById(R.id.error);
        usernameView = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        String username = PreferenceManager.getDefaultSharedPreferences(this).getString(SavedPrefs.username, "");
        if(!username.isEmpty()){
            usernameView.setText(username);
        }

        usernameView.addTextChangedListener(this);
        password.addTextChangedListener(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        socketActivity.connect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        socketActivity.disconnect();
    }

    @Override
    public void onClick(View v){
        if(service.isConnected()) {
            service.send.login(usernameView.getText().toString(), password.getText().toString());
            loginButton.setEnabled(false);
            loginButton.setText("");
            progressBar.setVisibility(View.VISIBLE);
        }else{
            errorText.setText("Could not connect to server.");
        }
    }

    @Override
    public void onLoginSuccess(MyProfile myProfile) {
        Intent intent = new Intent(this, FavoriteListActivity.class);
        intent.putExtra(FavoriteListActivity.MY_ID, myProfile.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void onLoginFailed(String err){
        errorText.setText(err);
        password.setText("");
        loginButton.setEnabled(true);
        loginButton.setText("Login");
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(usernameView.getText().length() != 0 && password.getText().length() != 0){
            loginButton.setVisibility(View.VISIBLE);
        }else{
            loginButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
