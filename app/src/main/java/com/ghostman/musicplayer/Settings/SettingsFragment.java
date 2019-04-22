package com.ghostman.musicplayer.Settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.widget.Toast;

import com.ghostman.musicplayer.Others.Controls;
import com.ghostman.musicplayer.Others.PlayerConstants;
import com.ghostman.musicplayer.R;
import com.ghostman.musicplayer.ShakeListener;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SharedPreferences mPreferences;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener mShaker;
    private int counter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPreferences = getActivity().getSharedPreferences("com.ghostman.musicplayer.settings", MODE_PRIVATE);

        Preference sleepChangePref = this.findPreference("sleep_key");
        sleepChangePref.setSummary(mPreferences.getString("summary","NONE"));
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference sleepChangePref = this.findPreference("sleep_key");
        sleepChangePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String stringValue = newValue.toString();
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

                return true;          ///<------ Keep changes in list
            }
        });

        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShaker = new ShakeListener();
        mShaker.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake(int count) {
                Controls.nextControl();
                Log.d("------ ON SHAKE ","---- TRUE -----");
            }
        });

        Preference shakePref = this.findPreference("shake_key");
        shakePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if((Boolean)newValue) {
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
                getActivity().getApplicationContext().sendBroadcast(it);
            }
        };

        if(time == -1) {
            timer.cancel();

            Toast.makeText(getActivity().getApplicationContext(), "SLEEP CANCELED", Toast.LENGTH_SHORT).show();
            //PlayerConstants.SLEEP = false;

        } else {
            timer.start();

            Toast.makeText(getActivity().getApplicationContext(),"SLEEP after " + timeInMin + " min", Toast.LENGTH_SHORT).show();
            //PlayerConstants.SLEEP = true;

        }
    }

}
