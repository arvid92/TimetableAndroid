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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

/**
 * Die TimetableDbHelper-Klasse dient der Erzeugung der Datenbanktabellen.
 */
class TimetableDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "timetable.db";

    TimetableDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_LECTURER_TABLE = "CREATE TABLE " + LecturerEntry.TABLE_NAME + " (" +
                LecturerEntry._ID + " INTEGER PRIMARY KEY," +
                LecturerEntry.COLUMN_FIRST_NAMES + " TEXT NOT NULL, " +
                LecturerEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                LecturerEntry.COLUMN_TITLE + " TEXT, " +
                LecturerEntry.COLUMN_OFFICE + " TEXT, " +
                LecturerEntry.COLUMN_TELEPHONE + " TEXT, " +
                LecturerEntry.COLUMN_EMAIL + " TEXT, " +
                "UNIQUE (" + LecturerEntry.COLUMN_FIRST_NAMES + ", " +
                LecturerEntry.COLUMN_LAST_NAME + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_MODULE_TABLE = "CREATE TABLE " + ModuleEntry.TABLE_NAME + " (" +
                ModuleEntry._ID + " INTEGER PRIMARY KEY," +
                ModuleEntry.COLUMN_NUMBER + " TEXT NOT NULL, " +
                ModuleEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ModuleEntry.COLUMN_ECTS + " TEXT NOT NULL, " +
                ModuleEntry.COLUMN_EXAM_TYPE + " TEXT NOT NULL, " +
                ModuleEntry.COLUMN_HOURS + " INTEGER NOT NULL, " +
                "UNIQUE (" + ModuleEntry.COLUMN_NUMBER + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_TIMETABLE_TABLE = "CREATE TABLE " + TimetableEntry.TABLE_NAME + " (" +
                TimetableEntry._ID + " INTEGER PRIMARY KEY, " +
                TimetableEntry.COLUMN_LECTURER_KEY + " INTEGER NOT NULL, " +
                TimetableEntry.COLUMN_MODULE_KEY + " INTEGER NOT NULL, " +
                TimetableEntry.COLUMN_ROOM + " TEXT NOT NULL, " +
                TimetableEntry.COLUMN_CENTURY + " TEXT NOT NULL, " +
                TimetableEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                TimetableEntry.COLUMN_START_TIME + " INTEGER NOT NULL, " +
                TimetableEntry.COLUMN_END_TIME + " INTEGER NOT NULL, " +
                TimetableEntry.COLUMN_CHANGE_CODE + " INTEGER NOT NULL, " +
                TimetableEntry.COLUMN_CALENDAR_WEEK + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + TimetableEntry.COLUMN_LECTURER_KEY + ") REFERENCES " +
                LecturerEntry.TABLE_NAME + " (" + LecturerEntry._ID + "), " +
                "FOREIGN KEY (" + TimetableEntry.COLUMN_MODULE_KEY + ") REFERENCES " +
                ModuleEntry.TABLE_NAME + " (" + ModuleEntry.COLUMN_NUMBER + "), " +
                "UNIQUE (" + TimetableEntry.COLUMN_LECTURER_KEY + ", " +
                TimetableEntry.COLUMN_MODULE_KEY + ", " + TimetableEntry.COLUMN_START_TIME + ", " +
                TimetableEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LECTURER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MODULE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TIMETABLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LecturerEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ModuleEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TimetableEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
