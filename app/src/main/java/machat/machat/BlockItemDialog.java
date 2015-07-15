package machat.machat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Admin on 7/8/2015.
 */
public class BlockItemDialog extends DialogFragment {

    public static final String NAME = "name";

    public static final String ID = "id";

    public interface BlockChange{

        void unBlock(int id);

    }

    private BlockChange blockChange;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            this.blockChange = (BlockChange) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String name = getArguments().getString(NAME);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(name)
                .setPositiveButton(R.string.unblock, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int userId = getArguments().getInt(ID);
                        blockChange.unBlock(userId);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
