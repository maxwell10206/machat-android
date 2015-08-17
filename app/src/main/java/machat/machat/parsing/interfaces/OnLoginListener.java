package machat.machat.parsing.interfaces;

import machat.machat.models.MyProfile;

public interface OnLoginListener {

    void onLoginSuccess(MyProfile myProfile);

    void onLoginFailed(String err);

}
