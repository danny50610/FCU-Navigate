package iecs.fcu_navigate.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class BuildingContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + BuildingEntry.TABLE_NAME + " (" +
                    BuildingEntry._ID + " INTEGER PRIMARY KEY," +
                    BuildingEntry.COLUMN_NAME_NAME + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + BuildingEntry.TABLE_NAME;

    public BuildingContract() {
    }

    public static void insertDefaultData(SQLiteDatabase db) {
        String[] buildingList = new String[] {
            "學思樓", "土木水利館", "育樂館", "語文大樓", "建築館", "忠勤樓",
            "行政一館", "行政二館", "丘逢甲紀念館", "工學院", "人言大樓",
            "理學大樓", "教堂", "體育館", "圖書館", "科學與航太館",
            "商學大樓", "資訊電機館", "人文社會館", "電子通訊館"
        };

        for (String name : buildingList) {
            ContentValues values = new ContentValues();
            values.put(BuildingEntry.COLUMN_NAME_NAME, name);

            db.insert(BuildingEntry.TABLE_NAME, null, values);
        }
    }

    public static abstract class BuildingEntry implements BaseColumns {
        public static final String TABLE_NAME = "building";
        public static final String COLUMN_NAME_NAME = "name";
    }

}
