package machat.machat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Admin on 6/21/2015.
 */

public class FavoriteItemDialogFragment extends DialogFragment {

    public interface OnCompleteListener {

        void muteHouse(int id, boolean mute);

        void unFavoriteHouse(int id);

        void openProfile(int id);
    }

    public static final String FAVORITE_ITEM = "favoriteItem";

    private OnCompleteListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.listener = (OnCompleteListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final FavoriteItem favoriteItem = (FavoriteItem) getArguments().getSerializable(FAVORITE_ITEM);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String list[] = new String[3];
        if(favoriteItem.isMute()){
            list[0] = "UnMute";
        }else{
            list[0] = "Mute";
        }
        list[1] = "Profile";
        list[2] = "UnFavorite";
        builder.setTitle(favoriteItem.getUser().getName())
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            listener.muteHouse(favoriteItem.getUser().getId(), !(favoriteItem.isMute()));
                        }else if(which == 1){
                            listener.openProfile(favoriteItem.getUser().getId());
                        }else{
                            listener.unFavoriteHouse(favoriteItem.getUser().getId());
                        }
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dismiss();
            }
        });
        return builder.create();
    }

}
