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
public class DeleteConfirmDialog extends DialogFragment {

    public static final String MESSAGE = "message";
    public static final String LINE_ID = "lineID";
    public static final String TAG = Constants.DELETEDIALOG;

    public interface DeleteListener {
        public void onDeletePositiveClick(long lineID);
    }

    private long lineID;
    DeleteListener mListener;
    static DeleteConfirmDialog newInstance(String message, long lineID) {
        DeleteConfirmDialog f = new DeleteConfirmDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putLong(LINE_ID, lineID);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DeleteListener) activity;
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteListener");
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
        lineID = getArguments().getLong(LINE_ID);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(R.string.confirm_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDeletePositiveClick(lineID);
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
