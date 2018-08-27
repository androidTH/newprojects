package cn.liaox.cachelib.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 *
 */

public class DBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "user_info_db";
    private static final int DB_VERSION = 1;
    private static Context mContext;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserHelper.TABLE_NAME + " ("
            + UserHelper.COLUMN_NAME_NICK +" TEXT, "
            + UserHelper.COLUMN_NAME_HEAD_URL +" TEXT, "
            + UserHelper.COLUMN_NAME_TIME +" TEXT, "
            + UserHelper.COLUMN_NAME_ID +" TEXT, "
            + "id INTEGER PRIMARY KEY AUTOINCREMENT );";

    private DBHelper(){
        super(mContext,DB_NAME,null,DB_VERSION);
    }

    private DBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(USERNAME_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public static DBHelper getInstance(Context context){
        mContext = context.getApplicationContext();
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DBHelper INSTANCE = new DBHelper();
    }
}