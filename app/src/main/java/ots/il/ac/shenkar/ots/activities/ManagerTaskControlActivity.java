package ots.il.ac.shenkar.ots.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AnalyticsApplication;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.apputiles.GetContacts;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AppController;
import ots.il.ac.shenkar.ots.controlers.ManagerPagerAdapter;
import ots.il.ac.shenkar.ots.dialogs.AddMemberDialog;
import ots.il.ac.shenkar.ots.dialogs.AddTaskDialog;
import ots.il.ac.shenkar.ots.gcm_plugin.App42GCMController;
import ots.il.ac.shenkar.ots.gcm_plugin.App42GCMService;
import ots.il.ac.shenkar.ots.listeners.IGetCreatedTask;
import ots.il.ac.shenkar.ots.listeners.IGetCreatedUser;
import ots.il.ac.shenkar.ots.services.TimeService;

public class ManagerTaskControlActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,IGetCreatedTask,IGetCreatedUser,
        App42GCMController.App42GCMListener
{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ManagerPagerAdapter adapter;
    private List<Task> taskList;
    private AppController mController;
    private ProgressDialog mProgress;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private android.app.AlertDialog sortDialog;
    private  String [] tabsList = { "Waiting" , "In process" ,"Done"
            ,"Rejected","All"};
    private User manager;
    private BroadcastReceiver mMessageReceiver;
    private TextView mTvUserName;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_task_control_layout);
        startService(new Intent(this, TimeService.class));
        ParseUser user = ParseUser.getCurrentUser();
        manager = AppUtils.createUserFromPars(user);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        init();
    }

    public  void init() {



        toolbar = (Toolbar) findViewById(R.id.id_manager_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(AppConst.APP_NAME);

        tabLayout = (TabLayout) findViewById(R.id.id_manager_tab);
        //set tabs to screen
        for (int i= 0 ; i < tabsList.length ; i++) {
            tabLayout.addTab(tabLayout.newTab().setText((tabsList[i])));
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewPager = (ViewPager) findViewById(R.id.id_manager_pager);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mProgress = new ProgressDialog(this);
        mController = new AppController(this);
        mController.signToNotification(manager.getMail());

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                syncLocalDbWithClout();
            }
        };
        getTaskForManager();
        startAdapter();

    }

    public void startAdapter(){
        adapter = new ManagerPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                mTracker.setScreenName(tab.getText().toString());
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manager_task_control, menu);
        mTvUserName = (TextView)findViewById(R.id.nav_user_name);
        mTvUserName.setText("Hi " + manager.getUserName() + " " + manager.getUserLName());
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

                            //      show();
                            break;
                        case 4:
                            taskList = AppUtils.sortList(taskList, "Time");
                            mController.updateLocalDB(taskList);
                            mController.notifyAllOnChanges();

                            //        show();
                            break;

                    }
                    AppUtils.Toast(getBaseContext() ,AppConst.SORTING);
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



        if (id == R.id.nav_user_list) {
            Intent i = new Intent(this,UserListActivity.class);
            //Start the activity
            startActivity(i);
            finish();
        } else if (id == R.id.nav_add_user) {
            AddMemberDialog addUserDialog = new AddMemberDialog();
            addUserDialog.show(getFragmentManager(), "User");
        } else if (id == R.id.nav_check_default) {
            mController.setDefaultRefreshInterval();
        } else if (id == R.id.nav_check_manual) {
            mController.setManualRefreshInterval();
        } else if (id == R.id.nav_log_out) {

            ParseUser.logOut();
            mController.clearSharedPreferences();
            Intent i = new Intent(this , LoginActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_name_version) {
            Intent i = new Intent(this , AboutActivity.class);
            i.putExtra("Type" , "About");
            startActivity(i);
            finish();

        }else if (id == R.id.nav_students) {
            Intent i = new Intent(this , AboutActivity.class);
            i.putExtra("Type" , "Students");
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void addTaskOnClick(View view){
        AddTaskDialog addMemberDialog = new AddTaskDialog();
        addMemberDialog.show(getFragmentManager(), "Task");
    }



    @Override
    public void updateTaskResult(final List<Task> tasks) {
        if(tasks.size() > 0) {
            for (int i = 0 ; i < tasks.size() ;i++) {
                tasks.get(i).setManager(manager.getMail());
                tasks.get(i).setFirstRead(1);
                tasks.get(i).setFirstSync(1);
                final String userMail = tasks.get(i).getUser();
                final String taskTitle = tasks.get(i).getTitle();
                mController.addTaskToLocalDb(tasks.get(i));
                mController.createTask(tasks.get(i), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            sentNotification(userMail , "You have received a new Task "+taskTitle);
                        }else{
                            AppUtils.Toast(getBaseContext() , "Unable to connect to server");
                        }
                    }
                });
            }
            syncLocalDbWithClout();
        }
    }

    @Override
    public void updateUserResult(User user) {
        SharedPreferences prefs = this.getSharedPreferences(AppConst.SharedPrefsName, 0);
        final String pass  = prefs.getString(AppConst.SharedPrefs_UserPass, null);
        final  String managerName = manager.getMail();
        if(user != null) {
            user.setManager(manager.getMail());
            final  String[] userEmailToInvite = {user.getMail()};
            mController.signUp(user, new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        AppUtils.Toast(getApplication(), AppConst.REGISTER_SUCCEED);
                        sendInvite(userEmailToInvite);

                        try {
                            ParseUser.logIn(managerName, pass);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        AppUtils.Toast(getApplication(), AppConst.REGISTER_FAILED);

                    }
                }
            });
        }
    }



    public void refreshOnClick(View view){

        syncLocalDbWithClout();
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

        mTracker.setScreenName("Manager");
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


    public void sendInvite(final String[] userEmailToInvite) {
        AppUtils.sendTeamInvitation(userEmailToInvite, this);

    }

    public void getTaskForManager() {
        mProgress.setMessage("Getting task data...");
        mProgress.show();
        mController.getAllTask(manager.getMail(), new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {

            }

            @Override
            public void done(Object o, Throwable throwable) {

                if (throwable == null) {
                    ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;
                    taskList = AppUtils.createTasksFromDB(parseObjectArrayList);
                    mController.updateLocalDB(AppUtils.sortByTime(taskList));
                    mController.notifyAllOnChanges();
                    mProgress.dismiss();
                }else{
                    AppUtils.Toast(getBaseContext() , AppConst.CONNECTION_ERROR);
                }
            }
        });
    }



    public void syncLocalDbWithClout(){
        mProgress.setMessage("Retrieving tasks data...");
        mProgress.show();
        mController.getAllTask(manager.getMail() , new FindCallback() {
            @Override
            public void done(List objects, ParseException e) {

            }

            @Override
            public void done(Object o, Throwable throwable) {

                if (throwable == null) {
                    ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;
                    taskList = AppUtils.createTasksFromDB(parseObjectArrayList);
                    mController.updateLocalDB(AppUtils.createTasksFromDB(parseObjectArrayList));
                    mProgress.dismiss();
                    mController.notifyAllOnChanges();
                }else{
                    AppUtils.Toast(getApplication(),AppConst.CONNECTION_ERROR);
                }
            }
        });
    }

    @Override
    public void onError(String errorMsg) {

    }

    @Override
    public void onGCMRegistrationId(String gcmRegId) {
        App42GCMController.storeRegistrationId(this, gcmRegId);
        if(!App42GCMController.isApp42Registerd(ManagerTaskControlActivity.this))
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
                App42GCMController.storeApp42Success(ManagerTaskControlActivity.this);
            }
        });
    }


    public void sentNotification(String userMail , String taskTitle){
        App42GCMController.sendPushToUser(userMail, taskTitle, this);
    }

    public void onStart() {
        super.onStart();
        if (App42GCMController.isPlayServiceAvailable(this)) {
            App42GCMController.getRegistrationId(ManagerTaskControlActivity.this,
                    AppConst.GoogleProjectNo, this);
        } else {
            Log.i("App42PushNotification",
                    "No valid Google Play Services APK found.");
        }
    }
}
