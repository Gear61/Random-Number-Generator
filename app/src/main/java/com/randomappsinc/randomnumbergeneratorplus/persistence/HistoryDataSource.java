package com.randomappsinc.randomnumbergeneratorplus.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;

import com.randomappsinc.randomnumbergeneratorplus.constants.RNGType;
import com.randomappsinc.randomnumbergeneratorplus.models.HistoryRecord;

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

    public List<HistoryRecord> getHistory() {
        List<HistoryRecord> historyList = new ArrayList<>();
        open();
        String[] columns = {
                MySQLiteHelper.COLUMN_RNG_TYPE,
                MySQLiteHelper.COLUMN_RECORD_TEXT,
                MySQLiteHelper.COLUMN_TIME_INSERTED};
        String orderBy = MySQLiteHelper.COLUMN_TIME_INSERTED + " DESC";
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, columns, null,
                null, null, null, orderBy);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                HistoryRecord historyRecord = new HistoryRecord();
                historyRecord.setRngType(cursor.getInt(0));
                historyRecord.setRecordText(cursor.getString(1));
                historyList.add(historyRecord);
            }
            cursor.close();
        }
        close();
        return historyList;
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
