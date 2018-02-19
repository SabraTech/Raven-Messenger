package com.example.space.chatapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.space.chatapp.models.Friend;
import com.example.space.chatapp.models.FriendList;

public class FriendDB {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " ("
                    + FeedEntry.COLUMN_NAME_ID + " TEXT PRIMARY KEY,"
                    + FeedEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP
                    + FeedEntry.COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP
                    + FeedEntry.COLUMN_NAME_ID_ROOM + TEXT_TYPE + COMMA_SEP
                    + FeedEntry.COLUMN_NAME_AVATAR + TEXT_TYPE + " )";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;

    private static FriendDBHelper dbHelper = null;
    private static FriendDB instance = null;

    private FriendDB() {

    }

    public static FriendDB getInstance(Context context) {
        if (instance == null) {
            instance = new FriendDB();
            dbHelper = new FriendDBHelper(context);
        }
        return instance;
    }

    public long addFriend(Friend friend) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // create the new map of values where column name are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_ID, friend.id);
        values.put(FeedEntry.COLUMN_NAME_NAME, friend.getName());
        values.put(FeedEntry.COLUMN_NAME_EMAIL, friend.getEmail());
        values.put(FeedEntry.COLUMN_NAME_ID_ROOM, friend.idRoom);
        values.put(FeedEntry.COLUMN_NAME_AVATAR, friend.getAvatar());

        // insert the new row and return the primary key of the new row
        return db.insert(FeedEntry.TABLE_NAME, null, values);
    }

    public void addFriendList(FriendList friendList) {
        for (Friend friend : friendList.getFriendsList()) {
            addFriend(friend);
        }
    }

    public FriendList getFriendList() {
        FriendList friendList = new FriendList();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + FeedEntry.TABLE_NAME, null);
            while (cursor.moveToNext()) {
                Friend friend = new Friend();
                friend.id = cursor.getString(0);
                friend.setName(cursor.getString(1));
                friend.setEmail(cursor.getString(2));
                friend.idRoom = cursor.getString(3);
                friend.setAvatar(cursor.getString(4));
                friendList.getFriendsList().add(friend);
            }
            cursor.close();
        } catch (Exception e) {
            return new FriendList();
        }
        return friendList;
    }

    public void dropDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public static class FeedEntry implements BaseColumns {
        static final String TABLE_NAME = "friend";
        static final String COLUMN_NAME_ID = "friendID";
        static final String COLUMN_NAME_NAME = "name";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_ID_ROOM = "idRoom";
        static final String COLUMN_NAME_AVATAR = "avatar";

    }

    private static class FriendDBHelper extends SQLiteOpenHelper {

        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "FriendChat.db";

        FriendDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
            onCreate(sqLiteDatabase);
        }

        @Override
        public void onDowngrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            onUpgrade(sqLiteDatabase, i, i1);
        }


    }

}
