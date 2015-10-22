package machat.machat.main.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import machat.machat.R;

/**
 * Created by Maxwell on 10/21/2015.
 */
public class AvatarActivity extends Activity {

    public static String AVATAR = "Avatar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(AVATAR);

        byte[] byteArray = getIntent().getByteArrayExtra(AVATAR);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        ImageView avatarView = (ImageView) findViewById(R.id.avatarView);
        avatarView.setImageBitmap(bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
