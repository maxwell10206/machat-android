package machat.machat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import machat.machat.socketIO.AvatarManager;
import machat.machat.socketIO.OnCallbackAvatar;
import machat.machat.socketIO.OnCallbackSendAvatar;
import machat.machat.socketIO.OnChangeEmail;
import machat.machat.socketIO.OnChangeName;
import machat.machat.socketIO.OnChangePassword;
import machat.machat.socketIO.SocketActivity;
import machat.machat.socketIO.SocketCommand;
import machat.machat.socketIO.SocketParse;

/**
 * Created by Admin on 6/21/2015.
 */
public class MyProfileActivity extends Activity implements View.OnClickListener, OnCallbackSendAvatar, SocketActivity.SocketListener, OnCallbackAvatar, OptionsFragment.OnPreferenceChange, OnChangePassword, OnChangeEmail, OnChangeName{

    OptionsFragment fragment = new OptionsFragment();

    SocketActivity socketActivity = new SocketActivity(this);

    private SocketService mService;

    private boolean connected = false;

    private MyProfile myProfile;

    private TextView nameView;
    private TextView usernameView;
    private ImageView avatarView;
    private Button uploadAvatar;
    private TextView versionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        socketActivity.setOnSocketListener(this);
        setContentView(R.layout.activity_my_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        nameView = (TextView) findViewById(R.id.name);
        usernameView = (TextView) findViewById(R.id.username);
        avatarView = (ImageView) findViewById(R.id.avatar);
        uploadAvatar = (Button) findViewById(R.id.uploadAvatar);
        versionView = (TextView) findViewById(R.id.version);
        uploadAvatar.setOnClickListener(this);
        getFragmentManager().beginTransaction().add(R.id.preferences, fragment).commit();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionView.setText("machat v" + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnect(SocketService newService) {
        mService = newService;
        connected = true;
        myProfile = mService.user.getMyProfile();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(SavedPrefs.name, myProfile.getName()).commit();
        AvatarManager.getAvatar(myProfile.getId(), this);
        nameView.setText(myProfile.getName());
        usernameView.setText(myProfile.getUsername());
    }

    @Override
    public void onDisconnect() {
        connected = false;
    }

    @Override
    public void onReceive(String command, String data) {
        if(command.equals(SocketCommand.CHANGE_NAME)){
            SocketParse.parseChangeName(data, this);
        }else if(command.equals(SocketCommand.CHANGE_PASSWORD)){
            SocketParse.parseChangePassword(data, this);
        }else if(command.equals(SocketCommand.CHANGE_EMAIL)){
            SocketParse.parseChangeEmail(data, this);
        }else if(command.equals(SocketCommand.GET_AVATAR)){
            SocketParse.parseGetAvatar(data, this);
        }else if(command.equals(SocketCommand.SEND_AVATAR)){
            SocketParse.parseSendAvatar(data, this);
        }
    }

    @Override
    public void changeName(String name) {
        if(connected && mService.isConnected()){
            if(name.equals(mService.user.getMyProfile().getName())) {
                Toast.makeText(this, "Name is same as current one", Toast.LENGTH_SHORT).show();
            }else{
                mService.send.changeName(name, this);
            }
        }
    }

    @Override
    public void changeEmail(String password, String email) {
        if(connected && mService.isConnected()){
            if(email.equals(mService.user.getMyProfile().getEmail())){
                Toast.makeText(this, "Email is same as current one", Toast.LENGTH_SHORT).show();
            }else {
                mService.send.changeEmail(password, email, this);
            }
        }
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        if(connected && mService.isConnected()){
            mService.send.changePassword(oldPassword, newPassword);
        }
    }

    @Override
    public void logout() {
        mService.send.logout(myProfile.getSessionId());
        Intent intent = new Intent(this, LoginOrRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void passwordChangeSuccess() {
        Toast.makeText(this, "Password changed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void passwordChangeFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeEmailSuccess(String email) {
        Toast.makeText(this, "Email changed successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeEmailFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeNameSuccess(String name) {
        nameView.setText(name);
        Toast.makeText(this, "Name changed successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void changeNameFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void newAvatar(int id, final byte[] avatar, long time) {
        if(id == myProfile.getId()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    avatarView.setImageBitmap(User.getBitmapAvatar(avatar));
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Crop.pickImage(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(result.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, result);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Bitmap b = rotateBitmap(Crop.getOutput(result).getPath());
            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, b.getWidth(), b.getHeight()), new RectF(0, 0, 256, 256), Matrix.ScaleToFit.CENTER);
            Bitmap bitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
            myProfile.setAvatar(bitmap);
            avatarView.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            mService.send.sendAvatar(byteArray);
            AvatarManager.newAvatar(myProfile.getId(), byteArray, System.currentTimeMillis());

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public Bitmap rotateBitmap(String src) {
        Bitmap bitmap = BitmapFactory.decodeFile(src);
        try {
            ExifInterface exif = new ExifInterface(src);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                case ExifInterface.ORIENTATION_UNDEFINED:
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void avatarUploadSuccess() {
        Toast.makeText(this, "Avatar successfully updated", Toast.LENGTH_SHORT);
    }

    @Override
    public void avatarUploadFailed(String err) {
        Toast.makeText(this, err, Toast.LENGTH_LONG);
    }
}
