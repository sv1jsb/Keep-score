package gr.sv1jsb.kratascore.persistance;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by andreas on 23/7/2014.
 */
public class KrataScoreContract {

    public static final String AUTHORITY =
            "gr.sv1jsb.kratascore.provider";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    public static final class GameEntry implements BaseColumns {
        public static final String TABLE_NAME = "game";
        public static final String COL_NAME = "name";
        public static final String COL_STARTED = "started";
        public static final String COL_ENDED = "ended";
        public static final String COL_WINNER = "winner";
        public static final String COL_METHOD = "method";
        public static final int NUM_NAME = 1;
        public static final int NUM_STARTED = 2;
        public static final int NUM_ENDED = 3;
        public static final int NUM_WINNER = 4;
        public static final int NUM_METHOD = 5;
        public static final String METHOD_MAX = "max";
        public static final String METHOD_MIN = "min";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COL_NAME + " TEXT," +
                        COL_STARTED + " TEXT," +
                        COL_ENDED + " TEXT," +
                        COL_WINNER + " TEXT," +
                        COL_METHOD + " TEXT);";

        public static final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        KrataScoreContract.CONTENT_URI,
                        "games");

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.gr.sv1jsb.kratascore.provider.games";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.gr.sv1jsb.kratascore.provider.games";

        public static final String[] PROJECTION_ALL =
                { _ID,
                  COL_NAME,
                  COL_STARTED,
                  COL_ENDED,
                  COL_WINNER,
                  COL_METHOD
                };

        public static final String SORT_ORDER_DEFAULT =
                _ID + " DESC";

        public static final String SORT_ORDER_GAME =
                COL_NAME;

        public static final String SORT_ORDER_WINNER =
                COL_WINNER ;

        public static final String SORT_ORDER_DATE =
                COL_STARTED;

        public static final String SELECTION_NAME =
                COL_NAME + " LIKE ? ";

        public static final String SELECTION_WINNER =
                COL_WINNER + " LIKE ? ";

