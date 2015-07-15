package machat.machat.socketIO;

/**
 * Created by Admin on 6/25/2015.
 */
public interface OnCallbackSendAvatar {

    void avatarUploadSuccess();

    void avatarUploadFailed(String err);
}
