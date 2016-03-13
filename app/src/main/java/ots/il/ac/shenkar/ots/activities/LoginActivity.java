package ots.il.ac.shenkar.ots.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AnalyticsApplication;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.apputiles.GetContacts;
import ots.il.ac.shenkar.ots.common.Task;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AppController;

public class LoginActivity extends AppCompatActivity {
    private EditText mEEmail, mEPassword;
    private TextView mTLink;
    private ProgressDialog progress;
    private AppController appController;
    private Animation animAlpha;
    private Tracker mTracker;
    private GetContacts contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form_layout);

        try {
            Parse.enableLocalDatastore(this);
            Parse.initialize(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Login");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * this method init all activity vars
     */
    public void init() {



        progress = new ProgressDialog(this);
        mEEmail = (EditText) findViewById(R.id.input_email);
        mEPassword = (EditText) findViewById(R.id.input_password);
        mTLink = (TextView) findViewById(R.id.id_link_signup);
        animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);
        appController = new AppController(this);
        try {
            User user = AppUtils.createUserFromPars(ParseUser.getCurrentUser());
            if (user != null) {
                if (user.getIsManager()) {
                    contacts = new GetContacts(this);
                    contacts.start();
                    startManagerTaskControlActivity();
                } else {
                    startEmployeeTaskControlActivity();
                }
            }
        }catch (Exception e){

        }
    }


    /**
     * active login button onclick
     * its declare on the login_layout
     *
     * @param view
     */
    public void loginOnClick(View view) {
        view.startAnimation(animAlpha);
        boolean isError = false;
        boolean emailPattenErr = true;
        String emailStr = mEEmail.getText().toString().trim();
        String passStr = mEPassword.getText().toString().trim();
        //check if email anf password fields are empty
        if(!AppUtils.isValidEmail(emailStr)){
            mEEmail.setError("Illegal email address\n"+"Example:John@gmail.com");
            emailPattenErr = false;
        }
        if (emailStr.equals("")) {
            mEEmail.setError("Enter email");
            isError = true;
        }

        if (passStr.equals("")) {
            mEPassword.setError("Enter password");
            isError = true;
        }
        if (isError == true) {
            AppUtils.Toast(LoginActivity.this, AppConst.EMPTY_FIELDS);
        } else if (!emailPattenErr){
            AppUtils.Toast(this , AppConst.EMAIL_ERR);
        } else {
            progress.setMessage("Authenticating...");
            progress.show();
            appController.login(emailStr, passStr, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    progress.dismiss();
                    if (e == null) {
                        User user = AppUtils.createUserFromPars(parseUser);
                        user.setPassword(mEPassword.getText().toString());
                        appController.setLogedIn(user);

                        if (user.getIsManager() == true) {
                            contacts = new GetContacts(getApplication());
                            contacts.start();
                            startManagerTaskControlActivity();
                            return;
                        } else if (user.getIsManager() == false) {
                            startEmployeeTaskControlActivity();
                            return;
                        }
                    } else {
                        AppUtils.Toast(LoginActivity.this, AppConst.LOGIN_FAILED);
                        return;

                    }
                }
            });

        }
    }


    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        finish();
    }

    public void signUpOnClick(View view) {
        startRegisterActivity();

    }


    /**
     * simple method to return to welcome screen when
     * click on back button
     */
    public void startManagerTaskControlActivity()
    {
        //Explicit intent.
        Intent i = new Intent(this,ManagerTaskControlActivity.class);
        //Start the activity
        startActivity(i);
        finish();
    }
    public void startRegisterActivity() {
        //Explicit intent.
        Intent i = new Intent(this, RegisterActivity.class);
        //Start the activity
        startActivity(i);
        finish();
    }

    public void startEmployeeTaskControlActivity()
    {
        //Explicit intent.
        Intent i = new Intent(this,EmployeeTaskControlActivity.class);
        //Start the activity
        startActivity(i);
        finish();
    }


}


