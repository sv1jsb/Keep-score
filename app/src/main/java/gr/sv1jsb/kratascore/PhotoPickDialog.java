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
public class PhotoPickDialog extends DialogFragment {

    public interface PhotoPickDialogListener {
        public void onPhotoPickChoice(int which);
    }
    PhotoPickDialogListener mListener;
    public static final String TAG = Constants.PHOTODIALOG;

    static PhotoPickDialog newInstance() {
        PhotoPickDialog f = new PhotoPickDialog();
        return f;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PhotoPickDialogListener) activity;
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PhotoPickDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        builder.setItems(R.array.photo_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(mListener != null){
                    mListener.onPhotoPickChoice(which);
                }
            }
        });
        return builder.create();
    }
}
