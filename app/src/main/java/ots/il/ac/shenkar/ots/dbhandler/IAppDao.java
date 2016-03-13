package ots.il.ac.shenkar.ots.dbhandler;

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
public interface IAppDao {

    void signUp(User user , SignUpCallback signUpCallback);

    void login (String userName,String pass,LogInCallback callback);

    void createTask(Task task , SaveCallback saveCallback);

    void getAllUsers(String manager , FindCallback findCallback);

    void getAllTask(String managerEmail , FindCallback findCallback) ;

    void getUserTask(String userEmail, FindCallback findCallback);

    void updateTask(Task task, FindCallback findCallback);

    void deleteUser(User user, DeleteCallback deleteCallback);

    void deleteTask (String key , String factor , FindCallback findCallback );

}
