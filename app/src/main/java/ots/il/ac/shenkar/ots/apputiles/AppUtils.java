package ots.il.ac.shenkar.ots.apputiles;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;

/**
 * Created by moshe on 21-02-16.
 */
public abstract class AppUtils {

    public static void Toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * this method receive parse user and create manager or employee
     *
     * @param parseUser
     * @return
     */
    public static User createUserFromPars(ParseUser parseUser) {
        User retUser = new User();
        if (parseUser.getBoolean("isManager") == false) {
            retUser.setManager(parseUser.getString("Manager"));
        }
        retUser.setUserName(parseUser.getString("Name"));
        retUser.setUserLName(parseUser.getString("LName"));
        retUser.setMail(parseUser.getString("Email"));
        retUser.setPassword(parseUser.getString("Pass"));
        retUser.setIsManager(parseUser.getBoolean("isManager"));
        retUser.setUserPhone(parseUser.getString("Phone"));
        retUser.setUserId(parseUser.getObjectId());
        return retUser;
    }


    /**
     * this method create user email list
     * @return
     */
    public static String [] getUserEmails(List<User> userList){

        String[] employeesEmails = new String[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            employeesEmails[i] = userList.get(i).getMail();
        }

        return employeesEmails;
    }

    /**
     * this method send to all invited employees join invitation
     */
    public static void sendTeamInvitation(String[] employeeEmails, Activity activity) {
        if (employeeEmails != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_BCC, employeeEmails);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Invitation to Join OTS team");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "\tHi\n" +
                    "\tYou have been invited to be a team member in an OTS Team created by me.\n" +
                    "\tUse this link to download and install the App from Google Play.\n" +
                    "\t<LINK to Google Play download>");
            emailIntent.setType("message/rfc822");
            activity.startActivity(Intent.createChooser(emailIntent, "Choose email app..."));
        } else {
            AppUtils.Toast(activity, AppConst.EMAIL_ERROR);
        }
    }


    /**
     * This method receive parse user object and parse it to user list
     * @param list
     * @return
     */
    public static ArrayList<User> getUser(ArrayList<ParseObject> list) {
        ArrayList<User> retUsers = new ArrayList<>();
        for (ParseObject object : list) {
            User retUser = new User(object.getString("Name"), object.getString("LName"),
                    object.getString("Pass"), object.getString("Email") , object.getString("Phone"));
            retUser.setIsManager(object.getBoolean("isManager"));
            retUser.setManager(object.getString("Manager"));
            retUser.setUserId(object.getObjectId());
            retUsers.add(retUser);

        }
        return retUsers;
    }


    /**
     * this method create user name list
     *
     * @param users
     * @return
     */
    public static String[] createUserNamesList(List<User> users) {
        String[] listOfUserName = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            listOfUserName[i] = users.get(i).getUserName() + " " + users.get(i).getUserLName();
        }
        return listOfUserName;
    }

    /**
     * This method is the sorting controller
     * @param taskList
     * @param sortFactor
     * @return
     */
    public static List<Task> sortList(List<Task> taskList, String sortFactor) {
        switch (sortFactor) {

            case "Priority":
                return sortByPriority(taskList);
            case "Time":
                return sortByTime(taskList);

            case "Waiting":
                return sortByWaiting(taskList, sortFactor);
            case "In process":
                return sortByWaiting(taskList , sortFactor);
            case "Done":
                return sortByWaiting(taskList , sortFactor);

        }

        return null;
    }
    /**
     * This method receive tasks list and sort it by priority factor
     * @param taskList
     * @return
     */
    public static List<Task> sortByPriority(List<Task> taskList) {
        List<Task> sortList;
        List<Task> high = new ArrayList<>();
        List<Task> normal = new ArrayList<>();
        List<Task> low = new ArrayList<>();

        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getPriority().equals("Urgent")) high.add(taskList.get(i));
            else if (taskList.get(i).getPriority().equals("Normal")) normal.add(taskList.get(i));
            else low.add(taskList.get(i));
        }
        sortList = high;
        for (int i = 0; i < normal.size(); i++) {
            sortList.add(normal.get(i));
        }
        for (int i = 0; i < low.size(); i++) {
            sortList.add(low.get(i));
        }
        return sortList;
    }


    /**
     * This method receive tasks list and sort it by time factor
     * @param taskList
     * @return
     */
    public static List<Task> sortByTime(List<Task> taskList) {
        Collections.sort(taskList, new Comparator<Task>() {
            @Override
            public int compare(Task lhs, Task rhs) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date1 = sdf.parse(lhs.getDate());
                    Date date2 = sdf.parse(rhs.getDate());
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return taskList;
    }

    /**
     * This method sorting lis by waiting factor
     * @param taskList
     * @param sortFactor
     * @return sorted list
     */
    public static List<Task> sortByWaiting(List<Task> taskList , String sortFactor) {
        List<Task> waiting = new ArrayList<>();
        List<Task> inProcess = new ArrayList<>();
        List<Task> done = new ArrayList<>();
        List<Task> reject = new ArrayList<>();
        for (int i = 0 ; i < taskList.size() ;i++ ){
            if (taskList.get(i).getTaskStatus().equals("Waiting")) waiting.add(taskList.get(i));
            if (taskList.get(i).getTaskStatus().equals("In process")) inProcess.add(taskList.get(i));
            if (taskList.get(i).getTaskStatus().equals("Done")) done.add(taskList.get(i));
            if (taskList.get(i).getTaskStatus().equals("Reject")) reject.add(taskList.get(i));
        }
        taskList.clear();
        if (sortFactor.equals("Waiting")) {
            taskList = waiting;
            for (int i = 0; i < inProcess.size(); i++) {
                taskList.add(inProcess.get(i));
            }
            for (int i = 0 ; i < done.size() ;i++ ){
                taskList.add(done.get(i));
            }
        }else if(sortFactor.equals("In process")){
            taskList = inProcess;
            for (int i = 0; i < waiting.size(); i++) {
                taskList.add(waiting.get(i));
            }
            for (int i = 0 ; i < done.size() ;i++ ){
                taskList.add(done.get(i));
            }
        }else if(sortFactor.equals("Done")){
            taskList = done;
            for (int i = 0; i < waiting.size(); i++) {
                taskList.add(waiting.get(i));
            }
            for (int i = 0 ; i < inProcess.size() ;i++ ){
                taskList.add(inProcess.get(i));
            }

        }

        for (int i = 0 ; i < reject.size() ;i++ ){
            taskList.add(reject.get(i));
        }
        return taskList;
    }

    /**
     * This method receive parse objects list and parsing it to task list
     * @param list
     * @return task list
     */
    public static ArrayList<Task> createTasksFromDB(ArrayList<ParseObject> list) {
        ArrayList<Task> taskList = new ArrayList<>();
        Task task;
        if (list != null) {
            for (ParseObject object : list) {
                task = new Task();
                task.setTitle(object.getString("Title"));
                task.setContent(object.getString("Content"));
                task.setManager(object.getString("Manager"));
                task.setUser(object.getString("Employee"));
                task.setTaskStatus(object.getString("Status"));
                task.setCategory(object.getString("Category"));
                task.setTime(object.getString("Time"));
                task.setPriority(object.getString("Priority"));
                task.setDate(object.getString("Date"));
                task.setTaskId(object.getObjectId());
                task.setFirstRead(object.getInt("FirstRead"));
                task.setFirstSync(object.getInt("FirstSync"));
                task.setDoneTaskPic(object.getBytes("Image"));
                taskList.add(task);
            }

        }
        return taskList;
    }

    /**
     * this method convert bitmap to byts
     * @param imageBitmap
     * @return
     */
    public static byte[] createBytesFromImage(Bitmap imageBitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    /**
     * this method convert bytes to bitmap
     * @param imageBytes
     * @return
     */
    public static Bitmap createImageFromBytes(byte[] imageBytes){
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }


    /**
     * this method validate email address
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }


}
