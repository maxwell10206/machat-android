package machat.machat.socketIO;

/**
 * Created by Admin on 6/24/2015.
 */
public interface OnChangeEmail {

    void changeEmailSuccess(String email);

    void changeEmailFailed(String err);
}
