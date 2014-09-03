package gr.sv1jsb.kratascore.persistance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by andreas on 22/7/2014.
 */
public class KrataScoreDB extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "KrataScore.db";

    public KrataScoreDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(KrataScoreContract.GameEntry.CREATE_TABLE);
        db.execSQL(KrataScoreContract.PlayerEntry.CREATE_TABLE);
        db.execSQL(KrataScoreContract.ScoreEntry.CREATE_TABLE);
        db.execSQL(KrataScoreContract.ScoreEntry.CREATE_INDEX_GAME);
        db.execSQL(KrataScoreContract.ScoreEntry.CREATE_INDEX_PLAYER);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
