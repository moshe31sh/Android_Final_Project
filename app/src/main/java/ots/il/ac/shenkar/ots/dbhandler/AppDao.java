package ots.il.ac.shenkar.ots.dbhandler;

import android.content.Context;
import android.content.SharedPreferences;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;

/**
 * Created by moshe on 21-02-16.
 */
public class AppDao  implements IAppDao {

    private static  AppDao instance;
    private Context context;
    private String managerEmail;
    private ParseUser parseUser;

    private AppDao(Context context){
        this.context = context;

    }

    public static AppDao getInstance(Context context) {
        if (instance == null) {
            instance = new AppDao(context);
        }
        return instance;
    }

    @Override
    public void signUp(User user ,SignUpCallback signUpCallback) {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(user.getMail().trim());
        parseUser.setPassword(user.getPassword().trim());
        parseUser.setEmail(user.getMail().trim());
        parseUser.put("isManager", user.getIsManager());
        parseUser.put("Name", user.getUserName().trim());
        parseUser.put("LName", user.getUserLName().trim());
        parseUser.put("Email", user.getMail().trim());
        parseUser.put("Phone", user.getUserPhone().trim());
        parseUser.put("Manager", user.getManager().trim());
        parseUser.signUpInBackground(signUpCallback);
    }

    @Override
    public void login (String userName,String pass,LogInCallback callback){
        ParseUser.logInInBackground(userName, pass, callback);
    }



    @Override
    public void createTask(Task task, SaveCallback saveCallback) {
        ParseObject parseObject = new ParseObject("Task");
        parseObject.put("Title", task.getTitle().trim());
        parseObject.put("Content", task.getContent().trim());
        parseObject.put("Manager", task.getManager().trim());
        parseObject.put("Employee", task.getUser());
        parseObject.put("Time", task.getTime());
        parseObject.put("Priority", task.getPriority());
        parseObject.put("Status", task.getTaskStatus());
        parseObject.put("Category", task.getCategory());
        parseObject.put("Date", task.getDate());
        parseObject.put("FirstRead", task.isFirstRead());
        parseObject.put("FirstSync", task.getFirstSync());
        parseObject.saveInBackground(saveCallback);

    }

    @Override
    public void getAllUsers(String manager, FindCallback findCallback) {
        parseUser = ParseUser.getCurrentUser();
        managerEmail = parseUser.getEmail();
        ParseQuery query = ParseQuery.getQuery("_User");
        query.whereEqualTo("Manager", managerEmail);
        query.findInBackground(findCallback);
    }


    @Override
    public void getAllTask(String managerEmail ,FindCallback findCallback ) {
        ParseQuery query = ParseQuery.getQuery("Task");
        query.whereEqualTo("Manager", managerEmail);
        query.findInBackground(findCallback);
    }


    @Override
    public void getUserTask(String userEmail, FindCallback findCallback) {
        ParseQuery query = ParseQuery.getQuery("Task");
        query.whereEqualTo("Employee", userEmail);
        query.findInBackground(findCallback);
    }

    @Override
    public void updateTask(Task task, FindCallback findCallback) {
        ParseQuery query = ParseQuery.getQuery("Task");
        try {
            ParseObject object = query.get(task.getTaskId());
            object.put("Status", task.getTaskStatus());
            object.put("FirstRead", task.isFirstRead());
            object.put("Priority", task.getPriority());
            object.put("Status", task.getTaskStatus());
            object.put("Category", task.getCategory());
            object.put("FirstSync", task.getFirstSync());
            if (task.getDoneTaskPic() != null) {
                object.put("Image", task.getDoneTaskPic());
            }
            object.saveInBackground();
            query.findInBackground(findCallback);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(User user, DeleteCallback deleteCallback) {
        SharedPreferences prefs = context.getSharedPreferences(AppConst.SharedPrefsName, 0);
        final String pass  = prefs.getString(AppConst.SharedPrefs_UserPass, null);
        final  String managerName =  prefs.getString(AppConst.SharedPrefs_UserEmail, null);
        try {
            ParseUser.logIn(user.getMail() , user.getUserPhone());
            parseUser = ParseUser.getCurrentUser();
            parseUser.deleteInBackground(deleteCallback);
            ParseUser.logIn(managerName,pass);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteTask(String key , String factor, FindCallback findCallback) {
        ParseQuery query = ParseQuery.getQuery("Task");
        query.whereEqualTo(key, factor);
        query.findInBackground(findCallback);
    }


}

