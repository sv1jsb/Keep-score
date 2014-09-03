package gr.sv1jsb.kratascore.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import gr.sv1jsb.kratascore.R;
import gr.sv1jsb.kratascore.persistance.KrataScoreContract;
import gr.sv1jsb.utils.ImageUtils;

/**
 * Created by andreas on 30/7/2014.
 */
public class GameAdapter extends  BaseCursorAdapter {

    private Bitmap mPlaceHolderBitmap;

    public GameAdapter(Context context, Cursor c, int flags, int layout) {
        super(context, c, flags, layout);
        mPlaceHolderBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.user);

    }

    @Override
    public View populateView(View view, Cursor cursor) {
        String playerName = cursor.getString(KrataScoreContract.ScoreEntry.NUM_PLAYER);
        String playerScore = cursor.getString(KrataScoreContract.ScoreEntry.NUM_SCORE);
        String playerPhoto = cursor.getString(KrataScoreContract.ScoreEntry.NUM_PLAYER_PHOTO);
        ((TextView) view.findViewById(R.id.playerName)).setText(playerName);
        TextView scoreView = ((TextView) view.findViewById(R.id.playerScore));
        scoreView.setText(playerScore);
        if(playerPhoto != null) {
            ImageView iv = (ImageView) view.findViewById(R.id.ivPlayerPhoto);
            ImageUtils.loadBitmap(mContext.getExternalFilesDir(null), playerPhoto, iv, mContext.getResources(), mPlaceHolderBitmap);
        }
        return view;
    }
}
