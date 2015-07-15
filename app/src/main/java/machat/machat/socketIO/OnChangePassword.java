package machat.machat.socketIO;

/**
 * Created by Admin on 6/22/2015.
 */
public interface OnChangePassword {

    void passwordChangeSuccess();

    void passwordChangeFailed(String err);
}
