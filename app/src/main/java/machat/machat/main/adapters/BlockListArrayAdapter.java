package machat.machat.main.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import machat.machat.main.activities.BlockListActivity;
import machat.machat.models.BlockUser;
import machat.machat.R;
import machat.machat.util.AvatarManager;
import machat.machat.parsing.interfaces.OnCallbackAvatar;

public class BlockListArrayAdapter extends ArrayAdapter<BlockUser> {

    private ArrayList<BlockUser> blockList;

    private BlockListActivity activity;

    public BlockListArrayAdapter(BlockListActivity activity, ArrayList<BlockUser> blockList) {
        super(activity, -1, blockList);
        this.blockList = blockList;
        this.activity = activity;
    }

    @Override
    public BlockUser getItem(int position) {
        return blockList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_search, parent, false);

        BlockUser user = blockList.get(position);

        TextView usernameView = (TextView) rowView.findViewById(R.id.username);
        TextView nameView = (TextView) rowView.findViewById(R.id.name);
        final ImageView avatarView = (ImageView) rowView.findViewById(R.id.avatar);

        usernameView.setText(user.getUsername());
        nameView.setText(user.getName());

        AvatarManager.getAvatar(user.getId(), new OnCallbackAvatar() {
            @Override
            public void newAvatar(int id, final byte[] avatar, long time) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avatarView.setImageBitmap(BitmapFactory.decodeByteArray(avatar, 0, avatar.length));
                    }
                });
            }
        });

        return rowView;
    }

}
