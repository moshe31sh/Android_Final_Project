package ots.il.ac.shenkar.ots.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AnalyticsApplication;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AddMembersAdapter;
import ots.il.ac.shenkar.ots.controlers.AppController;
import ots.il.ac.shenkar.ots.dialogs.AddMemberDialog;
import ots.il.ac.shenkar.ots.listeners.IGetCreatedUser;
import ots.il.ac.shenkar.ots.listeners.ItemClickListener;
import ots.il.ac.shenkar.ots.listeners.ItemLongClickListener;

public class AddMembersActivity extends AppCompatActivity implements ItemLongClickListener,
        ItemClickListener, IGetCreatedUser {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isNotEmpty; // check if user list isn't empty before send  email
    private AppController mController;
    private AddMembersAdapter mAdapter;
    private List<User> userList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_members_layout);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        init();
    }

    public  void init() {
        isNotEmpty = false;
        mToolbar = (Toolbar) findViewById(R.id.id_create_member_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New members");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_user_list_swipe_refresh_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.id_add_member_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mController = new AppController(this);

        userList = new ArrayList<>();
        show();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }

        });

    }

    public void show() {
        // specify an adapter (see also next example)
        mAdapter = new AddMembersAdapter(userList);
        mAdapter.setmItemClickListener(this);
        mAdapter.setmItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void addMemberOnClick(View view){
        isNotEmpty = true;
        AddMemberDialog addUserDialog = new AddMemberDialog();
        addUserDialog.show(getFragmentManager(), "User");

    }


    @Override
    public void updateUserResult(User user) {
        userList.add(user);
        show();
    }

    @Override
    public void onItemClick(View view, int pos) {

    }

    @Override
    public void onItemLongClick(View view, int pos) {

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        SharedPreferences prefs = this.getSharedPreferences(AppConst.SharedPrefsName, 0);
        final String pass  = prefs.getString(AppConst.SharedPrefs_UserPass, null);
        final  String managerName =  prefs.getString(AppConst.SharedPrefs_UserEmail, null);
        //noinspection SimplifiableIfStatement
        if (id == R.id.send) {
            if (isNotEmpty == true){

                //set the user list to my team
                AppUtils.sendTeamInvitation(AppUtils.getUserEmails(userList), this);
                for (User user : userList){
                    mController.signUp(user, new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            AppUtils.Toast(getApplication(), AppConst.REGISTER_SUCCEED);
                        }
                    });
                }
                return true;
            }else {
                AppUtils.Toast(this, "Need at least one member in team");
            }
        }
        if (id == android.R.id.home) {
            try {
                ParseUser.logIn(managerName,pass);
            } catch (ParseException e1) {
                e1.printStackTrace();
            }
            startManagerTaskControlActivity();
            return true;


        }
        return super.onOptionsItemSelected(item);
    }

    public void startManagerTaskControlActivity(){
        Intent i = new Intent(this,ManagerTaskControlActivity.class);
        //Start the activity
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        startManagerTaskControlActivity();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("AddMember");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

}
