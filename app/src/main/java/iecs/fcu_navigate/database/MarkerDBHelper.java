package iecs.fcu_navigate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MarkerDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "Marker.db";

    public static MarkerDBHelper instance = null;

    public static void initialize(Context context) {
        if (instance == null) {
            instance = new MarkerDBHelper(context);
        }
    }

    private MarkerDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CategoryContract.SQL_CREATE_ENTRIES);
        db.execSQL(BuildingContract.SQL_CREATE_ENTRIES);
        db.execSQL(MarkerContract.SQL_CREATE_ENTRIES);

        CategoryContract.insertDefaultData(db);
        BuildingContract.insertDefaultData(db);
        MarkerContract.insertDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: 先方便起見，刪除所有舊資料表
        db.execSQL(CategoryContract.SQL_DELETE_ENTRIES);
        db.execSQL(BuildingContract.SQL_DELETE_ENTRIES);
        db.execSQL(MarkerContract.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
