package ots.il.ac.shenkar.ots.dbhandlerlocal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ots.il.ac.shenkar.ots.common.Task;

/**
 * Created by moshe on 22-02-16.
 */
public class LocalAppDao implements ILocalAppDao {

    private static LocalAppDao instance;
    private Context context;
    private TaskDbHelper dbHelper;
    private EmailDbHelper emailDbHelper;

    private String[] taskColumns = {
            DbContract.TaskEntry.COLUMN_TITLE,DbContract.TaskEntry.COLUMN_TEXT,
            DbContract.TaskEntry.COLUMN_DATE,
            DbContract.TaskEntry.COLUMN_MANAGER,DbContract.TaskEntry.COLUMN_USER,
            DbContract.TaskEntry.COLUMN_TASK_STATUS,DbContract.TaskEntry.COLUMN_PRIORITY,
            DbContract.TaskEntry.COLUMN_CATEGORY,DbContract.TaskEntry.COLUMN_TIME,
            DbContract.TaskEntry.COLUMN_FIRST_READ, DbContract.TaskEntry.COLUMN_TASK_ID
            ,DbContract.TaskEntry.COLUMN_DONE_IMAGE
    };


    private String[] emailColumns = { DbContract.EmailEntry.EMAIL_COLUMN_EMAIL };



    private LocalAppDao(Context context) {
        this.context = context;
        this.dbHelper = new TaskDbHelper(this.context);
        this.emailDbHelper = new EmailDbHelper(this.context);
    }


    public static LocalAppDao getInstance(Context context){
        if (instance == null){
            instance = new LocalAppDao(context);
        }
        return instance;
    }


