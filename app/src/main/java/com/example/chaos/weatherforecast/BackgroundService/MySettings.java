package com.example.chaos.weatherforecast.BackgroundService;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.chaos.weatherforecast.Main.MyApp;
import com.example.chaos.weatherforecast.R;

import java.util.List;



public class MySettings extends AppCompatPreferenceActivity {

    //使preference刷新自己的summary
    private static void freshPreference(Preference preference, Object newValue){

        if(preference instanceof TwoStatePreference){
            ((TwoStatePreference)preference).setChecked((Boolean) newValue);
        }
        else if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list.

            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue((String) newValue);

            // Set the summary to reflect the new value.
            preference.setSummary(
                    index >= 0
                            ? listPreference.getEntries()[index]
                            : null);

        } else {
            // For all other preferences, set the summary to the value's
            // simple string representation.
            preference.setSummary((String)newValue);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_my_headers,target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {

            return PreferenceFragment.class.getName().equals(fragmentName)
                    || StatePreferenceFragment.class.getName().equals(fragmentName)
                    || LocationPreferenceFragment.class.getName().equals(fragmentName)
                    || TimePreferenceFragment.class.getName().equals(fragmentName);

    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    public static class StatePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_state);
            setHasOptionsMenu(true);

            //设置开启/关闭preference
            //显示当前状态
            TwoStatePreference myTwoStatePreference= (TwoStatePreference) findPreference("run");
            SharedPreferences sharedPreferences=
                    PreferenceManager.getDefaultSharedPreferences(myTwoStatePreference.getContext());

            freshPreference(myTwoStatePreference,sharedPreferences.getBoolean("run",false));
            myTwoStatePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((boolean)newValue){
                        //变为运行,则启动服务
                        Intent intent=new Intent(MyApp.getContext(),WeatherService.class);
                        MyApp.getContext().startService(intent);
                   }
                    else{
                        //变为停止,则停止服务
                        AlarmManager alarmManager=
                                (AlarmManager) MyApp.getContext().getSystemService(Context.ALARM_SERVICE);
                        //取消定时启动
                        PendingIntent pendingIntent=PendingIntent.getService(MyApp.getContext(), 0,
                                new Intent(MyApp.getContext(), WeatherService.class),
                                PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManager.cancel(pendingIntent);
                    }
                    return true;
                }
            });

            myTwoStatePreference= (TwoStatePreference) findPreference("wifi");
            freshPreference(myTwoStatePreference,sharedPreferences.getBoolean("wifi",false));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), MySettings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class LocationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_location);
            setHasOptionsMenu(true);

            EditTextPreference myEditTextPreference= (EditTextPreference) findPreference("location");
            SharedPreferences sharedPreferences=
                    PreferenceManager.getDefaultSharedPreferences(myEditTextPreference.getContext());

            freshPreference(myEditTextPreference,sharedPreferences.getString("location","武汉"));
            myEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    freshPreference(preference,newValue);
                    return true;
                }
            });

            myEditTextPreference= (EditTextPreference) findPreference("number");
            freshPreference(myEditTextPreference,sharedPreferences.getString("number","57494"));
            myEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    freshPreference(preference,newValue);
                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), MySettings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class TimePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_time);
            setHasOptionsMenu(true);

            ListPreference myListPreference= (ListPreference) findPreference("remindTime");
            SharedPreferences sharedPreferences=
                    PreferenceManager.getDefaultSharedPreferences(myListPreference.getContext());
            freshPreference(myListPreference,sharedPreferences.getString("remindTime","22"));
            myListPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    freshPreference(preference,newValue);
                    return true;
                }
            });

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), MySettings.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
