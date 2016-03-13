package ots.il.ac.shenkar.ots.dbhandlerlocal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by moshe on 13-03-16.
 */
public class EmailDbHelper extends SQLiteOpenHelper {


    public EmailDbHelper(Context context) {
        super(context, DbContract.EMAIL_DATABASE_NAME, null, DbContract.EMAIL_DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DbContract.EmailEntry.SQL_CREATE_EMAIL_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.EmailEntry.EMAIL_TABLE_NAME);
        onCreate(db);
    }



}
