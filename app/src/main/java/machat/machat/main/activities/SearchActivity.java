package machat.machat.main.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import machat.machat.main.adapters.SearchArrayAdapter;
import machat.machat.models.MyProfile;
import machat.machat.R;
import machat.machat.models.SearchItem;
import machat.machat.util.SocketService;
import machat.machat.parsing.interfaces.OnSearchResults;
import machat.machat.conf.SocketCommand;
import machat.machat.parsing.SocketParse;

public class SearchActivity extends ListActivity implements SocketActivity.SocketListener, TextWatcher, OnSearchResults, View.OnClickListener {

    private static final int MAX_DELAY = 500;
    private static final int MIN_DELAY = 250;
    private long lastSearch = 0;
    private SocketActivity socketActivity = new SocketActivity(this);
    private boolean connected = false;
    private SocketService mService;
    private SearchArrayAdapter arrayAdapter;
    private EditText searchEditText;
    private ImageButton clearButton;
    private Timer searchTimer = new Timer();

    private MyProfile myProfile;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_search);
        socketActivity.setOnSocketListener(this);
        arrayAdapter = new SearchArrayAdapter(this, new ArrayList());
        setListAdapter(arrayAdapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        socketActivity.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        socketActivity.disconnect();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflate with your particular xml
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchEditText = (EditText) menuItem.getActionView().findViewById(R.id.search);
        searchEditText.addTextChangedListener(this);
        clearButton = (ImageButton) menuItem.getActionView().findViewById(R.id.clear);
        clearButton.setOnClickListener(this);
        Drawable clearDrawable = DrawableCompat.wrap(clearButton.getDrawable());
        DrawableCompat.setTint(clearDrawable, R.color.grey600);
        clearButton.setVisibility(View.INVISIBLE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConnect(SocketService mService) {
        connected = true;
        this.mService = mService;
        myProfile = mService.user.getMyProfile();
    }

    @Override
    public void onDisconnect() {
        connected = false;
    }

    @Override
    public void onReceive(String command, String data) {
        if (command.equals(SocketCommand.SEARCH)) {
            SocketParse.parseSearchResults(data, this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(final CharSequence s, int start, int before, int count) {
        if((System.currentTimeMillis() - lastSearch) > MAX_DELAY) {
            lastSearch = System.currentTimeMillis();
            searchTimer.cancel();
            searchTimer.purge();
            searchTimer = new Timer();
            searchTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String searchText = s.toString();
                    if (connected && mService.isConnected() && !searchText.isEmpty()) {
                        mService.send.search(searchText);
                    }
                }
            }, MIN_DELAY);
        }
        String searchText = s.toString();
        if (searchText.isEmpty()) {
            clearButton.setVisibility(View.INVISIBLE);
            arrayAdapter.clear();
        } else {
            clearButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void newSearchResults(ArrayList<SearchItem> users) {
        arrayAdapter.clear();
        arrayAdapter.addAll(users);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long i) {
        SearchItem user = arrayAdapter.getItem(position);
        if (user.isBlock()) {
            Toast.makeText(this, "You are blocked from this house", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, HouseActivity.class);
            int houseId = user.getId();
            intent.putExtra(HouseActivity.HOUSE_ID, houseId);
            intent.putExtra(HouseActivity.MY_ID, myProfile.getId());
            intent.putExtra(HouseActivity.HOUSE_NAME, user.getName());
            intent.putExtra(HouseActivity.HOUSE_USERNAME, user.getUsername());
            intent.putExtra(HouseActivity.FAVORITE, mService.favorites.getFavorite(houseId));
            intent.putExtra(HouseActivity.MUTE, mService.favorites.getMute(houseId));
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        searchEditText.setText("");
    }
}
