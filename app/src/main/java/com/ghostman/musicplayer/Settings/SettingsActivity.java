package com.ghostman.musicplayer.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.Preference;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.ghostman.musicplayer.AppCompatPreferenceActivity;
import com.ghostman.musicplayer.Others.Controls;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.ShakeListener;

public class SettingsActivity extends AppCompatPreferenceActivity {


    private SharedPreferences mPreferences;
    private String SharedPrefFile = "com.ghostman.musicplayer.settings";

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener mShaker;
    private int counter;

    static Context context;

    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        context = this;

        mPreferences = getApplicationContext().getSharedPreferences(SharedPrefFile, MODE_PRIVATE);

        Preference sleepChangePref = this.findPreference("sleep_key");
        sleepChangePref.setSummary(mPreferences.getString("summary","NONE"));
        sleepChangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
                Log.d("-----STRING-VALUE------",stringValue);
                switch (stringValue) {
                    case "15" : PlayerConstants.SLEEP_TIMER = 900000;
                        break;
                    case "30" : PlayerConstants.SLEEP_TIMER = 1800000;
                        break;
                    case "45" : PlayerConstants.SLEEP_TIMER = 2700000;
                        break;
                    case "60" : PlayerConstants.SLEEP_TIMER = 3600000;
                        break;
                    case "120" : PlayerConstants.SLEEP_TIMER = 7200000;
                        break;
                    case "-1" : PlayerConstants.SLEEP_TIMER = -1;
                        break;
                }
                countDownTimer(PlayerConstants.SLEEP_TIMER, stringValue);

                String summary_sleep = stringValue.equals("-1")
                        ?"None"
                        :"Sleep after " + stringValue + " min.";

                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString("summary",summary_sleep).apply();

                preference.setSummary(mPreferences.getString("summary",summary_sleep));

                return true;
            }
        });


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Preference shakePref = this.findPreference("shake_key");
        shakePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean)newValue) {
                    mShaker = new ShakeListener();
                    mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
                        @Override
                        public void onShake(int count) {
                            Controls.nextControl();
                            Log.d("------ ON SHAKE ","---- TRUE -----");
                        }
                    });

                    mSensorManager.registerListener(mShaker, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                    Log.d("------ SHAKE ","---- TRUE -----");

                } else {
                    mSensorManager.unregisterListener(mShaker);
                    Log.d("---- SHAKE -------","FALSE");
                }
                return true;
            }
        });
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    void countDownTimer(long time, String timeInMin) {
        final CountDownTimer timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                counter++;
            }

            @Override
            public void onFinish()  {
                PlayerConstants.EXIT_HANDLER.sendMessage(PlayerConstants.EXIT_HANDLER.obtainMessage());
                Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                getApplicationContext().sendBroadcast(it);
            }
        };

        if(time == -1) {
            timer.cancel();

            Toast.makeText(this, "SLEEP CANCELED", Toast.LENGTH_SHORT).show();
            PlayerConstants.SLEEP = false;

        } else {
            timer.start();

            Toast.makeText(this,"SLEEP after " + timeInMin + " min",Toast.LENGTH_SHORT).show();
            PlayerConstants.SLEEP = true;

        }
    }
}