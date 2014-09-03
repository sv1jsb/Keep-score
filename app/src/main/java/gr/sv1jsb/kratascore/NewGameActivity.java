package gr.sv1jsb.kratascore;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gr.sv1jsb.kratascore.adapters.PlayerListAdapter;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;
import gr.sv1jsb.kratascore.persistance.Player;


public class NewGameActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemSelectedListener {

    public static final String GAME_NAME = "GAME_NAME";
    public static final String GAME_METHOD = "GAME_METHOD";
    public static final String GAME_PLAYERS = "GAME_PLAYERS";
    private String mMethod;
    private ArrayList<Player> mPlayers = new ArrayList<Player>();
    private List<String> mSelectedIds = new ArrayList<String>();
    private PlayerListAdapter mAdapter;
    private String mGameName;
    private TextView mAddPlayers;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_game);
        if(savedInstanceState != null){
            mGameName = savedInstanceState.getString(GAME_NAME);
            mMethod = savedInstanceState.getString(GAME_METHOD);
            mPlayers = savedInstanceState.getParcelableArrayList(GAME_PLAYERS);
        }
        mAdapter = new PlayerListAdapter(this, null, 0, R.layout.playerlist_row, false, null);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        mAddPlayers = (TextView) findViewById(R.id.addplayers);
        mProgressBar = (ProgressBar) findViewById(android.R.id.empty);
        Spinner spinner = (Spinner) findViewById(R.id.spinnerMethod);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.method_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        Button createBtn = (Button) findViewById(R.id.btnCreate);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameName = ((EditText) findViewById(R.id.gameNameText)).getText().toString();
                if(!"".equals(mGameName) && mPlayers != null && mPlayers.size()>1) {
                    NewGameEntered(mGameName, mMethod, mPlayers);
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else {
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    Fragment prev = getFragmentManager().findFragmentByTag(GenericAlertDialog.TAG);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    String message;
                    message = getResources().getString(R.string.new_game_error);
                    DialogFragment errorFrag = GenericAlertDialog.newInstance(message, null, 0);
                    errorFrag.show(ft, GenericAlertDialog.TAG);
                }
            }
        });

        ImageButton selectPlayerBtn = (ImageButton) findViewById(R.id.btnSelectPlayer);
        selectPlayerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(NewGameActivity.this, PlayerListActivity.class), Constants.PLAYER_LIST_REQUEST_CODE);
            }
        });
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setSubtitle(getString(R.string.new_game_title));
        } catch (NullPointerException e){

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(GAME_NAME, mGameName);
        outState.putString(GAME_METHOD, mMethod);
        outState.putParcelableArrayList(GAME_PLAYERS, mPlayers);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == Constants.PLAYER_LIST_REQUEST_CODE) {
            mPlayers = data.getParcelableArrayListExtra(Constants.PLAYERS_CHOOSEN);
            mSelectedIds.clear();
            for(Player p: mPlayers) {
                mSelectedIds.add(String.valueOf(p._id));
            }
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    @Override
    public void onPause() {
        final EditText et = (EditText) findViewById(R.id.gameNameText);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(et.getWindowToken(), 0);
        super.onPause();
    }

     public void NewGameEntered(String gameName, String gameMethod, ArrayList<Player> players) {
        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(KrataScoreContract.GameEntry.COL_NAME, gameName);
        values.put(KrataScoreContract.GameEntry.COL_METHOD, gameMethod);
        Calendar cal = Calendar.getInstance();
        long dt = cal.getTimeInMillis();
        values.put(KrataScoreContract.GameEntry.COL_STARTED, String.valueOf(dt));
        Uri uri = cr.insert(KrataScoreContract.GameEntry.CONTENT_URI, values);
        int gameID = Integer.parseInt(uri.getLastPathSegment());
        for(Player p: players){
            values = new ContentValues();
            values.put(KrataScoreContract.ScoreEntry.COL_GAME_ID, gameID);
            values.put(KrataScoreContract.ScoreEntry.COL_PLAYER_ID, p._id);
            values.put(KrataScoreContract.ScoreEntry.COL_SCORE, 0);
            cr.insert(KrataScoreContract.ScoreEntry.CONTENT_URI, values);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if(pos == 0){
            mMethod = KrataScoreContract.GameEntry.METHOD_MAX;
        } else {
            mMethod = KrataScoreContract.GameEntry.METHOD_MIN;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String selection;
        String[] selectionArgs;
        if(mSelectedIds.size() == 0){
            selection = "_id = 0";
            selectionArgs = null;
        } else {
            selection = "_id in (" + makePlaceholders(mSelectedIds.size()) + ")";
            selectionArgs = mSelectedIds.toArray(new String[mSelectedIds.size()]);
        }
        return new CursorLoader(this, KrataScoreContract.PlayerEntry.CONTENT_URI,
                KrataScoreContract.PlayerEntry.PROJECTION_ALL,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor.getCount() == 0) {
            mProgressBar.setVisibility(View.GONE);
            getListView().setEmptyView(mAddPlayers);
        }
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    public void onPlayerListFragmentInteraction(ArrayList<Player> playersList) {
        getFragmentManager().popBackStack();
        mPlayers = playersList;
        mSelectedIds.clear();
        for(Player p: mPlayers) {
            mSelectedIds.add(String.valueOf(p._id));
        }
        getLoaderManager().restartLoader(0, null, this);
    }
}
