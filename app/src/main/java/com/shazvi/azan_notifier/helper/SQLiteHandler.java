//http://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/
//https://blog.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/

package com.shazvi.azan_notifier.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";
    private static String DB_NAME = "myDBName";
    private SQLiteDatabase myDataBase;
    private Context myContext;

    // All Static variables
    private static final int DATABASE_VERSION = 1; // Database Version
    private static final String DATABASE_NAME = "android_api"; // Database Name
    private static final String TABLE_USER = "user"; // Login table name

    // Login Table Columns names
    private static final String LOGIN_ID = "_id";
    private static final String LOGIN_EMAIL = "email";
    private static final String LOGIN_PASSWORD = "password";
    private static final String LOGIN_ACCESSTOKEN = "access_token";
    private static final String LOGIN_IS_ACTIVE = "is_active";
    private static final String LOGIN_IS_LOGGED_IN = "is_logged_in";
    private static final String LOGIN_CREATED_AT = "created_at";
    private static final String LOGIN_LAST_LOGGED_IN = "last_logged_in";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "(" +
            LOGIN_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE," +
            LOGIN_EMAIL + " TEXT NOT NULL UNIQUE," +
            LOGIN_PASSWORD + " TEXT," +
            LOGIN_ACCESSTOKEN + " TEXT," +
            LOGIN_IS_ACTIVE + " INTEGER," +
            LOGIN_IS_LOGGED_IN + " INTEGER," +
            LOGIN_CREATED_AT + " TEXT" + ")" +
            LOGIN_LAST_LOGGED_IN + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(JSONObject user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(LOGIN_ID, user.getString(LOGIN_ID));
            values.put(LOGIN_EMAIL, user.getString(LOGIN_EMAIL));
            values.put(LOGIN_PASSWORD, user.getString(LOGIN_PASSWORD));
            values.put(LOGIN_ACCESSTOKEN, user.getString(LOGIN_ACCESSTOKEN));
            values.put(LOGIN_IS_ACTIVE, user.getString(LOGIN_IS_ACTIVE));
            values.put(LOGIN_IS_LOGGED_IN, user.getString(LOGIN_IS_LOGGED_IN));
            values.put(LOGIN_CREATED_AT, user.getString(LOGIN_CREATED_AT));
            values.put(LOGIN_LAST_LOGGED_IN, user.getString(LOGIN_LAST_LOGGED_IN));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Storing user details in database
     * */
    public JSONObject getUser(String email) {
        JSONObject user = new JSONObject();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " where email = ";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            try {
                user.put("name", cursor.getString(1));
                user.put("email", cursor.getString(2));
                user.put("uid", cursor.getString(3));
                user.put("created_at", cursor.getString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user data from database
     * */
    // TODO
    public JSONObject getAllUsers() {
        JSONObject user = new JSONObject();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            try {
                user.put("name", cursor.getString(1));
                user.put("email", cursor.getString(2));
                user.put("uid", cursor.getString(3));
                user.put("created_at", cursor.getString(4));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() {
        boolean dbExist = checkDataBase();

        if (!dbExist) {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            copyDataBase();
        }
    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            //database does't exist yet.
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() {

        //Open your local db as the input stream
        InputStream myInput = null;
        try {
            myInput = myContext.getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DB_NAME;

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer))>0){
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}