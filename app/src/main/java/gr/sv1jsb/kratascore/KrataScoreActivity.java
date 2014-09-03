package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;

import gr.sv1jsb.kratascore.persistance.KrataScoreContract;
import gr.sv1jsb.utils.ImageUtils;


public class KrataScoreActivity extends Activity implements
        DeleteConfirmDialog.DeleteListener,
        EndConfirmDialog.EndListener {

    private GameListFragment mGameListFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kratascore_main);
        if (savedInstanceState == null) {
            ImageUtils.addImageCache(getFragmentManager(), 0.1f);
            mGameListFrag = GameListFragment.newInstance();
            getFragmentManager().beginTransaction()
                    .add(R.id.container, mGameListFrag)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_help:
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new HelpFragment(), HelpFragment.TAG)
                        .addToBackStack(HelpFragment.TAG)
                        .commit();
                break;
            case R.id.action_about:
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new AboutFragment(), AboutFragment.TAG)
                        .addToBackStack(AboutFragment.TAG)
                        .commit();
                break;
            case android.R.id.home:
                getFragmentManager().popBackStack();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDeletePositiveClick(long lineID) {
        ContentResolver cr = getContentResolver();
        Uri uri = Uri.withAppendedPath(KrataScoreContract.GameEntry.CONTENT_URI, String.valueOf(lineID));
        cr.delete(uri,null,null);
        Toast.makeText(this, R.string.game_deleted, Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }

    @Override
    public void onEndPositiveClick(long gameID, String gameMethod) {
        ContentResolver cr = getContentResolver();
        Uri uri = KrataScoreContract.ScoreEntry.CONTENT_URI;
        String minOrMax;
        int count = 0;
        if(gameMethod.equals(KrataScoreContract.GameEntry.METHOD_MAX)){
            minOrMax = KrataScoreContract.ScoreEntry.SORT_SCORE_DESC;
        } else {
            minOrMax = KrataScoreContract.ScoreEntry.SORT_SCORE_ASC;
        }
        Cursor cursor = cr.query(uri, KrataScoreContract.ScoreEntry.PROJECTION_JOINED,
                KrataScoreContract.ScoreEntry.SELECTION_GAME_ID,
                new String[] {String.valueOf(gameID)},
                minOrMax);
        if(cursor.getCount() >= 1) {
            cursor.moveToFirst();
            String winner = cursor.getString(KrataScoreContract.ScoreEntry.NUM_PLAYER);
            cursor.close();
            ContentValues values = new ContentValues();
            Calendar cal = Calendar.getInstance();
            long dt = cal.getTimeInMillis();
            values.put(KrataScoreContract.GameEntry.COL_ENDED, String.valueOf(dt));
            values.put(KrataScoreContract.GameEntry.COL_WINNER, winner);
            uri = Uri.withAppendedPath(KrataScoreContract.GameEntry.CONTENT_URI, String.valueOf(gameID));
            count = cr.update(uri, values, null, null);
        }
        getFragmentManager().popBackStack();
        if(count>0) {
            Toast.makeText(getBaseContext(), getString(R.string.game_ended), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.game_not_updated), Toast.LENGTH_SHORT).show();
        }
    }
}
