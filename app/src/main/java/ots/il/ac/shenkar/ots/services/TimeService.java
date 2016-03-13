package ots.il.ac.shenkar.ots.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ots.il.ac.shenkar.ots.apputiles.AppConst;

/**
 * Created by moshe on 22-02-16.
 */
public class TimeService extends Service {
    // constant
    public static long NOTIFY_INTERVAL = 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task

        SharedPreferences prefs = this.getSharedPreferences(AppConst.SharedPrefsName, 0);
        int interval  = prefs.getInt(AppConst.SharedPrefs_refreshInterval, 5 * 60);
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL * interval);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast

                    Intent intent = new Intent("my-event");
                    // add data
                    intent.putExtra("message", getDateTime());
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());

        }
    }
}
