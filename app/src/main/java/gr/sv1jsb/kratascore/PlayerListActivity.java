package gr.sv1jsb.kratascore;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import gr.sv1jsb.kratascore.adapters.PlayerListAdapter;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;
import gr.sv1jsb.kratascore.persistance.Player;


public class PlayerListActivity extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SELECTED_PLAYERS = "SELECTED_PLAYERS";
    public static final String LIST_FILTER = "LIST_FILTER";
    private PlayerListAdapter mAdapter;
    private Map<Long, Player> mPlayers = new HashMap<Long, Player>();
    private String[] mSelectionArgs= {"%"};
    private TextView mNoPlayers;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playerlist);
        if(savedInstanceState != null) {
            mSelectionArgs = savedInstanceState.getStringArray(LIST_FILTER);
            if(savedInstanceState.getParcelableArrayList(SELECTED_PLAYERS) != null){
                for(Parcelable p: savedInstanceState.getParcelableArrayList(SELECTED_PLAYERS)){
                    Player pl = (Player) p;
                    mPlayers.put(pl._id, pl);
                }
            }
        }
        mAdapter = new PlayerListAdapter(this, null, 0,
                R.layout.playerlist_row, true, mPlayers.keySet());
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

        mNoPlayers = (TextView) findViewById(R.id.noplayers);
        mProgress = (ProgressBar) findViewById(android.R.id.empty);
        Button okBtn = (Button) findViewById(R.id.player_listview_create);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                final ArrayList<Player> players = new ArrayList<Player>(mPlayers.values());
                final int res;
                if(players.size()>0){
                    resultIntent.putExtra(Constants.PLAYERS_CHOOSEN, players);
                    res = RESULT_OK;
                } else {
                    res = RESULT_CANCELED;
                }
                setResult(res, resultIntent);
                finish();
            }
        });

        EditText filterPlayers = (EditText) findViewById(R.id.filterPlayers);
        filterPlayers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if(charSequence.length()>0) {
                    mSelectionArgs[0] = "%" + charSequence + "%";
                } else {
                    mSelectionArgs[0] = "%";
                }
                getLoaderManager().restartLoader(0, null, PlayerListActivity.this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Cursor c = (Cursor) mAdapter.getItem(i);
                final Player p = Player.newInstanceFromCursor(c);
                Intent playerIntent = new Intent(PlayerListActivity.this, PlayerActivity.class);
                playerIntent.putExtra(Constants.PLAYER, p);
                startActivityForResult(playerIntent, Constants.PLAYER_REQUEST_CODE);
                return true;
            }
        });
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setSubtitle(getString(R.string.choose_players));
        } catch (NullPointerException e){

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SELECTED_PLAYERS, new ArrayList<Player>(mPlayers.values()));
        outState.putStringArray(LIST_FILTER, mSelectionArgs);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Constants.PLAYER_REQUEST_CODE) {
            if(data.getIntExtra(Constants.PLAYER_DELETED, 0) == Constants.PLAYER_DELETED_OK) {
                Toast.makeText(this, getString(R.string.player_deleted_ok), Toast.LENGTH_SHORT).show();
            } else if(data.getIntExtra(Constants.PLAYER_DELETED, 0) == Constants.PLAYER_DELETED_ERROR) {
                Toast.makeText(this, getString(R.string.player_deleted_error), Toast.LENGTH_SHORT).show();
            }
            getLoaderManager().restartLoader(0, null, PlayerListActivity.this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.player_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_player_menu:
                Intent playerIntent = new Intent(PlayerListActivity.this, PlayerActivity.class);
                startActivityForResult(playerIntent, Constants.PLAYER_REQUEST_CODE);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor item = (Cursor) mAdapter.getItem(position);
        if(!mPlayers.containsKey(id)) {
            mPlayers.put(id, Player.newInstanceFromCursor(item));
            ((TextView) v.findViewById(R.id.player_listview_name)).setTextColor(getResources().getColor(android.R.color.black));
            ((ImageView) v.findViewById(R.id.playerCheck)).setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_on_background));
        } else {
            mPlayers.remove(id);
            ((TextView) v.findViewById(R.id.player_listview_name)).setTextColor(getResources().getColor(android.R.color.white));
            ((ImageView) v.findViewById(R.id.playerCheck)).setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_off_background));
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, KrataScoreContract.PlayerEntry.CONTENT_URI,
                KrataScoreContract.PlayerEntry.PROJECTION_ALL,
                KrataScoreContract.PlayerEntry.SELECTION_NAME,
                mSelectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor.getCount() == 0) {
            mProgress.setVisibility(View.GONE);
            getListView().setEmptyView(mNoPlayers);
        }
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }
}
