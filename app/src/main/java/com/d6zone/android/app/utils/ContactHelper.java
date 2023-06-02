package com.d6zone.android.app.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.d6zone.android.app.models.PhoneBookEntity;
import com.d6zone.android.app.models.PhoneNumEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * author : jinjiarui
 * time   : 2020/02/15
 * desc   :
 * version:
 */
public class ContactHelper {
    private static final String TAG = "ContactHelper";

    private static final String[] PROJECTION = new String[]{
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private static final String[] PROJECTIONPPHONE = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private static final String[] NEWPROJECTION = new String[]{
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
    };

    private static final String[] PROJECTIONPHONE = new String[]{
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private List<PhoneBookEntity> contacts = new ArrayList<>();

    private List<PhoneNumEntity> PhoneNums = new ArrayList<>();

    private ContactHelper() {

    }

    public static ContactHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final ContactHelper INSTANCE = new ContactHelper();
    }

    /**
     * 获取通讯录好友
     *
     * @param context 上下文
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContacts(Context context) {
        long currentTimeMillis = System.currentTimeMillis();

        contacts.clear();
        Cursor cursor = null;
        Cursor phoneCursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, NEWPROJECTION, null, null, "sort_key");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //获取联系人电话号码
                    phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            PROJECTIONPHONE, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    String mobileNo = "";
                    if(phoneCursor.moveToNext()){
                        mobileNo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
//                    while (phoneCursor.moveToNext()) {
//                        mobileNo = mobileNo.replace("-", "");
//                        mobileNo = mobileNo.replace(" ", "");
//                    }
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(phoneCursor!=null){
                phoneCursor.close();
            }
            if (cursor != null) {
                Log.d(TAG, "获取所有联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis) + "，共计：" + cursor.getCount());
                cursor.close();
            }
        }
        return contacts;
    }


    /**
     * 获取通讯录好友
     *
     * @param context 上下文
     * @return 联系人集合
     */
    public String getSbContacts(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        Cursor cursor = null;
        Cursor phoneCursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, NEWPROJECTION, null, null, "sort_key");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    //获取联系人电话号码
                    phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            PROJECTIONPHONE, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
                    String mobileNo = "";
                    if(phoneCursor.moveToNext()){
                        mobileNo = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    mobileNo.replace("-", "").replace(" ", "");
                    sb.append(mobileNo).append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(phoneCursor!=null){
                phoneCursor.close();
            }
            if (cursor != null) {
                Log.d(TAG, "获取所有联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis) + "，共计：" + cursor.getCount());
                cursor.close();
            }
        }
        return sb.toString();
    }

    /**
     * 获取所有联系人
     *
     * @param context 上下文
     * @return 联系人集合
     */
    public List<PhoneNumEntity> getAllPhone(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        PhoneNums.clear();
        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTIONPPHONE, null, null, "sort_key");
            if (cursor != null) {
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    PhoneNums.add(new PhoneNumEntity(mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                Log.d(TAG, "获取所有联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis) + "，共计：" + cursor.getCount());
                cursor.close();
            }
        }
        return PhoneNums;
    }

    /**
     * 获取所有联系人
     *
     * @param context 上下文
     * @return 联系人集合
     */
    public String getAllStringPhone(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTIONPPHONE, null, null, "sort_key");
            if (cursor != null) {
                int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    sb.append(mobileNo.replace("-", "").replace(" ", ""));
                    sb.append(",");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                Log.d(TAG, "获取所有联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis) + "，共计：" + cursor.getCount());
                cursor.close();
            }
        }
        return sb.toString();
    }

    /**
     * 获取所有联系人
     *
     * @param context 上下文
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getAllContacts(Context context) {
        long currentTimeMillis = System.currentTimeMillis();

        contacts.clear();
        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, "sort_key");
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                Log.d(TAG, "获取所有联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis) + "，共计：" + cursor.getCount());
                cursor.close();
            }
        }
        return contacts;
    }


    /**
     * 通过姓名获取联系人
     *
     * @param context     上下文
     * @param contactName 联系人姓名
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContactByName(Context context, String contactName) {
        long currentTimeMillis = System.currentTimeMillis();
        contacts.clear();

        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " like ? ";
        String[] selectionArgs = new String[]{"%" + contactName + "%"};
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, selection, selectionArgs, "sort_key");
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "通过姓名获取联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return contacts;
    }

    /**
     * 通过手机号获取联系人
     *
     * @param context 上下文
     * @param number  手机号码
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContactByNumber(Context context, String number) {
        long currentTimeMillis = System.currentTimeMillis();
        contacts.clear();

        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        String selection = ContactsContract.CommonDataKinds.Phone.NUMBER + " like ? ";
        String[] selectionArgs = new String[]{"%" + number + "%"};
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, selection, selectionArgs, "sort_key");
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "通过手机号获取联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return contacts;
    }

    /**
     * 分页查询联系人
     *
     * @param context  上下文
     * @param page     页数
     * @param pageSize 每页数据量
     * @return 联系人集合
     */
    public List<PhoneBookEntity> getContactsByPage(Context context, int page, int pageSize) {
        long currentTimeMillis = System.currentTimeMillis();
        contacts.clear();

        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            String sortOrder = "_id  limit " + page + "," + pageSize;
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, sortOrder);
            if (cursor != null) {
                final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int mobileNoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String mobileNo, displayName;
                while (cursor.moveToNext()) {
                    mobileNo = cursor.getString(mobileNoIndex);
                    displayName = cursor.getString(displayNameIndex);
                    contacts.add(new PhoneBookEntity(displayName, mobileNo));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "分页查询联系人耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return contacts;
    }

    /**
     * 获取联系人总数
     *
     * @param context 上下文
     * @return 数量
     */
    public int getContactCount(Context context) {
        long currentTimeMillis = System.currentTimeMillis();
        Cursor cursor = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, "sort_key");
            if (cursor != null) {
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            Log.d(TAG, "获取联系人总数耗时: " + (System.currentTimeMillis() - currentTimeMillis));
        }
        return 0;
    }
}