        public static final String SELECTION_DATE =
                COL_STARTED + " >= ? ";
    }

    public static final class PlayerEntry implements BaseColumns {
        public static final String TABLE_NAME = "player";
        public static final String COL_PLAYER = "player";
        public static final String COL_PHOTO = "photo";
        public static final String COL_FB_ID = "fb_id";

        public static final int NUM_PLAYER = 1;
        public static final int NUM_PHOTO = 2;
        public static final int NUM_FB_ID = 3;

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COL_PLAYER + " TEXT," +
                        COL_PHOTO + " TEXT," +
                        COL_FB_ID + " TEXT);";

        public static final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        KrataScoreContract.CONTENT_URI,
                        "players");

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.gr.sv1jsb.kratascore.provider.players";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.gr.sv1jsb.kratascore.provider.players";

        public static final String[] PROJECTION_ALL =
                {   _ID,
                    COL_PLAYER,
                    COL_PHOTO,
                    COL_FB_ID
                };

        public static final String SORT_ORDER_DEFAULT =
                COL_PLAYER;

        public static final String SELECTION_NAME =
                COL_PLAYER + " LIKE ? ";

        public static final String SELECTION_MULTI_IDS =
                _ID + " IN ";
    }


    public static final class ScoreEntry implements  BaseColumns {
        public static final String TABLE_NAME = "score";
        public static final String COL_GAME_ID = "game_id";
        public static final String COL_PLAYER_ID = "player_id";
        public static final String COL_SCORE = "score";
        public static final int NUM_GAME_ID = 1;
        public static final int NUM_PLAYER = 2;
        public static final int NUM_SCORE = 3;
        public static final int NUM_PLAYER_PHOTO = 4;
        public static final int NUM_GAME_NAME = 1;      // Used with PROJECTION_JOINED_FULL
        public static final int NUM_GAME_STARTED = 3;   // Used with PROJECTION_JOINED_FULL

        public static final String TABLE_JOINED = "score LEFT OUTER JOIN " + PlayerEntry.TABLE_NAME +
                " ON (" + TABLE_NAME + "." + COL_PLAYER_ID + "=" + PlayerEntry.TABLE_NAME + "." + PlayerEntry._ID + ")";

        public static final String TABLE_JOINED_FULL = TABLE_JOINED + " LEFT OUTER JOIN " + GameEntry.TABLE_NAME +
                " ON (" + TABLE_NAME + "." + COL_GAME_ID + "=" + GameEntry.TABLE_NAME + "." +GameEntry._ID + ")";

        public static final String CREATE_TABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        COL_GAME_ID + " INTEGER REFERENCES " + GameEntry.TABLE_NAME + "(" + GameEntry._ID + ") ON DELETE CASCADE," +
                        COL_PLAYER_ID + " INTEGER REFERENCES " + PlayerEntry.TABLE_NAME + "(" + PlayerEntry._ID + ") ON DELETE CASCADE," +
                        COL_SCORE + " INTEGER);";
        public static final String CREATE_INDEX_GAME = "CREATE INDEX fk_game_id on " + TABLE_NAME + "(" + COL_GAME_ID + ");";
        public static final String CREATE_INDEX_PLAYER = "CREATE INDEX fk_player_id on " + TABLE_NAME + "(" + COL_PLAYER_ID + ");";

        public static final String DROP_TABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(
                        KrataScoreContract.CONTENT_URI,
                        "scores");

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/vnd.gr.sv1jsb.kratascore.provider.scores";

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/vnd.gr.sv1jsb.kratascore.provider.scores";

        public static final String[] PROJECTION_ALL =
                { _ID,
                  COL_GAME_ID,
                  COL_PLAYER_ID,
                  COL_SCORE
                };

        public static final String[] PROJECTION_JOINED =
                {       COL_PLAYER_ID + " as " + _ID,
                        COL_GAME_ID,
                        PlayerEntry.COL_PLAYER,
                        "sum(" + COL_SCORE + ")",
                        PlayerEntry.COL_PHOTO
                };

        public static final String[] PROJECTION_JOINED_FULL =
                {       "DISTINCT " + GameEntry.TABLE_NAME + "." + GameEntry._ID,
                        GameEntry.COL_NAME,
                        PlayerEntry.COL_PLAYER,
                        GameEntry.COL_STARTED
                };

        public  static  final  String[] PROJECTION_PLAYER_SCORES =
                {
                        TABLE_NAME + "." + _ID,
                        GameEntry.COL_NAME,
                        PlayerEntry.COL_PLAYER,
                        COL_SCORE
                };

        public static final String SELECTION_GAME_ID =
                COL_GAME_ID + " = ? ";

        public static final String GROUP_BY =
                COL_PLAYER_ID;

        public static final String SORT_ORDER_DEFAULT =
                TABLE_NAME + "." +_ID + " ASC";

        public static final String SORT_SCORE_DESC =
                " sum(" +COL_SCORE +")" + " DESC";

        public static final String SORT_SCORE_ASC =
                " sum(" + COL_SCORE +")" + " ASC";
    }

    public static final int GAMES_LIST = 1;
    public static final int GAMES_ID = 2;
    public static final int SCORE_LIST = 3;
    public static final int SCORE_ID = 4;
    public static final int PLAYERS_LIST = 5;
    public static final int PLAYER_ID = 6;
    public static final int SCORES_PLAYER_ID = 7;
    public static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(KrataScoreContract.AUTHORITY, "games", GAMES_LIST);
        URI_MATCHER.addURI(KrataScoreContract.AUTHORITY, "games/#", GAMES_ID);
        URI_MATCHER.addURI(KrataScoreContract.AUTHORITY, "scores", SCORE_LIST);
        URI_MATCHER.addURI(KrataScoreContract.AUTHORITY, "scores/#", SCORE_ID);
        URI_MATCHER.addURI(KrataScoreContract.AUTHORITY, "scores/player/#", SCORES_PLAYER_ID);
        URI_MATCHER.addURI(KrataScoreContract.AUTHORITY, "players", PLAYERS_LIST);
        URI_MATCHER.addURI(KrataScoreContract.AUTHORITY, "players/#", PLAYER_ID);
    }
}
