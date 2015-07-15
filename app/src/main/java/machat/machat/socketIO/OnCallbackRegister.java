package machat.machat.socketIO;

/**
 * Created by Admin on 7/1/2015.
 */
public interface OnCallbackRegister {

    void registerSuccess();

    void registerFailed(String err);
}
