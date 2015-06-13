package iecs.fcu_navigate.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

import iecs.fcu_navigate.ListFragment;

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

    public static final Item ItemNothing = new Item("無", "無", "無",  0, 0.0, 0.0, "", null) {
        @Override
        public boolean isVirtual() {
            return true;
        }
    };

    public static final Item ItemMyLocation = new Item("無", "無", "無",  0, 0.0, 0.0, "", null);

    public MarkerContract() {
    }

    public static void insertDefaultData(SQLiteDatabase db) {
        Item[] markers = new Item[] {
            new Item("演講廳", "第一國際會議廳", "丘逢甲紀念館",  2, 24.178355, 120.648098, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第二國際會議廳", "丘逢甲紀念館",  3, 24.178351, 120.647988, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第三國際會議廳", "資訊電機館"  ,  2, 24.179299, 120.649739, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第四國際會議廳", "人言大樓"    , -1, 24.179148, 120.648465, "meeting_place_4", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第五國際會議廳", "人言大樓"    , -1, 24.179149, 120.648647, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第六國際會議廳", "人言大樓"    , -1, 24.179150, 120.648811, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第七國際會議廳", "人言大樓"    , -1, 24.179310, 120.648889, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第八國際會議廳", "商學大樓"    ,  8, 24.178406, 120.649825, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
            new Item("演講廳", "第九國際會議廳", "學思樓"      ,  2, 24.181397, 120.646697, "", ImmutableMap.of(MarkerEntry.CUSTOM_KEY_CLASS_NAME, "")),
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

    public static class Item implements ListFragment.ListItem {
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

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getImageName() {
            return image_name;
        }

        @Override
        public boolean isVirtual() {
            return false;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Item)) return false;

            Item item = (Item) o;

            if (floor != item.floor) return false;
            if (Double.compare(item.latitude, latitude) != 0) return false;
            if (Double.compare(item.longitude, longitude) != 0) return false;
            if (category_name != null ? !category_name.equals(item.category_name) : item.category_name != null)
                return false;
            if (name != null ? !name.equals(item.name) : item.name != null) return false;
            if (building_name != null ? !building_name.equals(item.building_name) : item.building_name != null)
                return false;
            if (image_name != null ? !image_name.equals(item.image_name) : item.image_name != null)
                return false;
            return !(customData != null ? !customData.equals(item.customData) : item.customData != null);

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = category_name != null ? category_name.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (building_name != null ? building_name.hashCode() : 0);
            result = 31 * result + floor;
            temp = Double.doubleToLongBits(latitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(longitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (image_name != null ? image_name.hashCode() : 0);
            result = 31 * result + (customData != null ? customData.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
