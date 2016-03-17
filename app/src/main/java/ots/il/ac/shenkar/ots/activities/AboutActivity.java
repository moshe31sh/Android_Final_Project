package ots.il.ac.shenkar.ots.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseUser;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AnalyticsApplication;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.User;


public class AboutActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String action;
    private TextView mTvTitle , mTvVersion ,mTvVersionNum,mTvS1,mTvS2,mTvS3;
    private User user;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        init();
    }

    public void init(){
        user = AppUtils.createUserFromPars(ParseUser.getCurrentUser());
        mToolbar = (Toolbar) findViewById(R.id.id_create_member_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTvTitle = (TextView)findViewById(R.id.id_about_title);
        mTvVersion = (TextView)findViewById(R.id.id_about_version);
        mTvVersionNum = (TextView)findViewById(R.id.id_about_version_number);
        mTvS1 = (TextView)findViewById(R.id.id_about_s1);
        mTvS2 = (TextView)findViewById(R.id.id_about_s2);
        mTvS3 = (TextView)findViewById(R.id.id_about_s3);

        Bundle bundle = getIntent().getExtras();
        action = bundle.getString("Type");
        if (action.equals("About")) {
            getSupportActionBar().setTitle("About");
            mTvS1.setVisibility(View.INVISIBLE);
            mTvS2.setVisibility(View.INVISIBLE);
            mTvS3.setVisibility(View.INVISIBLE);
        }else{
            getSupportActionBar().setTitle("Students names");
            mTvVersion.setVisibility(View.INVISIBLE);
            mTvVersionNum.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_member_menu, menu);
        menu.removeItem(R.id.send);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (user.getIsManager()) {
                startManagerTaskControlActivity();
            }else{
                startEmployeeTaskControlActivity();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (user.getIsManager()) {
            startManagerTaskControlActivity();
        }else{
            startEmployeeTaskControlActivity();
        }
    }


    public void startManagerTaskControlActivity(){
        Intent i = new Intent(this,ManagerTaskControlActivity.class);
        //Start the activity
        startActivity(i);
        finish();
    }
    public void startEmployeeTaskControlActivity(){
        Intent i = new Intent(this,EmployeeTaskControlActivity.class);
        //Start the activity
        startActivity(i);
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("About");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
