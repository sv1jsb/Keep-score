package gr.sv1jsb.kratascore;


import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AboutFragment extends Fragment {

    public static final String TAG = Constants.ABOUTFRAG;

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            ActionBar ab = getActivity().getActionBar();
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setSubtitle(R.string.action_about);
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ActionBar ab = getActivity().getActionBar();
            ab.setSubtitle(null);
        } catch (NullPointerException e) {

        }
    }
}