    /*
* Create item object from the cursor.
*/
    private Task cursorToItem(Cursor cursor) {
        Task task = new Task();
        task.setTitle(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_TITLE)));
        task.setText(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_TEXT)));
        task.setUser(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_USER)));
        task.setManager(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_MANAGER)));
        task.setTaskStatus(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_TASK_STATUS)));
        task.setPriority(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_PRIORITY)));
        task.setCategory(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_CATEGORY)));
        task.setDate(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_DATE)));
        task.setTime(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_TIME)));
        task.setFirstRead(cursor.getInt(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_FIRST_READ)));
        task.setTaskId(cursor.getString(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_TASK_ID)));
        task.setDoneTaskPic(cursor.getBlob(cursor.getColumnIndex(DbContract.TaskEntry.COLUMN_DONE_IMAGE)));



        return task;
    }


    /*
* Create item object from the cursor.
*/
    private String cursorToEmail(Cursor cursor) {

        return cursor.getString(cursor.getColumnIndex(DbContract.EmailEntry.EMAIL_COLUMN_EMAIL));
    }



    @Override
    public List<Task> getAllTask() {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getReadableDatabase();
            List<Task> tasks = new ArrayList<Task>();

            Cursor cursor = database.query(DbContract.TaskEntry.TABLE_NAME, taskColumns,
                    null, null, null, null, null,null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Task task = cursorToItem(cursor);
                tasks.add(task);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return tasks;
        } finally {
            if (database != null) {
                database.close();
            }
        }
    }

    @Override
    public Task addTaskToLocalDb(Task task){
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getReadableDatabase();
            if (task == null)
                return null;
            //build the content values.
            ContentValues values = new ContentValues();
            values.put(DbContract.TaskEntry.COLUMN_TITLE, task.getTitle());
            values.put( DbContract.TaskEntry.COLUMN_TEXT, task.getText());
            values.put(DbContract.TaskEntry.COLUMN_USER, task.getUser());
            values.put(DbContract.TaskEntry.COLUMN_MANAGER, task.getManager());
            values.put(DbContract.TaskEntry.COLUMN_TASK_STATUS, task.getTaskStatus());
            values.put(DbContract.TaskEntry.COLUMN_PRIORITY, task.getPriority());
            values.put(DbContract.TaskEntry.COLUMN_CATEGORY, task.getCategory());
            values.put(DbContract.TaskEntry.COLUMN_TIME, task.getTime());
            values.put(DbContract.TaskEntry.COLUMN_DATE, task.getDate());
            values.put(DbContract.TaskEntry.COLUMN_FIRST_READ, task.isFirstRead());
            values.put(DbContract.TaskEntry.COLUMN_TASK_ID, task.getTaskId());
            values.put(DbContract.TaskEntry.COLUMN_DONE_IMAGE, task.getDoneTaskPic());



            //do the insert.
            long insertId = database.insert(DbContract.TaskEntry.TABLE_NAME, null, values);

            //get the entity from the data base - extra validation, entity was insert properly.
            Cursor cursor = database.query(DbContract.TaskEntry.TABLE_NAME, taskColumns,
                    null, null, null, null, null,null);
            cursor.moveToFirst();
            //create the friend object from the cursor.
            Task newTask = cursorToItem(cursor);
            cursor.close();
            return newTask;
        }
        finally {
            if (database != null)
                database.close();
        }
    }


    @Override
    public void updateTaskLocalDb(Task task) {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getReadableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(DbContract.TaskEntry.COLUMN_TASK_STATUS,task.getTaskStatus());
            cv.put(DbContract.TaskEntry.COLUMN_FIRST_READ,task.isFirstRead());
            cv.put(DbContract.TaskEntry.COLUMN_CATEGORY,task.getCategory());
            cv.put(DbContract.TaskEntry.COLUMN_PRIORITY,task.getPriority());
            cv.put(DbContract.TaskEntry.COLUMN_DONE_IMAGE,task.getDoneTaskPic());
            database.update(DbContract.TaskEntry.TABLE_NAME, cv, DbContract.TaskEntry.COLUMN_TASK_ID
                    +" = "+ "'"+task.getTaskId()+"'", null);
        }finally {
            if(database != null){
                database.close();
            }
        }
    }

    @Override
    public void dropTable(){
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        database.delete(DbContract.TaskEntry.TABLE_NAME,null,null);


    }

    @Override
    public void removeTask(String taskId) {
        SQLiteDatabase database = null;
        try {
            database = dbHelper.getReadableDatabase();
            database.delete(DbContract.TaskEntry.TABLE_NAME, DbContract.TaskEntry.COLUMN_TASK_ID +
                    " = ?",new String[]{taskId});
        }finally {
            if(database != null){
                database.close();
            }
        }
    }


    @Override
    public boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath(DbContract.DATABASE_NAME);
        return dbFile.exists();
    }




    @Override
    public List<String> getAllEmail() {
        SQLiteDatabase database = null;
        try {
            database = emailDbHelper.getReadableDatabase();
            List<String> emails = new ArrayList<>();

            Cursor cursor = database.query(DbContract.EmailEntry.EMAIL_TABLE_NAME, emailColumns,
                    null, null, null, null, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String email = cursorToEmail(cursor);
                emails.add(email);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
            return emails;
        } finally {
            if (database != null) {
                database.close();
            }
        }    }




    @Override
    public String addEmailToLocalDb(String email) {
        SQLiteDatabase database = null;
        try {
            database = emailDbHelper.getReadableDatabase();
            if (email == null)
                return null;
            //build the content values.
            ContentValues values = new ContentValues();
            values.put(DbContract.EmailEntry.EMAIL_COLUMN_EMAIL, email);
//do the insert.
            long insertId = database.insert(DbContract.EmailEntry.EMAIL_TABLE_NAME, null, values);
            //get the entity from the data base - extra validation, entity was insert properly.
            Cursor cursor = database.query(DbContract.EmailEntry.EMAIL_TABLE_NAME, emailColumns,
                    null, null, null, null, null);
            cursor.moveToFirst();
            //create the friend object from the cursor.
            String newEmail = cursorToEmail(cursor);
            cursor.close();
            return newEmail;
        }
        finally {
            if (database != null)
                database.close();
        }
    }


    @Override
    public void dropEmailTable(){
        SQLiteDatabase database = emailDbHelper.getReadableDatabase();
        database.delete(DbContract.EmailEntry.EMAIL_TABLE_NAME,null,null);


    }
}
