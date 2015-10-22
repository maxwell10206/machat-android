package machat.machat.main.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import io.realm.Realm;
import io.realm.RealmResults;
import machat.machat.models.BlockUser;
import machat.machat.models.MyProfile;
import machat.machat.models.Profile;
import machat.machat.R;
import machat.machat.util.AvatarManager;
import machat.machat.util.SocketService;
import machat.machat.parsing.interfaces.OnCallbackAvatar;
import machat.machat.parsing.interfaces.OnCallbackBlock;
import machat.machat.parsing.interfaces.OnLoginListener;
import machat.machat.parsing.interfaces.OnNewProfile;
import machat.machat.conf.SocketCommand;
import machat.machat.parsing.SocketParse;

public class ProfileActivity extends Activity implements View.OnClickListener, SocketActivity.SocketListener, OnCallbackBlock, OnLoginListener, CompoundButton.OnCheckedChangeListener, OnNewProfile, OnCallbackAvatar {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String NAME = "name";
    private final String TITLE_PROFILE = "Profile";
    private SocketActivity socketActivity = new SocketActivity(this);
    private SocketService mService;
    private Profile profile;
    private boolean connected = false;
    private boolean waitingForBlock = false;
    private TextView nameView;
    private TextView usernameView;
    private CheckBox blockCheckBox;
    private ImageView avatarView;
    private int id;
    private String name;
    private String username;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        socketActivity.setOnSocketListener(this);
        getActionBar().setTitle(TITLE_PROFILE);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getExtras().getInt(USER_ID);
        name = getIntent().getExtras().getString(NAME);
        username = getIntent().getExtras().getString(USERNAME);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        nameView = (TextView) findViewById(R.id.name);
        usernameView = (TextView) findViewById(R.id.username);

        nameView.setText(name);
        usernameView.setText(username);

        blockCheckBox = (CheckBox) findViewById(R.id.blockCheckBox);
        avatarView = (ImageView) findViewById(R.id.avatar);
        avatarView.setOnClickListener(this);
        realm = Realm.getDefaultInstance();

        RealmResults<BlockUser> blockUsers = realm.where(BlockUser.class).findAll();
        for(int i = 0; i < blockUsers.size(); i++){
            BlockUser blockUser = blockUsers.get(i);
            if(blockUser.getId() == id){
                blockCheckBox.setChecked(true);
            }
        }
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
            nameView.setText(profile.getName());
            usernameView.setText(profile.getUsername());
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

    @Override
    public void onClick(View v) {
        Bitmap bitmap = ((BitmapDrawable) avatarView.getDrawable()).getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Intent intent = new Intent(this, AvatarActivity.class);
        intent.putExtra(AvatarActivity.AVATAR, byteArray);
        startActivity(intent);
    }
}
