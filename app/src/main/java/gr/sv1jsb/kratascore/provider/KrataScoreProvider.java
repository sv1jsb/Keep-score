package gr.sv1jsb.kratascore.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import gr.sv1jsb.kratascore.persistance.KrataScoreContract;
import gr.sv1jsb.kratascore.persistance.KrataScoreDB;

public class KrataScoreProvider extends ContentProvider {
    public KrataScoreProvider() {
    }
    private KrataScoreDB mHelper = null;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
        int delCount = 0;
        String idStr, where;
        switch (KrataScoreContract.URI_MATCHER.match(uri)) {
            case KrataScoreContract.GAMES_LIST:
                delCount = db.delete(
                        KrataScoreContract.GameEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case KrataScoreContract.GAMES_ID:
                idStr = uri.getLastPathSegment();
                where = KrataScoreContract.GameEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        KrataScoreContract.GameEntry.TABLE_NAME,
                        where,
                        selectionArgs);
                break;
            case KrataScoreContract.PLAYERS_LIST:
                delCount = db.delete(
                        KrataScoreContract.PlayerEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case KrataScoreContract.PLAYER_ID:
                idStr = uri.getLastPathSegment();
                where = KrataScoreContract.PlayerEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                delCount = db.delete(
                        KrataScoreContract.PlayerEntry.TABLE_NAME,
                        where,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (delCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return delCount;
    }

    @Override
    public String getType(Uri uri) {
        switch (KrataScoreContract.URI_MATCHER.match(uri)) {
            case KrataScoreContract.GAMES_LIST:
                return KrataScoreContract.GameEntry.CONTENT_TYPE;
            case KrataScoreContract.GAMES_ID:
                return KrataScoreContract.GameEntry.CONTENT_ITEM_TYPE;
            case KrataScoreContract.SCORE_LIST:
                return KrataScoreContract.ScoreEntry.CONTENT_TYPE;
            case KrataScoreContract.SCORE_ID:
                return KrataScoreContract.ScoreEntry.CONTENT_ITEM_TYPE;
            case KrataScoreContract.PLAYERS_LIST:
                return KrataScoreContract.PlayerEntry.CONTENT_TYPE;
            case KrataScoreContract.PLAYER_ID:
                return KrataScoreContract.PlayerEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
        long id;
        switch (KrataScoreContract.URI_MATCHER.match(uri)) {
            case KrataScoreContract.GAMES_LIST:
                id = db.insert(KrataScoreContract.GameEntry.TABLE_NAME, null, values);
                break;
            case KrataScoreContract.SCORE_LIST:
                id = db.insert(KrataScoreContract.ScoreEntry.TABLE_NAME, null, values);
                break;
            case KrataScoreContract.PLAYERS_LIST:
                id = db.insert(KrataScoreContract.PlayerEntry.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI for insertion: " + uri);
        }
        return getUriForId(id, uri);
    }

    @Override
    public boolean onCreate() {
        mHelper = new KrataScoreDB(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        String group_by = null;
        switch (KrataScoreContract.URI_MATCHER.match(uri)) {
            case KrataScoreContract.GAMES_LIST:
                builder.setTables(KrataScoreContract.GameEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = KrataScoreContract.GameEntry.SORT_ORDER_DEFAULT;
                }
                break;
            case KrataScoreContract.GAMES_ID:
                builder.setTables(KrataScoreContract.GameEntry.TABLE_NAME);
                builder.appendWhere(KrataScoreContract.GameEntry._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case KrataScoreContract.SCORE_LIST:
                builder.setTables(KrataScoreContract.ScoreEntry.TABLE_JOINED);
                group_by = KrataScoreContract.ScoreEntry.GROUP_BY;
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = KrataScoreContract.ScoreEntry.SORT_ORDER_DEFAULT;
                }
                break;
            case KrataScoreContract.SCORES_PLAYER_ID:
                builder.setTables(KrataScoreContract.ScoreEntry.TABLE_JOINED_FULL);
                builder.appendWhere(KrataScoreContract.ScoreEntry.COL_PLAYER_ID + " = " +
                        uri.getLastPathSegment());
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = KrataScoreContract.ScoreEntry.SORT_ORDER_DEFAULT;
                }
                break;
            case KrataScoreContract.SCORE_ID:
                builder.setTables(KrataScoreContract.ScoreEntry.TABLE_JOINED);
                builder.appendWhere(KrataScoreContract.ScoreEntry._ID + " = " +
                        uri.getLastPathSegment());
                break;
            case KrataScoreContract.PLAYERS_LIST:
                builder.setTables(KrataScoreContract.PlayerEntry.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = KrataScoreContract.PlayerEntry.SORT_ORDER_DEFAULT;
                }
                break;
            case KrataScoreContract.PLAYER_ID:
                builder.setTables(KrataScoreContract.PlayerEntry.TABLE_NAME);
                builder.appendWhere(KrataScoreContract.PlayerEntry._ID + " = " +
                        uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor cursor =
                builder.query(
                        db,
                        projection,
                        selection,
                        selectionArgs,
                        group_by,
                        null,
                        sortOrder);
        cursor.setNotificationUri(
                getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = mHelper.getReadableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");
        int updateCount = 0;
        String idStr;
        String where;
        switch (KrataScoreContract.URI_MATCHER.match(uri)) {
            case KrataScoreContract.GAMES_LIST:
                updateCount = db.update(
                        KrataScoreContract.GameEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case KrataScoreContract.GAMES_ID:
                idStr = uri.getLastPathSegment();
                where = KrataScoreContract.GameEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        KrataScoreContract.GameEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs);
                break;
            case KrataScoreContract.SCORE_LIST:
                updateCount = db.update(
                        KrataScoreContract.ScoreEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case KrataScoreContract.SCORE_ID:
                idStr = uri.getLastPathSegment();
                where = KrataScoreContract.ScoreEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        KrataScoreContract.ScoreEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs);
                break;
            case KrataScoreContract.PLAYERS_LIST:
                updateCount = db.update(
                        KrataScoreContract.PlayerEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case KrataScoreContract.PLAYER_ID:
                idStr = uri.getLastPathSegment();
                where = KrataScoreContract.PlayerEntry._ID + " = " + idStr;
                if (!TextUtils.isEmpty(selection)) {
                    where += " AND " + selection;
                }
                updateCount = db.update(
                        KrataScoreContract.PlayerEntry.TABLE_NAME,
                        values,
                        where,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        if (updateCount > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            getContext().
                getContentResolver().
                notifyChange(itemUri, null);
            return itemUri;
        }
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }
}
