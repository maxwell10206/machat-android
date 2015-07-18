package machat.machat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import machat.machat.FavoriteItem;

/**
 * Created by Admin on 7/17/2015.
 */
public class FavoriteListDbAdapter {

    private static final String DATABASE_TABLE = "favoriteList";
    private static final String DATABASE_NAME = "machat";

    public static final String COLUMN_NAME_HOUSE_ID = "_id";
    public static final String COLUMN_NAME_NAME = "name";
    public static final String COLUMN_USER_ID = "userId";
    public static final String COLUMN_NAME_MESSAGE = "message";
    public static final String COLUMN_NAME_MSG_STATUS = "status";
    public static final String COLUMN_NAME_READ = "read";
    public static final String COLUMN_NAME_TIME = "time";
    public static final String COLUMN_NAME_MUTE = "mute";
    public static final String COLUMN_NAME_BLOCK = "block";

    public static final String[] ALL_KEYS = new String[] {COLUMN_NAME_HOUSE_ID, COLUMN_USER_ID, COLUMN_NAME_NAME, COLUMN_NAME_MESSAGE,
    COLUMN_NAME_MSG_STATUS, COLUMN_NAME_READ, COLUMN_NAME_TIME, COLUMN_NAME_MUTE, COLUMN_NAME_BLOCK};

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DATABASE_TABLE + " (" +
                    COLUMN_NAME_HOUSE_ID + " INTEGER PRIMARY KEY," +
                    COLUMN_USER_ID + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_TIME + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_MSG_STATUS + TEXT_TYPE + COMMA_SEP +
                    COLUMN_NAME_MUTE + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_BLOCK + INTEGER_TYPE + COMMA_SEP +
                    COLUMN_NAME_READ + INTEGER_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DATABASE_TABLE;

    private static final int DATABASE_VERSION = 3;

    private final Context context;

    private FavoriteListDbHelper myDBHelper;
    private SQLiteDatabase db;

    public FavoriteListDbAdapter(Context context){
        this.context = context;
        myDBHelper = new FavoriteListDbHelper(context);
    }

    public FavoriteListDbAdapter open(){
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        myDBHelper.close();
    }

    public boolean deleteRow(long houseId){
        String where = COLUMN_NAME_HOUSE_ID + "=" + houseId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll(){
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(COLUMN_NAME_HOUSE_ID);
        if(c.moveToFirst()){
            do{
                deleteRow(c.getLong((int) rowId));
            }while(c.moveToNext());
        }
        c.close();
    }

    public Cursor getAllRows(){
        String where = null;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, COLUMN_NAME_TIME + " DESC", null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getRow(long rowId){
        String where = COLUMN_NAME_HOUSE_ID + "=" + rowId;
        Cursor c = db.query(true, DATABASE_TABLE, ALL_KEYS, where, null, null, null, null, null);
        if(c != null){
            c.moveToFirst();
        }
        return c;
    }

    public long insertRow(FavoriteItem favoriteItem){

        ContentValues initialValues = new ContentValues();
        initialValues.put(COLUMN_NAME_HOUSE_ID, favoriteItem.getUserId());
        initialValues.put(COLUMN_NAME_NAME, favoriteItem.getName());
        initialValues.put(COLUMN_NAME_MESSAGE, favoriteItem.getMessage().getMessage());
        initialValues.put(COLUMN_USER_ID, favoriteItem.getMessage().getUserId());
        initialValues.put(COLUMN_NAME_MSG_STATUS, favoriteItem.getMessage().getStatus());
        initialValues.put(COLUMN_NAME_MUTE, favoriteItem.isMute());
        initialValues.put(COLUMN_NAME_TIME, favoriteItem.getMessage().getTime());
        initialValues.put(COLUMN_NAME_READ, favoriteItem.isRead());
        initialValues.put(COLUMN_NAME_BLOCK, favoriteItem.isBlock());

        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean updateRow(FavoriteItem favoriteItem){
        String where = COLUMN_NAME_HOUSE_ID + "=" + favoriteItem.getUserId();

        ContentValues newValues = new ContentValues();
        newValues.put(COLUMN_NAME_HOUSE_ID, favoriteItem.getUserId());
        newValues.put(COLUMN_USER_ID, favoriteItem.getMessage().getUserId());
        newValues.put(COLUMN_NAME_MESSAGE, favoriteItem.getMessage().getMessage());
        newValues.put(COLUMN_NAME_NAME, favoriteItem.getName());
        newValues.put(COLUMN_NAME_MSG_STATUS, favoriteItem.getMessage().getStatus());
        newValues.put(COLUMN_NAME_MUTE, favoriteItem.isMute());
        newValues.put(COLUMN_NAME_TIME, favoriteItem.getMessage().getTime());
        newValues.put(COLUMN_NAME_READ, favoriteItem.isRead());
        newValues.put(COLUMN_NAME_BLOCK, favoriteItem.isBlock());

        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    private static class FavoriteListDbHelper extends SQLiteOpenHelper {
        FavoriteListDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

}
