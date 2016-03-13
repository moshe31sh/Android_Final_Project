package ots.il.ac.shenkar.ots.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.shephertz.app42.paas.sdk.android.App42API;

import java.util.ArrayList;
import java.util.List;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AnalyticsApplication;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AppController;
import ots.il.ac.shenkar.ots.controlers.EmployeePagerAdapter;
import ots.il.ac.shenkar.ots.gcm_plugin.App42GCMController;
import ots.il.ac.shenkar.ots.gcm_plugin.App42GCMService;
import ots.il.ac.shenkar.ots.services.TimeService;

/**
 * Created by moshe on 22-02-16.
 */
public class EmployeeTaskControlActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, App42GCMController.App42GCMListener
{
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private String [] tabsList = {"Waiting" , "In process" ,"Done","All"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private EmployeePagerAdapter adapter;
    private FloatingActionButton fab;
    private AppController mController;
    private BroadcastReceiver mMessageReceiver;
    private User employee;
    private ProgressDialog mProgress;
    private List<Task> taskList;
    private android.app.AlertDialog sortDialog;
    private TextView mTvUserName;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_task_control_layout);
        startService(new Intent(this, TimeService.class));
        ParseUser user = ParseUser.getCurrentUser();
        employee = AppUtils.createUserFromPars(user);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        init();
    }


    public void init(){


        toolbar = (Toolbar) findViewById(R.id.id_employee_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(AppConst.APP_NAME);

        drawer = (DrawerLayout) findViewById(R.id.employee_drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mProgress = new ProgressDialog(this);
        mController = new AppController(this);

        navigationView = (NavigationView) findViewById(R.id.employee_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        tabLayout = (TabLayout) findViewById(R.id.id_employee_tab);
        //set tabs to screen
        for (int i= 0 ; i < tabsList.length ; i++) {
            tabLayout.addTab(tabLayout.newTab().setText((tabsList[i])));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager = (ViewPager) findViewById(R.id.id_employee_pager);
        mController.signToNotification(employee.getMail());
        startAdapter();
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent

                syncLocalDbWithClout(employee.getMail());
            }
        };

        getEmployeeTaskFromDb(employee.getMail());

    }

    public void startAdapter(){
        adapter = new EmployeePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),"");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //  syncLocalDbWithClout(employee.getMail());
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.employee_task_control_menu, menu);
        mTvUserName = (TextView)findViewById(R.id.nav_user_name);
        mTvUserName.setText("Hi " + employee.getUserName() + " " + employee.getUserLName());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_create_users) {
            Intent i = new Intent(this,AddMembersActivity.class);
            startActivity(i);
            finish();
            return true;
        } if (id == R.id.id_action_sort_task) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sort by");
            builder.setSingleChoiceItems(AppConst.SORT_CHAR_SEQUENCE, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    mController.notifyAllOnChanges();

                    switch (item) {
                        case 0:
                            taskList = AppUtils.sortList(taskList, "Waiting");
                            mController.updateLocalDB(taskList);
                            mController.notifyAllOnChanges();

                            break;
                        case 1:
                            taskList = AppUtils.sortList(taskList, "In process");
                            mController.updateLocalDB(taskList);
                            mController.notifyAllOnChanges();

                            break;
                        case 2:
                            taskList = AppUtils.sortList(taskList, "Done");
                            mController.updateLocalDB(taskList);
                            mController.notifyAllOnChanges();

                            break;
                        case 3:
                            taskList = AppUtils.sortList(taskList, "Priority");
                            mController. updateLocalDB(taskList);
                            mController.notifyAllOnChanges();
                            break;
                        case 4:
                            taskList = AppUtils.sortList(taskList, "Time");
                            mController.updateLocalDB(taskList);
                            mController.notifyAllOnChanges();
                            break;

                    }
                    sortDialog.dismiss();
                }
            });
            sortDialog = builder.create();
            sortDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_employee_check_default) {
            mController.setDefaultRefreshInterval();
        } else if (id == R.id.nav_employee_check_manual) {
            mController.setManualRefreshInterval();
        } else if (id == R.id.nav_employee_log_out) {

            ParseUser.logOut();
            mController.clearSharedPreferences();
            Intent i = new Intent(this , LoginActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_employee_name_version) {
            Intent i = new Intent(this , AboutActivity.class);
            i.putExtra("Type" , "About");
            startActivity(i);
            finish();

        }else if (id == R.id.nav_employee_students) {
            Intent i = new Intent(this , AboutActivity.class);
            i.putExtra("Type" , "Students");
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.employee_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void refreshOnClick(View view){
        getEmployeeTaskFromDb(employee.getMail());

    }

    @Override
    public void onResume() {
        super.onResume();
        String message = getIntent().getStringExtra(
                App42GCMService.ExtraMessage);
        if (message != null)
            Log.d("MainActivity-onResume", "Message Recieved :" + message);
        IntentFilter filter = new IntentFilter(
                App42GCMService.DisplayMessageAction);
        filter.setPriority(2);
        registerReceiver(mBroadcastReceiver, filter);

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
        mTracker.setScreenName("Employee");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent
                    .getStringExtra(App42GCMService.ExtraMessage);

        }
    };
    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        unregisterReceiver(mBroadcastReceiver);
        super.onPause();
    }



    public void getEmployeeTaskFromDb(final String userEmail) {
        mProgress.setMessage("Retrieving tasks data...");
        mProgress.show();
        mController.getUserTask(userEmail, new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (throwable == null) {
                    ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;
                    taskList = AppUtils.createTasksFromDB(parseObjectArrayList);
                    mController.updateLocalDB(checkForNewTask(AppUtils.sortByTime(taskList)));
                    mController.notifyAllOnChanges();
                } else {
                    mController.notifyAllOnChanges();
                }
                mProgress.dismiss();
            }
        });
    }

    public void syncLocalDbWithClout(final String userEmail){

        mController.getUserTask(userEmail, new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {

            }

            @Override
            public void done(Object o, Throwable throwable) {

                if (throwable == null) {
                    ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;


                    mController.updateLocalDB(AppUtils.sortByTime(
                            (checkForNewTask(AppUtils.createTasksFromDB(parseObjectArrayList)))));
                    mController.notifyAllOnChanges();
                }else{
                    AppUtils.Toast(getApplication(),AppConst.CONNECTION_ERROR);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.employee_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    public List <Task> checkForNewTask(List<Task> tasks){
        int counter = 0;
        List<Task> tasksToUpdate = new ArrayList<>();
        for (int i = 0 ; i < tasks.size();i++){
            if(tasks.get(i).getFirstSync() == 1){
                counter++;
                tasks.get(i).setFirstSync(0);
                tasksToUpdate.add(tasks.get(i));
            }
        }
        if (counter > 0) {
        mController.updateListOfTask(tasksToUpdate);
        mController.notifyNewTask(counter);
        }
        return tasks;
    }


    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        App42GCMController.storeRegistrationId(this, gcmRegId);
        if(!App42GCMController.isApp42Registerd(EmployeeTaskControlActivity.this))
            App42GCMController.registerOnApp42(App42API.getLoggedInUser(), gcmRegId, this);
    }

    @Override
    public void onApp42Response(String responseMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    @Override
    public void onRegisterApp42(String responseMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                App42GCMController.storeApp42Success(EmployeeTaskControlActivity.this);
            }
        });
    }


    public void onStart() {
        super.onStart();
        if (App42GCMController.isPlayServiceAvailable(this)) {
            App42GCMController.getRegistrationId(EmployeeTaskControlActivity.this,
                    AppConst.GoogleProjectNo, this);
        } else {
            Log.i("App42PushNotification",
                    "No valid Google Play Services APK found.");
        }
    }
}