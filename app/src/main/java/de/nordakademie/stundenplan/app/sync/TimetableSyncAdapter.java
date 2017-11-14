package de.nordakademie.stundenplan.app.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import de.nordakademie.stundenplan.app.MainActivity;
import de.nordakademie.stundenplan.app.R;
import de.nordakademie.stundenplan.app.utils.PreferencesManager;
import de.nordakademie.stundenplan.app.utils.DateFormatConverter;

import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

/**
 * Created by arvid on 05.12.16.
 */

public class TimetableSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = TimetableSyncAdapter.class.getSimpleName();
    // Interval at which to sync with the weather, in seconds = 3h.
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int TIMETABLE_NOTIFICATION_ID = 3004;

    final String DOZENT = "dozent";
    final String DOZENT_VORNAMEN = "vornamen";
    final String DOZENT_NACHNAME = "nachname";
    final String DOZENT_TITEL = "titel";
    final String DOZENT_BUERO = "buero";
    final String DOZENT_TELEFON = "telefon";
    final String DOZENT_EMAIL = "email";

    final String MODUL = "modul";
    final String MODUL_MODULNUMMER = "modulnummer";
    final String MODUL_NAME = "name";
    final String MODUL_ECTS = "ects";
    final String MODUL_PRUEFUNGSFORM = "pruefungsform";
    final String MODUL_STUNDEN = "stunden";

    final String RAUM = "raum";
    final String ZENTURIE = "zenturie";
    final String UHRZEITVON = "uhrzeitVon";
    final String UHRZEITBIS = "uhrzeitBis";
    final String AENDERUNGSCODE = "aenderungsCode";

    private String CENTURY;
    private String CALENDAR_WEEK;
    private final Context context;

    public TimetableSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        CENTURY = PreferencesManager.getPreferredCentury(context);
        CALENDAR_WEEK= PreferencesManager.getPreferredWeek(context);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String timetableJsonStr = null;

        CENTURY = PreferencesManager.getPreferredCentury(context);
        CALENDAR_WEEK= PreferencesManager.getPreferredWeek(context);

        try {
            final String TIMETABLE_BASE_URL = "http://lx05.nordakademie.de/stundenplan/";

            URL url = new URL(TIMETABLE_BASE_URL + CENTURY + "/" + CALENDAR_WEEK);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            timetableJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            addDataToDb(timetableJsonStr);
            notifyTimetable();
            informUI(context);
            getContext().getContentResolver().notifyChange(TimetableEntry.CONTENT_URI, null, false);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void addDataToDb(String timetableJsonStr) throws Exception {
        JSONArray jsonArray = new JSONArray(timetableJsonStr);

        deleteAllTables();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject timetableItemJson = jsonArray.getJSONObject(i);

            long moduleId = addModule(timetableItemJson.getJSONObject(MODUL));
            long lecturerId = addLecturer(timetableItemJson.getJSONObject(DOZENT));

            ContentValues timetableContentValues = createTimetableItemFromJson(timetableItemJson, moduleId, lecturerId);
            getContext().getContentResolver().insert(TimetableEntry.CONTENT_URI, timetableContentValues);
        }
    }

    private void deleteAllTables() {
        getContext().getContentResolver().delete(LecturerEntry.CONTENT_URI, null, null);
        getContext().getContentResolver().delete(ModuleEntry.CONTENT_URI, null, null);
        getContext().getContentResolver().delete(TimetableEntry.CONTENT_URI, null, null);
    }

    private long addLecturer(JSONObject lecturerJson) throws JSONException {
        long lecturerId;
        String firstNames = lecturerJson.getString(DOZENT_VORNAMEN);
        String lastName = lecturerJson.getString(DOZENT_NACHNAME);

        Cursor lecturerCursor = getContext().getContentResolver().query(
                LecturerEntry.CONTENT_URI,
                new String[]{LecturerEntry._ID},
                LecturerEntry.COLUMN_FIRST_NAMES + " = ? AND " + LecturerEntry.COLUMN_LAST_NAME + " = ?",
                new String[]{firstNames, lastName},
                null
        );

        if (lecturerCursor.moveToFirst()) {
            int lecturerIdIndex = lecturerCursor.getColumnIndex(LecturerEntry._ID);
            lecturerId = lecturerCursor.getLong(lecturerIdIndex);
        } else {
            ContentValues lecturerValues = new ContentValues();
            lecturerValues.put(LecturerEntry.COLUMN_FIRST_NAMES, firstNames);
            lecturerValues.put(LecturerEntry.COLUMN_LAST_NAME, lastName);
            lecturerValues.put(LecturerEntry.COLUMN_TITLE, lecturerJson.optString(DOZENT_TITEL, null));
            lecturerValues.put(LecturerEntry.COLUMN_OFFICE, lecturerJson.optString(DOZENT_BUERO, null));
            lecturerValues.put(LecturerEntry.COLUMN_TELEPHONE, lecturerJson.optString(DOZENT_TELEFON, null));
            lecturerValues.put(LecturerEntry.COLUMN_EMAIL, lecturerJson.optString(DOZENT_EMAIL, null));
            Uri lecturerInsertedId = getContext().getContentResolver().insert(LecturerEntry.CONTENT_URI, lecturerValues);
            lecturerId = ContentUris.parseId(lecturerInsertedId);
        }
        lecturerCursor.close();
        return lecturerId;
    }

    private long addModule(JSONObject moduleJson) throws JSONException {
        long moduleId;
        String moduleNumber = moduleJson.getString(MODUL_MODULNUMMER);

        Cursor moduleCursor = getContext().getContentResolver().query(
                ModuleEntry.CONTENT_URI,
                new String[]{ModuleEntry._ID},
                ModuleEntry.COLUMN_NUMBER + " = ?",
                new String[]{moduleNumber},
                null
        );

        if (moduleCursor.moveToFirst()) {
            int moduleIdIndex = moduleCursor.getColumnIndex(ModuleEntry._ID);
            moduleId = moduleCursor.getLong(moduleIdIndex);
        } else {
            ContentValues moduleValues = new ContentValues();
            moduleValues.put(ModuleEntry.COLUMN_NUMBER, moduleNumber);
            moduleValues.put(ModuleEntry.COLUMN_NAME, moduleJson.getString(MODUL_NAME));
            moduleValues.put(ModuleEntry.COLUMN_ECTS, moduleJson.getInt(MODUL_ECTS));
            moduleValues.put(ModuleEntry.COLUMN_EXAM_TYPE, moduleJson.getString(MODUL_PRUEFUNGSFORM));
            moduleValues.put(ModuleEntry.COLUMN_HOURS, moduleJson.getInt(MODUL_STUNDEN));
            Uri moduleInsertUri = getContext().getContentResolver().insert(ModuleEntry.CONTENT_URI, moduleValues);
            moduleId = ContentUris.parseId(moduleInsertUri);
        }
        moduleCursor.close();
        return moduleId;
    }

    private ContentValues createTimetableItemFromJson(JSONObject timetableItemJson, long moduleId, long lecturerId) throws JSONException, ParseException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimetableEntry.COLUMN_ROOM, timetableItemJson.getString(RAUM));
        contentValues.put(TimetableEntry.COLUMN_CENTURY, timetableItemJson.getString(ZENTURIE));
        contentValues.put(TimetableEntry.COLUMN_DATE, DateFormatConverter.convertDateStringToDateInMillis(timetableItemJson.getString(UHRZEITVON)));
        contentValues.put(TimetableEntry.COLUMN_START_TIME, DateFormatConverter.convertDateStringToTimeInMillis(timetableItemJson.getString(UHRZEITVON)));
        contentValues.put(TimetableEntry.COLUMN_END_TIME, DateFormatConverter.convertDateStringToTimeInMillis(timetableItemJson.getString(UHRZEITBIS)));
        contentValues.put(TimetableEntry.COLUMN_CHANGE_CODE, timetableItemJson.getInt(AENDERUNGSCODE));
        contentValues.put(TimetableEntry.COLUMN_CALENDAR_WEEK, CALENDAR_WEEK);
        contentValues.put(TimetableEntry.COLUMN_MODULE_KEY, moduleId);
        contentValues.put(TimetableEntry.COLUMN_LECTURER_KEY, lecturerId);
        return contentValues;
    }

    private void notifyTimetable() {
        Context context = getContext();
        SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if ( displayNotifications ) {

            String lastNotificationKey = context.getString(R.string.pref_last_notification);
            long lastSync = prefs.getLong(lastNotificationKey, 0);

            if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {

                String title = context.getString(R.string.app_name);
                String contentText = context.getString(R.string.format_notification);

                Resources resources = context.getResources();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setColor(resources.getColor(R.color.colorPrimary))
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(title)
                                .setContentText(contentText);

                Intent resultIntent = new Intent(context, MainActivity.class);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =
                        stackBuilder.getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                mBuilder.setContentIntent(resultPendingIntent);

                NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(TIMETABLE_NOTIFICATION_ID, mBuilder.build());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(lastNotificationKey, System.currentTimeMillis());
                editor.commit();
            }
        }
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        TimetableSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void informUI(Context context){
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_DATABASEUPDATE);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    };

}
