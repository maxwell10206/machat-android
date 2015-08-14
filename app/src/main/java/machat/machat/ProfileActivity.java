package machat.machat;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.OnCallbackAvatar;
import machat.machat.socketIO.OnCallbackBlock;
import machat.machat.socketIO.OnLoginListener;
import machat.machat.socketIO.OnNewProfile;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

/**
 * Created by Admin on 6/18/2015.
 */
public class ProfileActivity extends Activity implements SocketActivity.SocketListener, OnCallbackBlock, OnLoginListener, CompoundButton.OnCheckedChangeListener, OnNewProfile, OnCallbackAvatar {

    public static final String BUNDLE_ID = "id";
    private final String TITLE_PROFILE = "Profile";
    private SocketActivity socketActivity = new SocketActivity(this);
    private SocketService mService;
    private Profile profile;
    private boolean connected = false;
    private boolean waitingForBlock = false;
    private TextView name;
    private TextView username;
    private ProgressBar progressBar;
    private CheckBox blockCheckBox;
    private ImageView avatarView;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        socketActivity.setOnSocketListener(this);
        getActionBar().setTitle(TITLE_PROFILE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getExtras().getInt(BUNDLE_ID);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        name = (TextView) findViewById(R.id.name);
        username = (TextView) findViewById(R.id.username);
        blockCheckBox = (CheckBox) findViewById(R.id.blockCheckBox);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        avatarView = (ImageView) findViewById(R.id.avatar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public void onConnect(SocketService mService) {
        this.mService = mService;
        connected = true;
        onLoginSuccess(mService.user.getMyProfile());
    }

    @Override
    public void onDisconnect() {
        connected = false;
    }

    @Override
    public void onReceive(String command, String data) {
        if (command.equals(SocketCommand.GET_PROFILE)) {
            SocketParse.parseProfile(data, this);
        } else if (command.equals(SocketCommand.BLOCK_USER)) {
            SocketParse.parseBlockUser(data, this);
        } else if (command.equals(SocketCommand.GET_AVATAR)) {
            SocketParse.parseGetAvatar(data, this);
        } else if (command.equals(SocketCommand.LOGIN)) {
            SocketParse.parseLogin(data, this);
        }
    }

    @Override
    public void newProfile(Profile profile) {
        if (profile.getId() == id) {
            this.profile = profile;
            blockCheckBox.setOnCheckedChangeListener(this);
            name.setText(profile.getName());
            username.setText(profile.getUsername());
            progressBar.setVisibility(View.GONE);
            blockCheckBox.setChecked(profile.isBlocked());
        }
    }

    @Override
    public void newAvatar(int id, final byte[] avatar, long time) {
        if (id == this.id) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    avatarView.setImageBitmap(BitmapFactory.decodeByteArray(avatar, 0, avatar.length));
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (connected && mService.isConnected() && !waitingForBlock) {
            if (profile.isBlocked() != isChecked) {
                mService.send.blockUser(id, isChecked);
            }
        }
    }

    @Override
    public void onLoginSuccess(MyProfile myProfile) {
        mService.send.getProfile(id);
        AvatarManager.getAvatar(id, this);
    }

    @Override
    public void onLoginFailed(String err) {

    }

    @Override
    public void unBlocked(int id) {
        if (id == this.id) {
            profile.setBlocked(false);
            blockCheckBox.setChecked(false);
            waitingForBlock = false;
        }
    }

    @Override
    public void blocked(BlockUser blockUser) {
        if (blockUser.getId() == this.id) {
            profile.setBlocked(true);
            blockCheckBox.setChecked(true);
            waitingForBlock = false;
        }
    }
}
