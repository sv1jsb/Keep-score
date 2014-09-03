package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import gr.sv1jsb.kratascore.adapters.GameAdapter;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;


public class GameFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
    AddScoreDialog.AddScoreDialogListener {

    public static final String TAG = Constants.GAMEFRAG;

    private static final String ARG_PARAM1 = "gameID";
    private static final String ARG_PARAM2 = "gameName";
    private static final String ARG_PARAM3 = "gameEnded";
    private static final String ARG_PARAM4 = "gameMethod";

    private long mGameID;
    private String mGameName;
    private boolean mGameEnded;
    private String mGameMethod;
    private Activity mParentActivity;

    private GameAdapter mAdapter;

    public static GameFragment newInstance(long gameID, String gameName, boolean gameEnded, String gameMethod) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, gameID);
        args.putString(ARG_PARAM2, gameName);
        args.putBoolean(ARG_PARAM3, gameEnded);
        args.putString(ARG_PARAM4, gameMethod);
        fragment.setArguments(args);
        return fragment;
    }
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = getActivity();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mGameID = getArguments().getLong(ARG_PARAM1);
            mGameName = getArguments().getString(ARG_PARAM2);
            mGameEnded = getArguments().getBoolean(ARG_PARAM3);
            mGameMethod = getArguments().getString(ARG_PARAM4);
        }
        mAdapter = new GameAdapter(mParentActivity.getBaseContext(), null, 0,
                R.layout.fragment_game_player_list);

        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        TextView tv = (TextView) view.findViewById(R.id.textGameTitle);
        tv.setText(mGameName);
        Button endBtn = (Button) view.findViewById(R.id.btnEnd);
        if(mGameEnded) {
            endBtn.setVisibility(View.GONE);
        }
        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(DeleteConfirmDialog.TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                DialogFragment endFrag = EndConfirmDialog.newInstance(getResources().getString(R.string.yesno_end_game), mGameID, mGameMethod);
                endFrag.show(ft, DeleteConfirmDialog.TAG);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_game_menu:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag(DeleteConfirmDialog.TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                DialogFragment deleteFrag = DeleteConfirmDialog.newInstance(getResources().getString(R.string.delete_game_confirmation), mGameID);
                deleteFrag.show(ft, DeleteConfirmDialog.TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(!mGameEnded) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag(AddScoreDialog.TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            DialogFragment addscoreFrag = AddScoreDialog.newInstance(this, id);
            addscoreFrag.show(ft, AddScoreDialog.TAG);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mParentActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showHistory(l);
                return true;
            }
        });
    }

    private void showHistory(long playerID){
        ContentResolver cr = mParentActivity.getContentResolver();
        Uri uri = Uri.withAppendedPath(KrataScoreContract.ScoreEntry.CONTENT_URI, "player/"+String.valueOf(playerID));
        Cursor cursor = cr.query(uri,
                KrataScoreContract.ScoreEntry.PROJECTION_PLAYER_SCORES,
                KrataScoreContract.ScoreEntry.SELECTION_GAME_ID,
                new String[] {String.valueOf(mGameID)},
                null);
        StringBuilder sb = new StringBuilder();
        if(cursor.getCount() >= 2) {
            cursor.moveToFirst();
            cursor.moveToNext();
            while (!cursor.isAfterLast()) {
                sb.append(cursor.getString(KrataScoreContract.ScoreEntry.NUM_SCORE));
                sb.append("\n");
                cursor.moveToNext();
            }
        } else {
            sb.append(mParentActivity.getString(R.string.no_score_yet));
        }
        cursor.close();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(GenericAlertDialog.TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        DialogFragment alertFrag = GenericAlertDialog.newInstance(sb.toString(), mParentActivity.getString(R.string.score_history), android.R.drawable.ic_dialog_info);
        alertFrag.show(ft, GenericAlertDialog.TAG);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sort;
        if(mGameMethod.equals(KrataScoreContract.GameEntry.METHOD_MAX)) {
            sort = KrataScoreContract.ScoreEntry.SORT_SCORE_DESC;
        } else {
            sort = KrataScoreContract.ScoreEntry.SORT_SCORE_ASC;
        }
        return new CursorLoader(
                                    mParentActivity.getBaseContext(),
                                    KrataScoreContract.ScoreEntry.CONTENT_URI,
                                    KrataScoreContract.ScoreEntry.PROJECTION_JOINED,
                                    KrataScoreContract.ScoreEntry.SELECTION_GAME_ID,
                                    new String[] {String.valueOf(mGameID)},
                                    sort
                                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onAddScoreDialogPositiveClick(int score, long playerID) {
        ContentResolver cr = mParentActivity.getContentResolver();
        ContentValues values = new ContentValues();
        values.put(KrataScoreContract.ScoreEntry.COL_SCORE, score);
        values.put(KrataScoreContract.ScoreEntry.COL_GAME_ID, mGameID);
        values.put(KrataScoreContract.ScoreEntry.COL_PLAYER_ID, playerID);
        cr.insert(KrataScoreContract.ScoreEntry.CONTENT_URI, values);

    }

}
