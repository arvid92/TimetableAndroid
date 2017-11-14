package de.nordakademie.stundenplan.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import de.nordakademie.stundenplan.app.data.TimetableContract;
import de.nordakademie.stundenplan.app.sync.TimetableSyncAdapter;
import de.nordakademie.stundenplan.app.utils.PreferencesManager;

import static de.nordakademie.stundenplan.app.utils.ConnectivityManager.isNetworkAvailable;

/**
 * @author Daniel Bormann (14150)
 *         <p>
 *         The MainActivity.
 */

public class MainActivity extends AppCompatActivity implements LessonProvider {

    private static final Object sMainActivityLock = new Object();


    private ArrayList<Long> mLongList;
    private BroadcastReceiver mReceiver;
    private String mCentury;
    private String mWeek;
    private long selectedDate;
    private long selectedTime;
    private int selectedModule;
    private boolean mTwoPane;
    private Cursor mLessonCursor;

    public static final String ACTION_DATABASEUPDATE = R.string.content_authority + "_DATABASEUPDATE";
    private static final int STATE_LECTURERDETAILOPEN = 2;
    private static final int STATE_SWIPEOPEN = 1;
    private static final int STATE_DEFAULT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.getLong("selectedDate") != 0 && savedInstanceState.getInt("selectedModule") != 0 && savedInstanceState.getLong("selectedTime") != 0) {
                selectedDate = savedInstanceState.getLong("selectedDate");
                selectedTime = savedInstanceState.getLong("selectedTime");
                selectedModule = savedInstanceState.getInt("selectedModule");
                try {
                    setupLesson(selectedDate, selectedTime, selectedModule);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.detail_contentFragment) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        mReceiver = getBCReciever();
        mCentury = PreferencesManager.getPreferredCentury(this);
        mWeek = PreferencesManager.getPreferredWeek(this);

        updateTimetable();
        setupDates();
        setupBCReciever();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_contentFragment, new MainWeekOverviewListFragment())
                    .commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String week = PreferencesManager.getPreferredWeek(this);
        String century = PreferencesManager.getPreferredCentury(this);

        if (week != null && !week.equals(mWeek) || century != null && !century.equals(mCentury)) {
            updateTimetable();
            mWeek = week;
            mCentury = century;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (selectedDate != 0 && selectedModule != 0 && selectedTime != 0) {
            outState.putLong("selectedDate", selectedDate);
            outState.putLong("selectedTime", selectedTime);
            outState.putInt("selectedModule", selectedModule);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int state = STATE_DEFAULT;
        for (Fragment fragment : getAllVisibleFragments()) {
            if (fragment instanceof LecturerDetailFragment && state < STATE_LECTURERDETAILOPEN) {
                state = STATE_LECTURERDETAILOPEN;
            } else if ((fragment instanceof MainWeekOverviewSwipeFragment || fragment instanceof ListLessonsFragment) && state < STATE_SWIPEOPEN) {
                state = STATE_SWIPEOPEN;
            }
        }

        switch (state) {
            case STATE_DEFAULT:
                super.onBackPressed();
                break;
            case STATE_SWIPEOPEN:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_contentFragment, new MainWeekOverviewListFragment())
                        .commit();
                break;
            case STATE_LECTURERDETAILOPEN:
                if (mTwoPane) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.detail_contentFragment, new LessonDetailFragment())
                            .commit();
                }
                break;
        }


    }

    @Override
    public Cursor getSelectedLesson() {
        return mLessonCursor;
    }


    private void setupLesson(long date, long startTime, int moduleKey) {
        mLessonCursor = getContentResolver().query(
                TimetableContract.TimetableEntry.buildTimetableWithDateStartTimeModuleKeyUri(date, startTime, moduleKey), null
                , null, null, null);
        mLessonCursor.moveToFirst();
    }

    private void setupDates() {
        synchronized (sMainActivityLock) {
            Cursor cursor = getContentResolver().query(
                    TimetableContract.TimetableEntry.CONTENT_URI, new String[]{TimetableContract.TimetableEntry._ID, TimetableContract.TimetableEntry.COLUMN_DATE}, null, null, null);
            mLongList = new ArrayList<>();
            if (!cursor.isClosed()) {
                SortedSet<Long> set = new TreeSet<>();
                while (cursor.moveToNext()) {
                    mLongList.add(cursor.getLong(1));
                }
                set.addAll(mLongList);
                mLongList.clear();
                mLongList.addAll(set);
            }
        }
    }

    private void setupBCReciever() {
        IntentFilter myFilter = new IntentFilter();
        myFilter.addAction(MainActivity.ACTION_DATABASEUPDATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, myFilter);
    }

    public void updateTimetable() {
        if (isNetworkAvailable(this)) {
            TimetableSyncAdapter.syncImmediately(this);
        } else {
            Toast.makeText(getBaseContext(), R.string.warn_no_network, Toast.LENGTH_LONG).show();
        }
    }

    private BroadcastReceiver getBCReciever() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String sAction = intent.getAction();
                if (MainActivity.ACTION_DATABASEUPDATE.equals(sAction)) {
                    setupDates();
                    for (Fragment f : getAllVisibleFragments()) {
                        try {
                            ((Updatable) f).update();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    private ArrayList<Fragment> getAllVisibleFragments() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        ArrayList<Fragment> list = new ArrayList<>();
        if (!fragments.isEmpty()) {
            for (Fragment fragment : fragments) {
                list.add(fragment);
            }
        }
        return list;
    }

    public ArrayList<Long> getmLongList() {
        return mLongList;
    }

    public void openDetailFragment(long date, long startTime, int moduleKey) {
        if (mTwoPane) {
            selectedDate = date;
            selectedTime = startTime;
            selectedModule = moduleKey;
            try {
                setupLesson(selectedDate, selectedTime, selectedModule);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_contentFragment, new LessonDetailFragment())
                        .commit();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }


        } else {
            Intent intent = new Intent(this, LessonDetailActivity.class);
            intent.putExtra("date", date);
            intent.putExtra("startTime", startTime);
            intent.putExtra("moduleKey", moduleKey);
            startActivity(intent);
        }
    }

    public void displayAlertOnVersionChange(String title, String message) {
        PackageInfo pInfo;
        int version = 0;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int versionNow = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("versionNow", 0);
        if (versionNow < version) {
            new AlertDialog.Builder(this).setTitle(title).setMessage(message).setNeutralButton("OK", null).show();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putInt("versionNow", version)
                    .apply();
        }
    }


}
