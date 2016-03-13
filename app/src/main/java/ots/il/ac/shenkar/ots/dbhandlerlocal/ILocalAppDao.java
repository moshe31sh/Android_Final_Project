package ots.il.ac.shenkar.ots.dbhandlerlocal;

import android.content.Context;

import java.util.List;

import ots.il.ac.shenkar.ots.common.Task;

/**
 * Created by moshe on 22-02-16.
 */
public interface ILocalAppDao {
    List<Task> getAllTask() ;

    Task addTaskToLocalDb(Task task);

    void updateTaskLocalDb(Task task);

    void dropTable();

    void removeTask(String taskId);

    boolean doesDatabaseExist(Context context);



    List<String> getAllEmail() ;

    String addEmailToLocalDb(String email);

    void dropEmailTable();

}
