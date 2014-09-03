package gr.sv1jsb.kratascore;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

import java.text.DateFormat;
import java.util.Calendar;


public class DateFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener {



    private OnDateFragmentInteractionListener mListener;

    public static DateFragment newInstance() {
        DateFragment fragment = new DateFragment();

        return fragment;
    }
    public DateFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDateFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnDateFragmentInteractioListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i2, int i3) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(i, i2, i3,0,0);
        DateFormat sdf = DateFormat.getDateInstance(DateFormat.SHORT);
        mListener.onDateFragmentInteraction(sdf.format(calendar.getTime()), calendar.getTimeInMillis());
    }

    public interface OnDateFragmentInteractionListener {
        public void onDateFragmentInteraction(String date, long dateMillies);
    }

}
