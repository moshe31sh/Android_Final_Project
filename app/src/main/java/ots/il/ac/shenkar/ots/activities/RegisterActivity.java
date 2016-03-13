package ots.il.ac.shenkar.ots.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AnalyticsApplication;
import ots.il.ac.shenkar.ots.apputiles.AppConst;
import ots.il.ac.shenkar.ots.apputiles.AppUtils;
import ots.il.ac.shenkar.ots.common.User;
import ots.il.ac.shenkar.ots.controlers.AppController;
import ots.il.ac.shenkar.ots.controlers.IAppController;

public class RegisterActivity extends AppCompatActivity {
    private EditText mEEmail , mEPassword , mEName , mELName , mERePassword;
    private AppController mController;
    private ProgressDialog progress;
    private Tracker mTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form_layout);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        init();
    }

    /**
     * init register vars
     */
    public void init(){
        mEName = (EditText)findViewById(R.id.id_register_user_name);
        mELName = (EditText)findViewById(R.id.id_register_last_user_name);
        mEEmail = (EditText)findViewById(R.id.id_register_email);
        mEPassword = (EditText)findViewById(R.id.id_register_user_password);
        mERePassword = (EditText)findViewById(R.id.id_register_user_confirm_password);
    }

    public void registerOnClick(View view){
        boolean isEmpty = false;
        boolean isPassErr = false;
        String name = mEName.getText().toString().trim();
        String lName = mELName.getText().toString().trim();
        String email = mEEmail.getText().toString().trim();
        String password = mEPassword.getText().toString().trim();
        String rePassword = mERePassword.getText().toString().trim();
///check input from user
        if (name.equals("")){
            mEName.setError("Enter name");
            isEmpty = true;
        }
        if (lName.equals("")){
            mELName.setError("Enter last name");
            isEmpty = true;
        }
        if (email.equals("")){
            mEEmail.setError("Enter email");
            isEmpty = true;
        }
        if ( password.equals("")){
            mEPassword.setError("Enter password");
            isEmpty = true;
        }
        if ( password.length() < 6){
            mEPassword.setError("Password need to be at list 6 letters");
            isPassErr = true;
        }

        if (rePassword.equals("")){
            mERePassword.setError("Enter password");
            isEmpty = true;
        }

        if (isEmpty == true) {
            AppUtils.Toast(RegisterActivity.this, AppConst.EMPTY_FIELDS);
//check if password is ok
        } else if(isPassErr == true){
            AppUtils.Toast(RegisterActivity.this, AppConst.PASS_ERROR);

        }else  if(!password.equals(rePassword)){
            AppUtils.Toast(RegisterActivity.this, AppConst.NOT_MATCH_PASS);
            mEPassword.setText("");
            mERePassword.setText("");
        }else{
            progress = new ProgressDialog(this);
            progress.setMessage("Registering...");
            progress.show();
            mController = new AppController(this);
            User manager = new User();
            manager.setUserName(name);
            manager.setUserLName(lName);
            manager.setMail(email);
            manager.setPassword(password);
            manager.setUserPhone(password);
            manager.setManager("");
            manager.setIsManager(true);
            mController.setLogedIn(manager);
            mController.signUp(manager, new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    AppUtils.Toast(RegisterActivity.this, AppConst.REGISTER_SUCCEED);
                    Intent registerIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(registerIntent);
                    finish();
                    progress.dismiss();
                }
            }) ;

        }
    }


    @Override
    public void onBackPressed() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName("Register");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
