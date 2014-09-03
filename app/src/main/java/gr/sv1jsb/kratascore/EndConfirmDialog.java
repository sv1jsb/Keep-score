package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by andreas on 23/7/2014.
 */
public class EndConfirmDialog extends DialogFragment {

    public static final String MESSAGE = "message";
    public static final String GAME_ID = "gameID";
    public static final String GAME_METHOD = "gameMethod";
    public static final String TAG = Constants.ENDDIALOG;

    public interface EndListener {
        public void onEndPositiveClick(long gameID, String gameMethod);
    }

    EndListener mListener;
    static EndConfirmDialog newInstance(String message, long gameID, String gameMethod) {
        EndConfirmDialog f = new EndConfirmDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putLong(GAME_ID, gameID);
        args.putString(GAME_METHOD, gameMethod);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (EndListener) activity;
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement EndListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(MESSAGE);
        final long gameID = getArguments().getLong(GAME_ID);
        final String gameMethod = getArguments().getString(GAME_METHOD);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(R.string.confirm_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onEndPositiveClick(gameID, gameMethod);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        return builder.create();
    }
}
