package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SearchActivity extends Activity implements
        DateFragment.OnDateFragmentInteractionListener {

    public static final String DATE = "DATE";
    public static final String MILLIS = "MILLIS";
    public static final String GAME = "GAME";
    public static final String WINNER = "WINNER";
    private TextView mDateText;
    private EditText mSearchGame;
    private EditText mSearchWinner;
    private long mDateMillis;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        mDateText = (TextView) findViewById(R.id.searchDateText);
        mSearchGame = (EditText) findViewById(R.id.searchName);
        mSearchWinner = (EditText) findViewById(R.id.searchWinner);
        if(savedInstanceState != null){
            mDateText.setText(savedInstanceState.getString(DATE));
            mDateMillis = savedInstanceState.getLong(MILLIS);
            mSearchGame.setText(savedInstanceState.getString(GAME));
            mSearchWinner.setText(savedInstanceState.getString(WINNER));
        }
        Button dateBtn = (Button) findViewById(R.id.searchDate);
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = DateFragment.newInstance();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
        Button searchBtn = (Button) findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.SEARCH_GAME_NAME, mSearchGame.getText().toString());
                resultIntent.putExtra(Constants.SEARCH_WINNER_NAME, mSearchWinner.getText().toString());
                resultIntent.putExtra(Constants.SEARCH_DATE, mDateMillis);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setSubtitle(getString(R.string.search_menu_entry));
        } catch (NullPointerException e) {

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(DATE, mDateText.getText().toString());
        outState.putString(GAME, mSearchGame.getText().toString());
        outState.putString(WINNER, mSearchWinner.getText().toString());
        outState.putLong(MILLIS, mDateMillis);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDateFragmentInteraction(String date, long dateMillis) {
        mDateText.setText(date);
        mDateMillis = dateMillis;
    }

}
