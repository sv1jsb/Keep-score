package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by andreas on 23/7/2014.
 */
public class SortDialog extends DialogFragment {

    public static final String TAG = Constants.SORTDIALOG;
    public static final String SELECTION = "SELECTION";

    public interface SortDialogListener {
        public void onSortDialogPositiveClick(int sortChoice);
    }
    private Fragment mParentFrag;
    private SortDialogListener mListener;
    private int mSelection = 0;
    static SortDialog newInstance(Fragment parentFrag) {
        SortDialog f = new SortDialog();
        f.mParentFrag = parentFrag;
        return f;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SortDialogListener) mParentFrag;
        } catch (ClassCastException e) {
            // The fragment doesn't implement the interface, throw exception
            throw new ClassCastException(mParentFrag.toString()
                    + " must implement SortDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            mParentFrag = getFragmentManager().getFragment(savedInstanceState, Constants.PARENT_FRAG);
            mListener = (SortDialogListener) mParentFrag;
            mSelection = savedInstanceState.getInt(SELECTION);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        getFragmentManager().putFragment(outState, Constants.PARENT_FRAG, mParentFrag);
        outState.putInt(SELECTION, mSelection);
        super.onSaveInstanceState(outState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), AlertDialog.THEME_HOLO_DARK);
        builder.setTitle(R.string.sort_menu_entry)
                .setIcon(android.R.drawable.ic_menu_sort_by_size)
                .setSingleChoiceItems(R.array.sort_array, mSelection,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSelection = which;
                            }
                        })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSortDialogPositiveClick(mSelection);
                    }
                });
        return builder.create();
    }
}
