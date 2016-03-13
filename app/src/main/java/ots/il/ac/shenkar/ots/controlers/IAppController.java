package ots.il.ac.shenkar.ots.controlers;

import android.content.Context;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;

/**
 * Created by moshe on 21-02-16.
 */
public interface IAppController {
    void signUp(User user , SignUpCallback signUpCallback);

    void login (String userName,String pass,LogInCallback callback);


    void createTask(Task task , SaveCallback saveCallback);

    void getAllUsers(String manager , FindCallback findCallback);

    void getUserTask(String userEmail, FindCallback findCallback);
    void addTaskToLocalDb(Task task);

    void getAllTask(String managerEmail ,FindCallback findCallback) ;

    void deleteUser (User user , DeleteCallback deleteCallback);

    List<Task> getAllTaskFromLocalDb();

    void dropTable();

    void updateTaskLocalDb(Task task);

    boolean doesDatabaseExist(Context context);

    void updateTask(Task task, FindCallback findCallback);

    void deleteTask (String key , String factor , FindCallback findCallback );

    void deleteTaskFromLocalDB(String taskId);

    void updateListOfTask (List<Task>tasks);


    List<String> getAllEmail() ;

    void addEmailToLocalDb(String email);

    void dropEmailTable();
}
