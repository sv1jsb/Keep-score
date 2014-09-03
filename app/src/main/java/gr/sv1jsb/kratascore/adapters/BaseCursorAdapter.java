package gr.sv1jsb.kratascore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;

/**
 * Created by andreas on 30/7/2014.
 */
public class BaseCursorAdapter extends CursorAdapter{

    protected Context mContext;
    private int mLayout;

    public BaseCursorAdapter(Context context, Cursor c, int flags,
                             int layout) {
        super(context, c, flags);
        mContext = context;
        mLayout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout gameRow = null;
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gameRow = (LinearLayout) li.inflate(mLayout, parent, false);
        } else {
            gameRow = (LinearLayout) convertView;
        }
        Cursor cur = (Cursor) getItem(position);

        return populateView(gameRow, cur);
    }

    public View populateView(View view, Cursor cursor){
        return view;
    }
}
