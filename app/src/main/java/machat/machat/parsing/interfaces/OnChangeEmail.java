package machat.machat.parsing.interfaces;

public interface OnChangeEmail {

    void changeEmailSuccess(String email);

    void changeEmailFailed(String err);
}
