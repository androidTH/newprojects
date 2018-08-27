package cn.liaox.cachelib.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import cn.liaox.cachelib.bean.UserBean;

/**
 *
 */

public class UserHelper {
    public static final String TABLE_NAME = "users_table";
    public static final String COLUMN_NAME_ID = "userId";
    public static final String COLUMN_NAME_NICK = "nick";
    public static final String COLUMN_NAME_HEAD_URL = "head_url";
    public static final String COLUMN_NAME_TIME = "time";

    private static DBHelper dbHelper;

    private UserHelper() {
    }

    public static UserHelper getInstance(Context context) {
        dbHelper = DBHelper.getInstance(context);
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private final static UserHelper INSTANCE = new UserHelper();
    }

    public void saveUser(String userId, String name, String headUrl, long time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db != null && db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME_ID, userId);
            values.put(COLUMN_NAME_TIME, time);
            if (name != null) {
                values.put(COLUMN_NAME_NICK, name);
            }
            if (headUrl != null) {
                values.put(COLUMN_NAME_HEAD_URL, headUrl);
            }
            long id = getId(db, userId);
            if (id == -1) {
                db.insert(TABLE_NAME, null, values);
            } else {
                if (isNeedUpdate(db, userId, name, headUrl)) {
                    db.update(TABLE_NAME, values, " id = ? ", new String[]{id + ""});
                }
            }
//            db.replace(TABLE_NAME, null, values);
        }
    }

    private long getId(SQLiteDatabase db, String userId) {
        String sql = "select id from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = ? limit 1 ";
        Cursor cursor = db.rawQuery(sql, new String[]{userId});
        if (cursor == null) {
            return -1;
        }
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            cursor.close();
            return id;
        } else {
            return -1;
        }
    }

    private boolean isNeedUpdate(SQLiteDatabase db, String userId, String nickName, String headUrl) {
        String sql = "select * from " + TABLE_NAME + " where " + COLUMN_NAME_ID + " = ? limit 1 ";
        Cursor cursor = db.rawQuery(sql, new String[]{userId});
        if (cursor == null) {
            return true;
        }
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
            String url = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HEAD_URL));
            if (TextUtils.equals(name, nickName) && TextUtils.equals(url, headUrl)) {
                return false;
            }
        } else {
            return true;
        }
        cursor.close();
        return true;
    }

    public UserBean getUser(String userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if (db != null && db.isOpen()) {
            String sql = "select * from " + TABLE_NAME
                    + " where " + COLUMN_NAME_ID + " = ? ";
            Cursor cursor = db.rawQuery(sql, new String[]{userId});
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
                String url = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_HEAD_URL));
                long time = cursor.getLong(cursor.getColumnIndex(COLUMN_NAME_TIME));
                UserBean user = new UserBean(time);
                user.setHeadUrl(url);
                user.setNickName(name);
                user.setUserId(userId);
                cursor.close();
                return user;
            }
        }
        return null;
    }
}
