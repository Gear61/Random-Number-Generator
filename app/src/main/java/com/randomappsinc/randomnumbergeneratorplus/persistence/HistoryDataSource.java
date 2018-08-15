package com.randomappsinc.randomnumbergeneratorplus.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseArray;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;

import java.util.ArrayList;
import java.util.List;

public class HistoryDataSource {

    private static final int MAX_RECORDS_PER_TYPE = 20;

    private static HistoryDataSource instance;

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Handler backgroundHandler;

    public static HistoryDataSource get(Context context) {
        if (instance == null) {
            instance = getSync(context);
        }
        return instance;
    }

    private static synchronized HistoryDataSource getSync(Context context) {
        if (instance == null) {
            instance = new HistoryDataSource(context);
        }
        return instance;
    }

    private HistoryDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
        HandlerThread handlerThread = new HandlerThread("Database");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    // Open connection to database
    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Terminate connection to database
    private void close() {
        dbHelper.close();
    }

    public SparseArray<List<CharSequence>> getHistory() {
        SparseArray<List<CharSequence>> rngTypeToHistoryList = new SparseArray<>();

        open();
        String[] columns = {
                MySQLiteHelper.COLUMN_RECORD_TEXT,
                MySQLiteHelper.COLUMN_TIME_INSERTED};
        String selection = MySQLiteHelper.COLUMN_RNG_TYPE + " = ?";
        String orderBy = MySQLiteHelper.COLUMN_TIME_INSERTED + " DESC";
        int[] rngTypes = new int[] {RNGType.NUMBER, RNGType.DICE, RNGType.COINS, RNGType.LOTTO};
        for (int rngType : rngTypes) {
            List<CharSequence> history = new ArrayList<>();
            String[] selectionArgs = {String.valueOf(rngType)};
            Cursor cursor = database.query(
                    MySQLiteHelper.TABLE_NAME,
                    columns,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    orderBy,
                    String.valueOf(MAX_RECORDS_PER_TYPE));
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    history.add(cursor.getString(0));

                }
                cursor.close();
            }

            rngTypeToHistoryList.append(rngType, history);
        }
        close();
        return rngTypeToHistoryList;
    }

    public void addHistoryRecord(@RNGType final int rngType, final String recordText) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                open();
                ContentValues values = new ContentValues();
                values.put(MySQLiteHelper.COLUMN_RNG_TYPE, rngType);
                values.put(MySQLiteHelper.COLUMN_RECORD_TEXT, recordText);
                values.put(MySQLiteHelper.COLUMN_TIME_INSERTED, System.currentTimeMillis());
                database.insert(MySQLiteHelper.TABLE_NAME, null, values);
                close();
            }
        });
    }

    public void deleteHistory(@RNGType final int rngType) {
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                open();
                String whereArgs[] = {String.valueOf(rngType)};
                database.delete(
                        MySQLiteHelper.TABLE_NAME,
                        MySQLiteHelper.COLUMN_RNG_TYPE + " = ?",
                        whereArgs);
                close();
            }
        });
    }
}
