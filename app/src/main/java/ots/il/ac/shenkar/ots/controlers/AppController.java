package ots.il.ac.shenkar.ots.controlers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Log;

import java.util.ArrayList;
import java.util.List;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.dbhandler.AppDao;
import ots.il.ac.shenkar.ots.dbhandler.IAppDao;
import ots.il.ac.shenkar.ots.dbhandlerlocal.ILocalAppDao;
import ots.il.ac.shenkar.ots.dbhandlerlocal.LocalAppDao;

/**
 * Created by moshe on 21-02-16.
 */
public class AppController implements IAppController {
    private Context context;
    private IAppDao dao;
    private ILocalAppDao localAppDao;
    private final int SECOND = 60;
    private int time;
    private String timeStr;
    private Task currentTask;

    public AppController(Context context) {
        dao = AppDao.getInstance(context.getApplicationContext());
        localAppDao = LocalAppDao.getInstance(context.getApplicationContext());
        this.context = context;
    }

    /**
     * check if user login flag is true
     *
     * @return
     */


    public void setLogedIn(User user) {
        if (user != null) {
            SharedPreferences prefs = context.getSharedPreferences(AppConst.SharedPrefsName, 0);
            if (prefs != null) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(AppConst.SharedPrefs_IsLogin, true);
                editor.putString(AppConst.SharedPrefs_UserName, user.getUserName());
                editor.putString(AppConst.SharedPrefs_ManagerEmail, user.getMail());
                editor.putString(AppConst.SharedPrefs_UserEmail, user.getMail());
                editor.putString(AppConst.SharedPrefs_UserPass, user.getPassword());
                editor.putString(AppConst.SharedPrefs_UserFullName, user.getUserName() + " " + user.getUserLName());
                editor.putString(AppConst.SharedPrefs_UserPass, user.getPassword());
                editor.putBoolean(AppConst.SharedPrefs_isManager, user.getIsManager());
                editor.putInt(AppConst.SharedPrefs_refreshInterval, 5 * SECOND);
                editor.commit();
            }
        }
    }

    public void setDefaultRefreshInterval() {
        setDefaultRefreshInterval(5);
        AppUtils.Toast(context, AppConst.TIME_SET_DEFAULT);

    }

    public void setDefaultRefreshInterval(int timeSet) {
        SharedPreferences prefs = context.getSharedPreferences(AppConst.SharedPrefsName, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(AppConst.SharedPrefs_refreshInterval, timeSet * SECOND);
        editor.commit();
    }


    public void setManualRefreshInterval() {
        Spinner spinner;

        ArrayAdapter<CharSequence> adapter;
        Button mBtnSet, mBtnCancel;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View dialogView = dialog.getLayoutInflater().inflate(R.layout.refresh_picker, null);
        spinner = (Spinner) dialogView.findViewById(R.id.id_refresh_spinner);
        mBtnSet = (Button) dialogView.findViewById(R.id.id_refresh_set_button);
        mBtnCancel = (Button) dialogView.findViewById(R.id.id_refresh_cancel_button);
        adapter = ArrayAdapter.createFromResource(context, R.array.refresh, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    time = 15 * SECOND;
                    timeStr = "15 minutes";
                }
                if (position == 1) {
                    time = 30 * SECOND;
                    timeStr = "30 minutes";
                }
                if (position == 2) {
                    time = 60 * SECOND;
                    timeStr = "1 hour";
                }
                if (position == 3) {
                    time = 720 * SECOND;
                    timeStr = "12 hour";
                }
                if (position == 4) {
                    time = 1440 * SECOND;
                    timeStr = "24 hour";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                AppUtils.Toast(context, AppConst.CANCEL);
            }
        });

        mBtnSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultRefreshInterval(time);
                AppUtils.Toast(context, "Set refresh time to " + timeStr);
                dialog.dismiss();
            }
        });
        dialog.setView(dialogView);

        dialog.show();
    }

    public void clearSharedPreferences() {
        SharedPreferences prefs = context.getSharedPreferences(AppConst.SharedPrefsName, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void signUp(User user, SignUpCallback signUpCallback) {
        dao.signUp(user, signUpCallback);
    }

    @Override
    public void login(String userName, String pass, LogInCallback callback) {
        dao.login(userName, pass, callback);
    }


    @Override
    public void createTask(Task task, SaveCallback saveCallback) {
        dao.createTask(task, saveCallback);
    }


    @Override
    public void getAllUsers(String manager, FindCallback findCallback) {
        dao.getAllUsers(manager, findCallback);
    }

    @Override
    public void getUserTask(String userEmail, FindCallback findCallback) {
        dao.getUserTask(userEmail, findCallback);
    }


    @Override
    public void updateTask(Task task, FindCallback findCallback) {
        dao.updateTask(task, findCallback);
    }

    @Override
    public void deleteUser(User user, DeleteCallback deleteCallback) {
        dao.deleteUser(user, deleteCallback);
    }

    @Override
    public void addTaskToLocalDb(Task task) {
        Task retTask = localAppDao.addTaskToLocalDb(task);
        if (retTask == null) return;

    }


    @Override
    public void getAllTask(String managerEmail, FindCallback findCallback) {
        dao.getAllTask(managerEmail, findCallback);
    }

    @Override
    public List<Task> getAllTaskFromLocalDb() {
        ParseUser parseUser = ParseUser.getCurrentUser();
        List<Task> taskList = localAppDao.getAllTask();

        if (!parseUser.getBoolean("isManager")) {
            List<Task> tempList = new ArrayList<>();
            for (Task task : taskList) {
                if (!task.getTaskStatus().equals(AppConst.REJECT)) {
                    tempList.add(task);
                }
            }
            taskList = tempList;
        }
        if (taskList == null) {
            taskList = new ArrayList<>();
        }
        return taskList;
    }


    @Override
    public void dropTable() {
        localAppDao.dropTable();
    }

    @Override
    public void updateTaskLocalDb(Task task) {
        localAppDao.updateTaskLocalDb(task);
    }

    @Override
    public boolean doesDatabaseExist(Context context) {
        return localAppDao.doesDatabaseExist(context);
    }

    public void updateLocalDB(List<Task> taskFromParseList) {
        dropTable();
        for (Task taskOb : taskFromParseList) {
            addTaskToLocalDb(taskOb);
        }
    }


    public List<Task> getTasksListByStatus(String status) {
        List<Task> allTask = getAllTaskFromLocalDb();
        List<Task> retList = new ArrayList<>();
        for (Task task : allTask) {
            if ((task.getTaskStatus().equals(status))) {
                retList.add(task);
            }
        }
        return retList;
    }


    @Override
    public void deleteTask(String key , String factor, FindCallback findCallback) {
        dao.deleteTask(key, factor, findCallback);
    }


    public void notifyAllOnChanges() {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context);
        Intent i = new Intent("TAG_REFRESH");
        lbm.sendBroadcast(i);
    }

    @Override
    public void deleteTaskFromLocalDB(String taskId) {
        localAppDao.removeTask(taskId);
    }


    public void notifyNewTask(int newTask) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("You have " + newTask + " new task(s)");
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    @Override
    public void updateListOfTask(List<Task> tasks) {
        if (tasks != null) {
            for (Task task : tasks) {
                updateTask(task, new FindCallback() {
                    @Override
                    public void done(List objects, ParseException e) {

                    }

                    @Override
                    public void done(Object o, Throwable throwable) {

                    }
                });
            }
        }
    }

    public void signToNotification(String user) {
        try {
            App42API.initialize(
                    context,
                    "ec1d5b8dc53f5685c67c5ff1c600a9304f4ee22090c121c5252a69e546aac0ce",
                    "104948a90c4d06fe60fcc143bc0a38c39bdb10977fa17b288ef9156963f8c2fc");
            App42Log.setDebug(true);
            App42API.setLoggedInUser(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void deleteTasksDialog(final List<Task> taskList) {
        final ProgressDialog mProgress = new ProgressDialog(context);
        mProgress.setMessage("Delete task(s)...");
        mProgress.show();
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure to delete " + taskList.size() + " records?");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        for (int i = 0; i < taskList.size(); i++) {
                            deleteTaskFromLocalDB(taskList.get(i).getTaskId());
                            deleteTask("objectId", taskList.get(i).getTaskId(), new FindCallback() {
                                @Override
                                public void done(List objects, ParseException e) {

                                }

                                @Override
                                public void done(Object o, Throwable throwable) {
                                    if (throwable == null) {
                                        ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;
                                        parseObjectArrayList.get(0).deleteInBackground();
                                        mProgress.dismiss();
                                        AppUtils.Toast(context, AppConst.DELETE_TASK);

                                    } else {
                                        AppUtils.Toast(context, AppConst.CONNECTION_ERROR);
                                    }
                                }
                            });
                            notifyAllOnChanges();
                        }

                        dialog.dismiss();

                    }

                }

        );


        alert.setNegativeButton("NO", new DialogInterface.OnClickListener()

                {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgress.dismiss();
                        dialog.dismiss();
                        notifyAllOnChanges();

                    }
                }

        );

        alert.show();
    }


    @Override
    public List<String> getAllEmail() {
        List<String> emails = localAppDao.getAllEmail();
        if (emails == null){
            emails = new ArrayList<>();
        }
        return  emails;
    }

    @Override
    public void addEmailToLocalDb(String email) {
      String retEmail = localAppDao.addEmailToLocalDb(email);
    }


    @Override
    public void dropEmailTable() {
        localAppDao.dropEmailTable();
    }


    public void updateLocalEmailDB(List<String> emails) {
        dropTable();
        for (String email : emails) {
            addEmailToLocalDb (email);
        }
    }
}