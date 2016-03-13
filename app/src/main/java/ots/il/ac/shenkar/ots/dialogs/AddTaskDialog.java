package ots.il.ac.shenkar.ots.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AppController;
import ots.il.ac.shenkar.ots.listeners.IGetCreatedTask;

/**
 * Created by moshe on 21-02-16.
 */
public class AddTaskDialog extends DialogFragment implements IGetCreatedTask
{


    private EditText mTitle , mContent ;
    private TextView mUsers ,mTaskTime , mTaskDate;
    private ImageButton mIBtnDate, mIBtnTime, mBtnUsers;
    private Button mBtnCreate , mBtnCancel;
    private Spinner mCategorySpinner , mPrioritySpinner;
    private ArrayAdapter mCategoryAdapter , mPriorityAdapter;
    private ProgressDialog mProgress;
    private String mTeamName , mManager;
    private List<User> userList;
    private List<Task> taskList;
    private AppController mController;
    private Task retTask;
    private List<String> userEmails;
    private StringBuilder stringBuilder;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private CheckBox mToday , mTomorrow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.task_create_form_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        retTask = new Task();
        userEmails = new ArrayList<>();
        taskList = new ArrayList<>();
        stringBuilder = new StringBuilder();
        mController = new AppController(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences(AppConst.SharedPrefsName, 0);
        mManager = prefs.getString(AppConst.SharedPrefs_ManagerEmail, null);
        mTeamName = prefs.getString(AppConst.SharedPrefs_TeamId, null);
        mTitle = (EditText) view.findViewById(R.id.id_task_title);
        mUsers = (TextView)view.findViewById(R.id.id_task_to_users);
        mContent = (EditText) view.findViewById(R.id.id_task_content);
        mBtnCancel = (Button) view.findViewById(R.id.id_add_member_cancel_button);
        mBtnCreate = (Button) view.findViewById(R.id.id_add_member_create_button);
        mCategorySpinner = (Spinner) view.findViewById(R.id.Category_array);
        mPrioritySpinner = (Spinner) view.findViewById(R.id.Priority_array);
        mIBtnDate = (ImageButton) view.findViewById(R.id.btnChangeDate);
        mIBtnTime = (ImageButton) view.findViewById(R.id.btnChangeTime);
        mBtnUsers = (ImageButton) view.findViewById(R.id.btnAddUsers);
        mTaskTime = (TextView) view.findViewById(R.id.id_task_time);
        mTaskDate = (TextView)view.findViewById(R.id.id_task_date);
        mToday = (CheckBox) view.findViewById(R.id.id_today_checkbox);
        mTomorrow = (CheckBox) view.findViewById(R.id.id_tomorrow_checkbox);
        mCategoryAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.category, R.layout.spinner_layout);
        mPriorityAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.priority, R.layout.spinner_layout);
        mCategorySpinner.setAdapter(mCategoryAdapter);
        mPrioritySpinner.setAdapter(mPriorityAdapter);

        final Calendar calendar = Calendar.getInstance();
        final Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        final Date tomorrow = calendar.getTime();
        final java.text.DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");



        //default time
        retTask.setTime("12" + ":" + "00" + " " + "PM");
        mTaskTime.setText(retTask.getTime());

        mTomorrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTomorrow.isChecked()) {
                    mToday.setChecked(false);
                    String tomorrowAsString = dateFormat.format(tomorrow);
                    mTaskDate.setText(tomorrowAsString);
                    retTask.setDate(tomorrowAsString);
                }
            }
        });

        mToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mToday.isChecked()){

                    mTomorrow.setChecked(false);
                    String tomorrowAsString = dateFormat.format(today);
                    mTaskDate.setText(tomorrowAsString);
                    retTask.setDate(tomorrowAsString);

                }
            }
        });


        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                retTask.setCategory(textView.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mPrioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                retTask.setPriority(textView.getText().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mIBtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Process to get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                // Launch Date Picker Dialog
                DatePickerDialog dpd = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Display Selected date in textbox
                                mTaskDate.setText(dayOfMonth + "-"
                                        + (monthOfYear + 1) + "-" + year);
                                retTask.setDate(mTaskDate.getText().toString());
                                mTomorrow.setChecked(false);
                                mToday.setChecked(false);
                            }
                        }, mYear, mMonth, mDay);
                dpd.show();

            }
        });

        mIBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                final TimePickerDialog tpd = new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                if (view.is24HourView() == true){
                                    retTask.setTime(hourOfDay+":"+minute+" "+"AM");
                                }else{
                                    retTask.setTime(hourOfDay+":"+minute+" "+"PM");
                                }

                                mTaskTime.setText(retTask.getTime());
                            }
                        }, mHour, mMinute, false);
                tpd.show();
            }
        });


        mBtnUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllUserFromDb();

            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtils.Toast(getActivity() , AppConst.NO_TASK);
                getDialog().cancel();
            }
        });

        getDialog().setTitle("Add new task");
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createTask();

                IGetCreatedTask createdTask = (IGetCreatedTask) getActivity();
                createdTask.updateTaskResult(taskList);
                AppUtils.Toast(getActivity(), AppConst.CREATE_TASK);

                getDialog().dismiss();

            }
        });

    }
    public void createTask(){
        if (mTitle.getText().toString().trim().equals("")) {
            mTitle.setError(AppConst.EMPTY_TITLE);
        } else if (mContent.getText().toString().trim().equals("")) {
            mContent.setError(AppConst.EMPTY_CONTENT);
        }else{
            retTask.setTitle(mTitle.getText().toString().trim());
            retTask.setContent(mContent.getText().toString().toString());
            for (String user : userEmails ){
                Task temp = new Task();
                temp.setTitle(retTask.getTitle());
                temp.setContent(retTask.getContent());
                temp.setTaskStatus("Waiting");
                temp.setPriority(retTask.getPriority());
                temp.setCategory(retTask.getCategory());
                temp.setTime(retTask.getTime());
                temp.setDate(retTask.getDate());
                temp.setUser(user);
                taskList.add(temp);
            }
        }

    }


    @Override
    public void updateTaskResult(List<Task> tasks) {

    }


    public void getAllUserFromDb() {
        mProgress = new ProgressDialog(getActivity());
        mProgress.setMessage("Getting users data...");
        mProgress.show();
        mController.getAllUsers( mManager, new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {

            }

            @Override
            public void done(Object o, Throwable throwable) {
                ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;
                userList = AppUtils.getUser(parseObjectArrayList);
                final AlertDialog.Builder attachTask = new AlertDialog.Builder(getActivity());
                attachTask.setTitle("Select users to attach");
                attachTask.setMultiChoiceItems(AppUtils.createUserNamesList(userList), null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                                if (isChecked == true) {

                                    userEmails.add(userList.get(i).getMail());

                                } else {
                                    userEmails.remove(userList.get(i).getMail());
                                }
                            }
                        });
                attachTask.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (String email : userEmails) {
                            stringBuilder.append(email + ",");

                        }
                        mUsers.setText(stringBuilder);
                    }
                });
                mProgress.dismiss();
                final Dialog dialog = attachTask.create();
                dialog.show();
                dialog.onSaveInstanceState().get("Time");
            }
        });
    }


}
