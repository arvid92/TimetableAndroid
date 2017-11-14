/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nordakademie.stundenplan.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import static de.nordakademie.stundenplan.app.data.TimetableContract.CONTENT_AUTHORITY;
import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.PATH_LECTURER;
import static de.nordakademie.stundenplan.app.data.TimetableContract.PATH_MODULE;
import static de.nordakademie.stundenplan.app.data.TimetableContract.PATH_TIMETABLE;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

/**
 * Der TimetableProvider stellt Methoden für CRUD-Zugriffe auf die Datenbank zur Verfügung.
 * Die Methoden werden immer mit einer Uri als Parameter aufgerufen, welche die Information enthält,
 * auf welcher Tabelle die Operation ausgeführt werden soll. Diese Zuordnung erfolgt durch den
 * UriMatcher. Des weiteren kann die Uri z.B. Parameter für eine WHERE-Bedingung enthalten.
 */
public class TimetableProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TimetableDbHelper mOpenHelper;

    public static final int TIMETABLE = 100;
    public static final int TIMETABLE_WITH_DATE = 101;
    public static final int TIMETABLE_WITH_DATE_AND_START_TIME_AND_MODULE_KEY = 102;
    public static final int LECTURER = 200;
    public static final int LECTURER_WITH_FIRST_AND_LAST_NAMES = 201;
    public static final int MODULE = 300;
    public static final int MODULE_WITH_NUMBER = 301;

    /**
     * Initialisierung des UriMatchers. Es werden die möglichen Uris definiert und auf welche
     * Tabelle diese abgebildet werden sollen, bzw. wie der konkrete Zugriff erfolgen soll.
     *
     * @return der initalisierte UriMatcher
     */
    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        matcher.addURI(authority, PATH_TIMETABLE, TIMETABLE);
        matcher.addURI(authority, PATH_TIMETABLE + "/#", TIMETABLE_WITH_DATE);
        matcher.addURI(authority, PATH_TIMETABLE + "/#/#/#", TIMETABLE_WITH_DATE_AND_START_TIME_AND_MODULE_KEY);

        matcher.addURI(authority, PATH_LECTURER, LECTURER);
        matcher.addURI(authority, PATH_LECTURER + "/*/*", LECTURER_WITH_FIRST_AND_LAST_NAMES);

        matcher.addURI(authority, PATH_MODULE, MODULE);
        matcher.addURI(authority, PATH_MODULE + "/*", MODULE_WITH_NUMBER);
        return matcher;
    }

    private static final SQLiteQueryBuilder sTimetableQueryBuilder;

    static {
        sTimetableQueryBuilder = new SQLiteQueryBuilder();

        sTimetableQueryBuilder.setTables(
                "(" + TimetableEntry.TABLE_NAME + " INNER JOIN " + LecturerEntry.TABLE_NAME +
                        " ON " + TimetableEntry.TABLE_NAME + "." + TimetableEntry.COLUMN_LECTURER_KEY +
                        " = " + LecturerEntry.TABLE_NAME + "." + LecturerEntry._ID +
                        ") INNER JOIN " + ModuleEntry.TABLE_NAME +
                        " ON " + TimetableEntry.TABLE_NAME + "." + TimetableEntry.COLUMN_MODULE_KEY +
                        " = " + ModuleEntry.TABLE_NAME + "." + ModuleEntry._ID);
    }

    // String, welcher eine WHERE-Bedingung repräsentiert.
    // timetable.date = ?
    private static final String sTimetableDateSettingSelection =
            TimetableEntry.TABLE_NAME + "." + TimetableEntry.COLUMN_DATE + " = ? ";

    private Cursor getTimetableByDateSetting(Uri uri, String[] projection, String sortOrder) {
        String date = TimetableEntry.getDateSettingFromUri(uri);
        String[] selectionArgs = new String[]{date};

        return sTimetableQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTimetableDateSettingSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    // String, welcher eine WHERE-Bedingung repräsentiert.
    // timetable.date = ? AND start_time = ? AND module_id = ?
    private static final String sTimetableDateStartTimeModuleKeySettingSelection =
            TimetableEntry.TABLE_NAME + "." + TimetableEntry.COLUMN_DATE + " = ? AND " +
                    TimetableEntry.COLUMN_START_TIME + " = ? AND " + TimetableEntry.COLUMN_MODULE_KEY + " = ? ";

    private Cursor getTimetableByDateAndStartTimeAndModuleKeySetting(Uri uri, String[] projection, String sortOrder) {
        String date = TimetableEntry.getDateSettingFromUri(uri);
        final String startTime = TimetableEntry.getStartTimeSettingFromUri(uri);
        final String moduleKey = TimetableEntry.getModuleKeySettingFromUri(uri);

        String[] selectionArgs = new String[]{date, startTime, moduleKey};

        return sTimetableQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sTimetableDateStartTimeModuleKeySettingSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    // String, welcher eine WHERE-Bedingung repräsentiert.
    //lecturer.first_names = ? AND last_name = ?
    private static final String sFirstAndLastNamesSelection =
            LecturerEntry.TABLE_NAME + "." + LecturerEntry.COLUMN_FIRST_NAMES + " = ? AND " +
                    LecturerEntry.COLUMN_LAST_NAME + " = ? ";

    private Cursor getLecturerByFirstAndLastNamesSetting(Uri uri, String[] projection, String sortOrder) {
        String firstNames = LecturerEntry.getFirstNamesFromUri(uri);
        String lastName = LecturerEntry.getLastNameFromUri(uri);
        String[] selectionArgs = new String[]{firstNames, lastName};

        return mOpenHelper.getReadableDatabase().query(
                LecturerEntry.TABLE_NAME,
                projection,
                sFirstAndLastNamesSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    // String, welcher eine WHERE-Bedingung repräsentiert.
    //module.number = ?
    private static final String sModuleNumberSettingSelection =
            ModuleEntry.TABLE_NAME + "." + ModuleEntry.COLUMN_NUMBER + " = ? ";

    private Cursor getModuleByNumberSetting(Uri uri, String[] projection, String sortOrder) {
        String moduleNumber = ModuleEntry.getModuleNumberSettingFromUri(uri);
        String[] selectionArgs = new String[]{moduleNumber};

        return mOpenHelper.getReadableDatabase().query(
                ModuleEntry.TABLE_NAME,
                projection,
                sModuleNumberSettingSelection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TimetableDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TIMETABLE:
                return TimetableEntry.CONTENT_TYPE;
            case TIMETABLE_WITH_DATE:
                return TimetableEntry.CONTENT_TYPE;
            case TIMETABLE_WITH_DATE_AND_START_TIME_AND_MODULE_KEY:
                return TimetableEntry.CONTENT_ITEM_TYPE;
            case LECTURER:
                return LecturerEntry.CONTENT_TYPE;
            case LECTURER_WITH_FIRST_AND_LAST_NAMES:
                return LecturerEntry.CONTENT_ITEM_TYPE;
            case MODULE:
                return ModuleEntry.CONTENT_TYPE;
            case MODULE_WITH_NUMBER:
                return ModuleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case TIMETABLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TimetableEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case TIMETABLE_WITH_DATE: {
                retCursor = getTimetableByDateSetting(uri, projection, sortOrder);
                break;
            }
            case TIMETABLE_WITH_DATE_AND_START_TIME_AND_MODULE_KEY: {
                retCursor = getTimetableByDateAndStartTimeAndModuleKeySetting(uri, projection, sortOrder);
                break;
            }
            case LECTURER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LecturerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case LECTURER_WITH_FIRST_AND_LAST_NAMES: {
                retCursor = getLecturerByFirstAndLastNamesSetting(uri, projection, sortOrder);
                break;
            }
            case MODULE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ModuleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MODULE_WITH_NUMBER: {
                retCursor = getModuleByNumberSetting(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case TIMETABLE: {
                long _id = db.insert(TimetableEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TimetableEntry.buildTimetableUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LECTURER: {
                long _id = db.insert(LecturerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = LecturerEntry.buildLecturerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MODULE: {
                long _id = db.insert(ModuleEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ModuleEntry.buildModuleUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (selection == null) selection = "1";
        switch (sUriMatcher.match(uri)) {
            case TIMETABLE:
                rowsDeleted = db.delete(
                        TimetableEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case LECTURER:
                rowsDeleted = db.delete(
                        LecturerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MODULE:
                rowsDeleted = db.delete(
                        ModuleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case TIMETABLE:
                rowsUpdated = db.update(TimetableEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case LECTURER:
                rowsUpdated = db.update(LecturerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MODULE:
                rowsUpdated = db.update(ModuleEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}