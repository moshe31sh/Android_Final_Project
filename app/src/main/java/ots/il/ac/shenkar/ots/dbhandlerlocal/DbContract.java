package ots.il.ac.shenkar.ots.dbhandlerlocal;

import android.provider.BaseColumns;

/**
 * Created by moshe on 22-02-16.
 */
public class DbContract {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "tasks.db";


    public static final int EMAIL_DATABASE_VERSION = 1;
    public static final String EMAIL_DATABASE_NAME = "email.db";


    /* Inner class that defines the table contents of the items */
    public static final class TaskEntry implements BaseColumns {


        // Table name
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_MANAGER = "manager";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_TASK_STATUS = "taskStatus";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_TASK_ID = "taskId";
        public static final String COLUMN_FIRST_READ = "firstRead";
        public static final String COLUMN_DONE_IMAGE = "doneImage";





        final static String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "
                + TABLE_NAME +
                " ("
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_TEXT + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_MANAGER + " TEXT, "
                + COLUMN_USER + " TEXT, "
                + COLUMN_TASK_STATUS + " TEXT, "
                + COLUMN_PRIORITY + " TEXT, "
                + COLUMN_CATEGORY + " TEXT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_FIRST_READ + " INTEGER, "
                + COLUMN_DONE_IMAGE + " TEXT, "
                + COLUMN_TASK_ID + " TEXT);";

    }










    public static final class EmailEntry implements BaseColumns {
        //user email db
        public static final String EMAIL_TABLE_NAME = "emails";
        public static final String EMAIL_COLUMN_EMAIL = "email";

        final static String SQL_CREATE_EMAIL_TABLE = "CREATE TABLE "
                + EMAIL_TABLE_NAME +
                " ("
                + EMAIL_COLUMN_EMAIL + " TEXT);";

    }

}