package ots.il.ac.shenkar.ots.dbhandlerlocal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by moshe on 22-02-16.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database
    // version.






    public TaskDbHelper(Context context) {
        super(context, DbContract.DATABASE_NAME, null, DbContract.DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(DbContract.TaskEntry.SQL_CREATE_LOCATION_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DbContract.TaskEntry.TABLE_NAME);
        onCreate(db);
    }



}
