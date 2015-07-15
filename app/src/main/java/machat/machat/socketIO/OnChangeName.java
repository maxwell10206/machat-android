package machat.machat.socketIO;

/**
 * Created by Admin on 6/24/2015.
 */
public interface OnChangeName {

    void changeNameSuccess(String name);

    void changeNameFailed(String err);

}
