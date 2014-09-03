package gr.sv1jsb.kratascore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Set;

import gr.sv1jsb.kratascore.R;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;
import gr.sv1jsb.utils.ImageUtils;


/**
 * Created by andreas on 30/7/2014.
 */
public class PlayerListAdapter extends  BaseCursorAdapter {

    private boolean mHasCheck;
    private Bitmap mPlaceHolderBitmap;
    private Set<Long> mPlayerIds;

    public PlayerListAdapter(Context context, Cursor c, int flags, int layout, boolean hasCheck, Set<Long> playerIds) {
        super(context, c, flags, layout);
        mHasCheck = hasCheck;
        mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.user);
        mPlayerIds = playerIds;
    }

    @Override
    public View populateView(View view, Cursor cursor) {
        long playerId = cursor.getLong(0);
        String playerName = cursor.getString(KrataScoreContract.PlayerEntry.NUM_PLAYER);
        String playerPhoto = cursor.getString(KrataScoreContract.PlayerEntry.NUM_PHOTO);
        TextView tvName = ((TextView) view.findViewById(R.id.player_listview_name));
        tvName.setText(playerName);
        if(playerPhoto != null) {
            ImageView ivPhoto = (ImageView) view.findViewById(R.id.ivPlayerPhoto);
            ImageUtils.loadBitmap(mContext.getExternalFilesDir(null), playerPhoto, ivPhoto, mContext.getResources(), mPlaceHolderBitmap);
        }
        ImageView ivCheck = (ImageView) view.findViewById(R.id.playerCheck);
        if(!mHasCheck) {
            ivCheck.setVisibility(View.GONE);
        } else {
            if(mPlayerIds.contains(playerId)){
                ivCheck.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.checkbox_on_background));
                tvName.setTextColor(mContext.getResources().getColor(android.R.color.black));
            } else {
                ivCheck.setImageDrawable(mContext.getResources().getDrawable(android.R.drawable.checkbox_off_background));
                tvName.setTextColor(mContext.getResources().getColor(android.R.color.white));
            }
        }
        return view;
    }






}
