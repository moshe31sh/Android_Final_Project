package ots.il.ac.shenkar.ots.activities;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.parse.Parse;

import ots.il.ac.shenkar.ots.R;
import ots.il.ac.shenkar.ots.apputiles.AnalyticsApplication;

public class SplashActivity extends AppCompatActivity {
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CountDownTimer(3000 , 1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent registerIntent = new Intent(SplashActivity.this , LoginActivity.class);
                startActivity(registerIntent);
                finish();
            }
        }.start();
        mTracker.setScreenName("Splash");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }



}

