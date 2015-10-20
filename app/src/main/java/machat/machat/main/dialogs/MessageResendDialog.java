package machat.machat.main.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import machat.machat.R;

/**
 * Created by Maxwell on 10/18/2015.
 */
public class MessageResendDialog extends DialogFragment {

    public static final String TITLE = "Re-send Message";

    public static final String MESSAGE_ID = "messageId";

    public static final String LOCAL_ID = "localId";

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
        final int localId = getArguments().getInt(LOCAL_ID);
        final int messageId = getArguments().getInt(MESSAGE_ID);
        builder.setTitle(TITLE);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.message_resend, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                action.resendMessage(localId, messageId);
            }
        });
        return builder.create();
    }

    public interface Action {

        void resendMessage(int localId, int messageId);

    }

}
