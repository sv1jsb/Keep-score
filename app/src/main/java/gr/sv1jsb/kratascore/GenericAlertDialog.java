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
public class GenericAlertDialog extends DialogFragment {


    public static final String MESSAGE = "message";
    public static final String TAG = Constants.ERRORDIALOG;
    public static final String TITLE = "title";
    public static final String ICON = "icon";

    static GenericAlertDialog newInstance(String message, String title, int icon) {
        GenericAlertDialog f = new GenericAlertDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        args.putString(TITLE, title);
        args.putInt(ICON, icon);
        f.setArguments(args);
        return f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString(MESSAGE);
        String title = getArguments().getString(TITLE, getString(R.string.error_title));
        int icon;
        if(getArguments().getInt(ICON) == 0) {
            icon = android.R.drawable.ic_dialog_alert;
        } else {
            icon = getArguments().getInt(ICON);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(title)
                .setIcon(icon)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //
                    }
                });
        return builder.create();
    }
}
