package iecs.fcu_navigate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * 定義地標分類的資料表
 */
public final class CategoryContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                    CategoryEntry._ID + " INTEGER PRIMARY KEY," +
                    CategoryEntry.COLUMN_NAME_NAME + TEXT_TYPE +
            " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME;

    public CategoryContract() {
    }

    public static void insertDefaultData(SQLiteDatabase db) {
        for (String name : new String[] { "演講廳", "自動販賣機", "建築物" }) {
            ContentValues values = new ContentValues();
            values.put(CategoryEntry.COLUMN_NAME_NAME, name);

            db.insert(CategoryEntry.TABLE_NAME, null, values);
        }
    }

    public static String[] getAllCategoryName(MarkerDBHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                CategoryEntry.TABLE_NAME,                       //table
                new String[]{CategoryEntry.COLUMN_NAME_NAME},   //columns
                null,                                           //selection
                null,                                           //selectionArgs
                null,                                           //groupBy
                null,                                           //having
                CategoryEntry._ID + " ASC"                      //orderBy
        );

        int i = 0;
        int COLUMN_NAME_NAME_INDDX = c.getColumnIndexOrThrow(CategoryEntry.COLUMN_NAME_NAME);
        String[] result = new String[c.getCount()];
        while (c.moveToNext()) {
            result[i++] = c.getString(COLUMN_NAME_NAME_INDDX);
        }
        c.close();

        return result;
    }

    public static long getID(SQLiteDatabase db, String name) {
        Cursor c = db.query(
                CategoryEntry.TABLE_NAME,                                          //table
                new String[] {CategoryEntry._ID, CategoryEntry.COLUMN_NAME_NAME},  //columns
                CategoryEntry.COLUMN_NAME_NAME + "= ?",                            //selection
                new String[] {name},                                               //selectionArgs
                null,                                                              //groupBy
                null,                                                              //having
                CategoryEntry._ID + " ASC"                                         //orderBy
        );

        long result = 0;
        if (c.moveToFirst()) {
            result = c.getInt(c.getColumnIndexOrThrow(CategoryEntry._ID));
        }
        c.close();

        return result;
    }

    public static abstract class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_NAME = "name";
    }

}
