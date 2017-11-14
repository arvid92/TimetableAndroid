package de.nordakademie.stundenplan.app;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import de.nordakademie.stundenplan.app.utils.PreferencesManager;

/**
 * Created by Timo on 27.11.2016.
 */

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_century_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_cw_key)));
        //bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sync_cw_key)));

        final CheckBoxPreference SYNC_CW_PREF = (CheckBoxPreference)findPreference("sync_cw");
        final EditTextPreference CW_PREF = (EditTextPreference)findPreference("calendar_week");

        if(SYNC_CW_PREF.isChecked()) {
            CW_PREF.setEnabled(false);
        }else{
            CW_PREF.setEnabled(true);
        }

        SYNC_CW_PREF.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String val = newValue.toString();
                 Boolean syncChecked = SYNC_CW_PREF.isChecked();
                 if(syncChecked)
                     CW_PREF.setEnabled(true);
                    else
                     CW_PREF.setEnabled(false);
                     CW_PREF.setText(PreferencesManager.getActualCalendarWeek());
                     CW_PREF.setSummary(PreferencesManager.getActualCalendarWeek());
                        return true;
            }
        });
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                android.preference.PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if(getPreferences(R.id.cw_text).equals(preference)) {
            int cw = Integer.valueOf(stringValue);
            if(cw > 0 && cw < 54){
                preference.setSummary(stringValue);
            } else {
                Toast toast = Toast.makeText(getBaseContext(), R.string.warn_wrong_cw, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
        } else {
            preference.setSummary(stringValue);
        }

        return true;
    }



    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }


}
