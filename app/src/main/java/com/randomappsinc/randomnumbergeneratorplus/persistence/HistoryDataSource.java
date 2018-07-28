package com.randomappsinc.randomnumbergeneratorplus.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;

import java.util.ArrayList;
import java.util.List;

public class HistoryDataSource {

    private static HistoryDataSource instance;

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private Handler backgroundHandler;

    public static HistoryDataSource get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized HistoryDataSource getSync() {
        if (instance == null) {
            instance = new HistoryDataSource();
        }
        return instance;
    }

    private HistoryDataSource() {
        dbHelper = new MySQLiteHelper();
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

    public List<CharSequence> getHistory(@RNGType int rngType) {
        List<CharSequence> history = new ArrayList<>();
        open();
        String[] columns = {MySQLiteHelper.COLUMN_RECORD_TEXT, MySQLiteHelper.COLUMN_TIME_INSERTED};
        String selection = MySQLiteHelper.COLUMN_RNG_TYPE + " = ?";
        String[] selectionArgs = {String.valueOf(rngType)};
        String orderBy = MySQLiteHelper.COLUMN_TIME_INSERTED + " DESC";
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, columns, selection,
                selectionArgs, null, null, orderBy);
        while (cursor.moveToNext()) {
            history.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return history;
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
}
