package ots.il.ac.shenkar.ots.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
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
import ots.il.ac.shenkar.ots.controlers.UserListAdapter;
import ots.il.ac.shenkar.ots.listeners.ItemClickListener;
import ots.il.ac.shenkar.ots.listeners.ItemLongClickListener;

public class UserListActivity extends AppCompatActivity implements ItemLongClickListener,
        ItemClickListener {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    // private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean isNotEmpty; // check if user list isn't empty before send  email
    private  String manager;
    private AppController mController;
    private UserListAdapter mAdapter;
    private List<User> userList;
    private FloatingActionButton fab;
    private ProgressDialog mProgress;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Menu menu;
    private boolean click;
    private User currentUser;
    private Animation animAlpha;
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
        click = false;
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        SharedPreferences prefs = this.getSharedPreferences(AppConst.SharedPrefsName, 0);
        manager = prefs.getString(AppConst.SharedPrefs_ManagerEmail, null);
        isNotEmpty = false;
        mToolbar = (Toolbar) findViewById(R.id.id_create_member_bar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.id_user_list_swipe_refresh_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User list");
        fab = (FloatingActionButton) findViewById(R.id.id_fab_add_member);
        fab.setVisibility(View.INVISIBLE);

        mRecyclerView = (RecyclerView) findViewById(R.id.id_add_member_recycle_view);
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mController = new AppController(this);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllUserFromDb();
                mSwipeRefreshLayout.setRefreshing(false);
            }

        });
        getAllUserFromDb();



    }



    public void show() {
//TODO need to user the controller and connect the db
        // specify an adapter (see also next example)
        mAdapter = new UserListAdapter(userList);
        mAdapter.setmItemClickListener(this);
        mAdapter.setmItemLongClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, int pos) {
        TextView mTvmail , mTvName , mTvLName  , mTvPhone;
        Button mBtnClose , mBtnSave;
        TextView mTvTitle;

        User user = userList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final AlertDialog dialog = builder.create();
        View dialogView = dialog.getLayoutInflater().inflate(R.layout.user_view, null);

        mTvmail = (TextView) dialogView.findViewById(R.id.id_view_email);
        mTvName = (TextView) dialogView.findViewById(R.id.id_view_fname);
        mTvLName = (TextView) dialogView.findViewById(R.id.id_view_lname);
        mTvPhone = (TextView) dialogView.findViewById(R.id.id_view_phone);
        mTvTitle = (TextView) dialogView.findViewById(R.id.id_view_member_title);

        mBtnClose = (Button) dialogView.findViewById(R.id.id_view_member_close_button);

        dialog.setView(dialogView);

        mTvTitle.setText(user.getMail());
        mTvmail.setText(user.getMail());
        mTvName.setText(user.getUserLName());
        mTvLName.setText(user.getUserName());
        mTvPhone.setText(user.getUserPhone());
        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onItemLongClick(View view, int pos) {
        view.startAnimation(animAlpha);
        MenuItem item = menu.findItem(R.id.id_action_delete);
        if (click == false) {
            click = true;
        }else if (click == true){
            click = false;
        }

        if (click == true) {
            item.setVisible(true);

        }
        if (click == false) {
            item.setVisible(false);
        }
        currentUser = userList.get(pos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_list_menu, menu);
        this.menu = menu;
        menu.setGroupVisible(0,false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == android.R.id.home) {
            startManagerTaskControlActivity();
            return true;

        }
        if(id == R.id.id_action_delete){
            final ProgressDialog mProgress = new ProgressDialog(this);
            mProgress.setMessage("Delete user...");
            mProgress.show();
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    this);
            alert.setMessage("Are you sure to delete "+currentUser.getUserName()+" and all his records?");
            alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    mController.getUserTask(currentUser.getMail(), new FindCallback() {
                        @Override
                        public void done(List objects, ParseException e) {

                        }

                        @Override
                        public void done(Object o, Throwable throwable) {
                            ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;
                            for (int i = 0; i < parseObjectArrayList.size(); i++) {
                                parseObjectArrayList.get(i).deleteInBackground();
                            }
                            mController.deleteUser(currentUser, new DeleteCallback() {
                                @Override
                                public void done(ParseException e) {
                                    mProgress.dismiss();
                                    getAllUserFromDb();
                                }
                            });
                            click = false;
                            MenuItem item = menu.findItem(R.id.id_action_delete);
                            item.setVisible(false);
                            mProgress.dismiss();
                        }
                    });
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mProgress.dismiss();
                    click = false;
                    MenuItem item = menu.findItem(R.id.id_action_delete);
                    item.setVisible(false);
                    dialog.dismiss();
                }
            });

            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAllUserFromDb() {
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Retrieving users data...");
        mProgress.show();
        mController.getAllUsers(manager, new FindCallback() {
                    @Override
                    public void done(List objects, ParseException e) {

                    }

                    @Override
                    public void done(Object o, Throwable throwable) {
                        if (throwable == null) {
                            ArrayList<ParseObject> parseObjectArrayList = (ArrayList<ParseObject>) o;
                            userList = AppUtils.getUser(parseObjectArrayList);
                            mProgress.dismiss();
                            show();
                        } else {
                            AppUtils.Toast(getApplication(), AppConst.CONNECTION_ERROR);
                        }
                    }
                }

        );
    }


    @Override
    public void onBackPressed() {
        startManagerTaskControlActivity();
    }


    public void startManagerTaskControlActivity(){
        Intent i = new Intent(this,ManagerTaskControlActivity.class);
        //Start the activity
        startActivity(i);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("UserList");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
