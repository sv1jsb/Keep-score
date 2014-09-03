package gr.sv1jsb.kratascore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import gr.sv1jsb.kratascore.R;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;

/**
 * Created by andreas on 30/7/2014.
 */
public class GameListAdapter extends  BaseCursorAdapter {

    private SimpleDateFormat mDateFormat;

    public GameListAdapter(Context context, Cursor c, int flags, int layout) {
        super(context, c, flags, layout);
        mDateFormat = new SimpleDateFormat();
    }

    @Override
    public View populateView(View view, Cursor cursor) {
        String gameName = cursor.getString(KrataScoreContract.GameEntry.NUM_NAME);
        String gameStarted = mDateFormat.format(new Date(Long.parseLong(cursor.getString(KrataScoreContract.GameEntry.NUM_STARTED))));
        String endmillis = cursor.getString(KrataScoreContract.GameEntry.NUM_ENDED);
        String gameEnded;
        try {
            gameEnded = mDateFormat.format(new Date(Long.parseLong(endmillis)));
        } catch(NumberFormatException e){
            gameEnded = "";
        }
        String gameWinner = cursor.getString(KrataScoreContract.GameEntry.NUM_WINNER);
        ((TextView) view.findViewById(R.id.game_listview_custom_row_NAME_textView)).setText(gameName);
        ((TextView) view.findViewById(R.id.game_listview_custom_row_DATE_textView)).setText(gameStarted);
        ((TextView) view.findViewById(R.id.game_listview_custom_row_DATE_END_textView)).setText(gameEnded);
        ((TextView) view.findViewById(R.id.game_listview_custom_row_WINNER_textView)).setText(gameWinner);
        return view;
    }
}
