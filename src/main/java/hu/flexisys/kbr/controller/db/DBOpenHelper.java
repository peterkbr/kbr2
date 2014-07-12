package hu.flexisys.kbr.controller.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Peter on 2014.07.01..
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public DBOpenHelper(Context context, String path, int version) {
        super(context, path, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String script : DBScripts.createDBStrings()) {
            db.execSQL(script);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
    }
}