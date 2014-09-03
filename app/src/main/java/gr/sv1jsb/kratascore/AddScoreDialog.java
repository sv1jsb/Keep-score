package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by andreas on 23/7/2014.
 */
public class AddScoreDialog extends DialogFragment {

    public static final String PLAYER_ID = "player_id";
    public static final String TAG = Constants.ADDSCOREDIALOG;

    public interface AddScoreDialogListener {
        public void onAddScoreDialogPositiveClick(int score, long playerID);
    }

    private Fragment mParentFrag;
    private AddScoreDialogListener mListener;
    private View dialogView;
    private Activity mParentActivity;
    private InputMethodManager inputMethodManager;
    private EditText et;

    static AddScoreDialog newInstance(Fragment parentFrag, long playerID) {
        AddScoreDialog f = new AddScoreDialog();
        Bundle args = new Bundle();
        args.putLong(PLAYER_ID, playerID);
        f.setArguments(args);
        f.mParentFrag = parentFrag;
        return f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddScoreDialogListener) mParentFrag;
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface, throw exception
            throw new ClassCastException(mParentFrag.toString()
                    + " must implement AddScoreDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParentActivity = getActivity();
        inputMethodManager = (InputMethodManager) mParentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(savedInstanceState != null){
            mParentFrag = getFragmentManager().getFragment(savedInstanceState, Constants.PARENT_FRAG);
            mListener = (AddScoreDialogListener) mParentFrag;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getFragmentManager().putFragment(outState, Constants.PARENT_FRAG, mParentFrag);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        et = (EditText) dialogView.findViewById(R.id.edit_add_score);
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    et.post(new Runnable() {
                        @Override
                        public void run() {
                            inputMethodManager.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onPause() {
        inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final long playerID = getArguments().getLong(PLAYER_ID);
        AlertDialog.Builder builder = new AlertDialog.Builder(mParentActivity, AlertDialog.THEME_HOLO_DARK);
        LayoutInflater inflater = mParentActivity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.add_score_dialog, null);
        builder.setTitle(R.string.add_score_dialog_title)
                .setIcon(android.R.drawable.ic_input_add)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editScore = (EditText) dialogView.findViewById(R.id.edit_add_score);
                        String score_str = editScore.getText().toString();
                        int score;
                        try {
                            score = Integer.parseInt(score_str);
                            mListener.onAddScoreDialogPositiveClick(score, playerID);
                        } catch(NumberFormatException e) {
                            score = 0;
                        }

                    }
                });
        return builder.create();
    }
}
