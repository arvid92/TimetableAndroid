package de.nordakademie.stundenplan.app.utils;

/**
 * Created by arvid on 14.12.16.
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

import de.nordakademie.stundenplan.app.R;

public class PreferencesManager {

    public static String getPreferredCentury(Context context) {
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_century_key), context.getString(R.string.pref_century_default));
    }
    public static String getPreferredWeek(Context context) {
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_cw_key), context.getString(R.string.pref_cw_default));
    }

    public static String getActualCalendarWeek() {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return (Integer.toString(week));
    }

}
