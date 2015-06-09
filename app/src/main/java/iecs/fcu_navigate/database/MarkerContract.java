package iecs.fcu_navigate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

public final class MarkerContract {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + MarkerEntry.TABLE_NAME + " (" +
            MarkerEntry._ID + " INTEGER PRIMARY KEY," +
            MarkerEntry.COLUMN_NAME_CATEGORY_ID    + INTEGER_TYPE + COMMA_SEP +
            MarkerEntry.COLUMN_NAME_NAME           + TEXT_TYPE    + COMMA_SEP +
            MarkerEntry.COLUMN_NAME_BUILDING_ID    + INTEGER_TYPE + COMMA_SEP +
            MarkerEntry.COLUMN_NAME_FLOOR          + INTEGER_TYPE + COMMA_SEP +
            MarkerEntry.COLUMN_NAME_LATITUDE       + REAL_TYPE    + COMMA_SEP +
            MarkerEntry.COLUMN_NAME_LONGITUDE      + REAL_TYPE    + COMMA_SEP +
            MarkerEntry.COLUMN_NAME_IMAGE_NAME + TEXT_TYPE    + COMMA_SEP +
            MarkerEntry.COLUMN_NAME_CUSTOM + TEXT_TYPE    + COMMA_SEP +
            "FOREIGN KEY(" + MarkerEntry.COLUMN_NAME_CATEGORY_ID + ")" +
                " REFERENCES " + CategoryContract.CategoryEntry.TABLE_NAME + "(" + CategoryContract.CategoryEntry._ID +  ")" + COMMA_SEP +
            "FOREIGN KEY(" + MarkerEntry.COLUMN_NAME_BUILDING_ID + ")" +
                " REFERENCES " + BuildingContract.BuildingEntry.TABLE_NAME + "(" + BuildingContract.BuildingEntry._ID +  ")" +
        " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MarkerEntry.TABLE_NAME;

    public MarkerContract() {
    }

    public static void insertDefaultData(SQLiteDatabase db) {
        Item[] markers = new Item[] {
            new Item("演講廳", "第一國際會議廳", "丘逢甲紀念館",  2, 24.178355, 120.648098, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第二國際會議廳", "丘逢甲紀念館",  3, 24.178351, 120.647988, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第三國際會議廳", "資訊電機館"  ,  2, 24.179299, 120.649739, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第四國際會議廳", "人言大樓"    , -1, 0.0, 0.0, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第五國際會議廳", "人言大樓"    , -1, 0.0, 0.0, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第六國際會議廳", "人言大樓"    , -1, 0.0, 0.0, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第七國際會議廳", "人言大樓"    , -1, 0.0, 0.0, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第八國際會議廳", "商學大樓"    ,  8, 0.0, 0.0, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第九國際會議廳", "學思樓"      ,  2, 0.0, 0.0, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
        };

        for (Item marker : markers) {
            db.insert(MarkerEntry.TABLE_NAME, null, marker.generateContentValues(db));
        }
    }

    public static Item[] getItembyCategory(MarkerDBHelper dbHelper, String categoryName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                MarkerEntry.TABLE_NAME,                                                   //table
                null,                                                                     //columns
                MarkerEntry.COLUMN_NAME_CATEGORY_ID + "= ?",                              //selection
                new String[]{Long.toString(CategoryContract.getID(db, categoryName))},    //selectionArgs
                null,                                                                     //groupBy
                null,                                                                     //having
                MarkerEntry._ID + " ASC"                                                  //orderBy
        );

        int i = 0;
        Item[] result = new Item[c.getCount()];
        Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
        while (c.moveToNext()) {
            result[i++] = new Item(
                    categoryName,
                    c.getString(2),
                    BuildingContract.getNamebyId(db, c.getLong(3)),
                    c.getInt(4),
                    c.getDouble(5),
                    c.getDouble(6),
                    c.getString(7),
                    (Map<String, String>) new Gson().fromJson(c.getString(8), stringStringMap)
            );
        }
        c.close();

        return result;
    }

    public static abstract class MarkerEntry implements BaseColumns {
        public static final String TABLE_NAME = "marker";
        public static final String COLUMN_NAME_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_BUILDING_ID = "building_id";
        public static final String COLUMN_NAME_FLOOR = "floor";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_IMAGE_NAME = "image_name";
        public static final String COLUMN_NAME_CUSTOM = "custom";

        public static final String CUSTOM_KEY_CLASS_NAME = "class_name";
    }

    public static class Item implements Serializable {
        private String category_name;
        private String name;
        private String building_name;
        private int floor;
        private double latitude;
        private double longitude;
        private String image_name;
        private Map<String, String> customData;

        public Item(String category_name, String name, String building_name, int floor, double latitude, double longitude, String image_name, Map<String, String> customData) {
            this.category_name = category_name;
            this.name = name;
            this.building_name = building_name;
            this.floor = floor;
            this.latitude = latitude;
            this.longitude = longitude;
            this.image_name = image_name;
            this.customData = customData;
        }

        public String getCategoryName() {
            return category_name;
        }

        public String getName() {
            return name;
        }

        public String getBuildingName() {
            return building_name;
        }

        public int getFloor() {
            return floor;
        }

        public Map<String, String> getCustomData() {
            return customData;
        }

        public void setCustomData(Map<String, String> customData) {
            this.customData = customData;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getImageName() {
            return image_name;
        }

        public ContentValues generateContentValues(SQLiteDatabase db) {
            ContentValues values = new ContentValues();

            values.put(MarkerEntry.COLUMN_NAME_CATEGORY_ID, CategoryContract.getID(db, this.category_name));
            values.put(MarkerEntry.COLUMN_NAME_NAME, this.name);
            values.put(MarkerEntry.COLUMN_NAME_BUILDING_ID, BuildingContract.getID(db, this.building_name));
            values.put(MarkerEntry.COLUMN_NAME_FLOOR, this.floor);
            values.put(MarkerEntry.COLUMN_NAME_LATITUDE, this.latitude);
            values.put(MarkerEntry.COLUMN_NAME_LONGITUDE, this.longitude);
            values.put(MarkerEntry.COLUMN_NAME_IMAGE_NAME, this.image_name);
            values.put(MarkerEntry.COLUMN_NAME_CUSTOM, new Gson().toJson(this.customData));

            return values;
        }
    }
}
