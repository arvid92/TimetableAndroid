package de.nordakademie.stundenplan.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.nordakademie.stundenplan.app.data.TimetableContract;

/**
 * Created by Leif Johannson on 27.11.2016.
 */


public class LessonDetailActivity extends AppCompatActivity implements LessonProvider{
    static Cursor mCursor;
    private long date;
    private long startTime;
    private int moduleKey;
    static int state = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        date = getIntent().getLongExtra("date", 0);
        startTime = getIntent().getLongExtra("startTime", 0);
        moduleKey = getIntent().getIntExtra("moduleKey", 0);
        try {
            mCursor = getContentResolver().query(
                    TimetableContract.TimetableEntry.buildTimetableWithDateStartTimeModuleKeyUri(date, startTime, moduleKey), null
                    , null, null, null);
            mCursor.moveToFirst();
        } catch (NullPointerException e) {

        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_contentFragment, new LessonDetailFragment())
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (state == 0) {
            super.onBackPressed();
        } else {
            state = 0;
            LessonDetailFragment fragment = new LessonDetailFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.detail_contentFragment, fragment); //Container -> R.id.contentFragment
            transaction.commit();
        }

    }

    public void setState(int i){
        state = i;
    }


    @Override
    public Cursor getSelectedLesson() {
        return mCursor;
    }
}
