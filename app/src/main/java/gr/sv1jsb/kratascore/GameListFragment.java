package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import gr.sv1jsb.kratascore.adapters.GameListAdapter;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;

/**
 * A fragment representing a list of Items.
 * <p />
 * <p />
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class GameListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SortDialog.SortDialogListener {

    private GameListAdapter mAdapter;
    private boolean mClearSearch, mClearSort = true;
    private String mSearch = null;
    private String[] mSearchArgs = null;
    private String mSort = null;
    private Activity mParentActivity;
    private TextView mNoGames;
    private ProgressBar mProgress;

    public static GameListFragment newInstance() {
        GameListFragment fragment = new GameListFragment();
        return fragment;
    }

    public GameListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = getActivity();
        mAdapter = new GameListAdapter(getActivity().getBaseContext(), null, 0,
                R.layout.games_list_custom_row);
        setListAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.games_list, container, false);
        mNoGames = (TextView) view.findViewById(R.id.nogames);
        mProgress = (ProgressBar) view.findViewById(android.R.id.empty);
        Button newGameBtn = (Button) view.findViewById(R.id.game_listview_create);
        newGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(mParentActivity, NewGameActivity.class), Constants.NEW_GAME_REQUEST_CODE);
            }
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.game_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.search_list:
                searchListView();
                return true;
            case R.id.sort_list:
                sortListView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void searchListView(){
        startActivityForResult(new Intent(mParentActivity, SearchActivity.class), Constants.SEARCH_REQUEST_CODE);
    }

    private void sortListView(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(SortDialog.TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        DialogFragment sortFrag = SortDialog.newInstance(this);
        sortFrag.show(ft, SortDialog.TAG);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mParentActivity.getActionBar().setDisplayHomeAsUpEnabled(false);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor item = (Cursor) mAdapter.getItem(position);
        String gameName =  item.getString(KrataScoreContract.GameEntry.NUM_NAME);
        boolean gameEnded = null != item.getString(KrataScoreContract.GameEntry.NUM_WINNER);
        String gameMethod = item.getString(KrataScoreContract.GameEntry.NUM_METHOD);
        Fragment frag = GameFragment.newInstance(mAdapter.getItemId(position), gameName, gameEnded, gameMethod);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, frag, GameFragment.TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (mClearSearch) {
            mSearch = null;
            mSearchArgs = null;
        }
        if(mClearSort) {
            mSort = KrataScoreContract.GameEntry.SORT_ORDER_DEFAULT;
        }
        return new CursorLoader(mParentActivity.getBaseContext(), KrataScoreContract.GameEntry.CONTENT_URI,
                KrataScoreContract.GameEntry.PROJECTION_ALL, mSearch, mSearchArgs, mSort);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor.getCount() == 0) {
            mProgress.setVisibility(View.GONE);
            getListView().setEmptyView(mNoGames);
        }
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.SEARCH_REQUEST_CODE) {
            String name = data.getStringExtra(Constants.SEARCH_GAME_NAME);
            String winner = data.getStringExtra(Constants.SEARCH_WINNER_NAME);
            long dateMillis = data.getLongExtra(Constants.SEARCH_DATE, 0);
            searchGames(name, winner, dateMillis);
        } else if(resultCode == Activity.RESULT_OK && requestCode == Constants.NEW_GAME_REQUEST_CODE) {
            mClearSort = true;
            mClearSearch = true;
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    private void searchGames(String name, String winner, long dateMillis) {
        ArrayList<String> sTerms = new ArrayList<String>();
        ArrayList<String> sArgs = new ArrayList<String>();
        if(!name.equals("") || !winner.equals("") || dateMillis !=0){
            if(!name.equals("")) {
                sTerms.add(KrataScoreContract.GameEntry.SELECTION_NAME);
                sArgs.add(name+"%");
            }
            if(!winner.equals("")){
                sTerms.add(KrataScoreContract.GameEntry.SELECTION_WINNER);
                sArgs.add(winner+"%");
            }
            if(dateMillis != 0){
                sTerms.add(KrataScoreContract.GameEntry.SELECTION_DATE);
                sArgs.add(String.valueOf(dateMillis));
            }
            StringBuilder searchTerms = new StringBuilder();
            for(String s: sTerms){
                searchTerms.append(s).append(" AND ");
            }
            searchTerms.delete(searchTerms.length() - 5, searchTerms.length());
            mSearch = searchTerms.toString();
            mSearchArgs = sArgs.toArray(new String[sArgs.size()]);
            mClearSearch = false;

        } else {
            mClearSearch = true;
        }
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onSortDialogPositiveClick(int sortChoice) {
        switch(sortChoice){
            case 1:
                mSort = KrataScoreContract.GameEntry.SORT_ORDER_GAME;
                mClearSort = false;
                break;
            case 2:
                mSort = KrataScoreContract.GameEntry.SORT_ORDER_WINNER;
                mClearSort = false;
                break;
            case 3:
                mSort = KrataScoreContract.GameEntry.SORT_ORDER_DATE;
                mClearSort = false;
                break;
            default:
                mSort = KrataScoreContract.GameEntry.SORT_ORDER_DEFAULT;
                mClearSort = true;
        }
        getLoaderManager().restartLoader(0, null, this);
    }
}
