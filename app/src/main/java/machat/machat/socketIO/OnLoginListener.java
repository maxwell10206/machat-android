package machat.machat.socketIO;

import machat.machat.MyProfile;

/**
 * Created by Admin on 5/27/2015.
 */
public interface OnLoginListener {

    void onLoginSuccess(MyProfile myProfile);

    void onLoginFailed(String err);

}
