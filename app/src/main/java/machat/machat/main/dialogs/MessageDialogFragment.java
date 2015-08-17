package machat.machat.main.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import machat.machat.R;

public class MessageDialogFragment extends DialogFragment {

    public static final String NAME = "name";

    public static final String USERNAME = "username";

    public static final String ID = "id";

    public static final String MESSAGE_ID = "messageId";

    public static final String MY_ID = "myId";

    public static final String HOUSE_ID = "houseId";

    public static final String MESSAGE = "message";
    private Action action;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            this.action = (Action) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String name = getArguments().getString(NAME);
        int myId = getArguments().getInt(MY_ID);
        int houseId = getArguments().getInt(HOUSE_ID);
        final int id = getArguments().getInt(ID);
        final int messageId = getArguments().getInt(MESSAGE_ID);
        final String username = getArguments().getString(USERNAME);
        final String message = getArguments().getString(MESSAGE);
        builder.setTitle(name);
        if (myId == id) {
            builder.setItems(R.array.message_dialog_options_short, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        action.copyText(message);
                    }
                }
            });
        } else {
            builder.setItems(R.array.message_dialog_options_full, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        action.copyText(message);
                    } else {
                        action.goToProfile(id, name, username);
                    }
                }
            });
        }
        return builder.create();
    }

    public interface Action {

        void goToProfile(int userId, String name, String username);

        void goToHouse(int houseId, String name, String username);

        void copyText(String text);

    }

}
