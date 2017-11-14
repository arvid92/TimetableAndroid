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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Set;

import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

public class TestDb extends AndroidTestCase {

    public void setUp() {
        mContext.deleteDatabase(TimetableDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(TimetableEntry.TABLE_NAME);
        tableNameHashSet.add(LecturerEntry.TABLE_NAME);
        tableNameHashSet.add(ModuleEntry.TABLE_NAME);

        mContext.deleteDatabase(TimetableDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new TimetableDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue(c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue(tableNameHashSet.isEmpty());

        final HashSet<String> timetableColumnHashSet = new HashSet<>();
        timetableColumnHashSet.add(TimetableEntry._ID);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_LECTURER_KEY);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_MODULE_KEY);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_ROOM);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_CENTURY);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_DATE);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_START_TIME);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_END_TIME);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_CHANGE_CODE);
        timetableColumnHashSet.add(TimetableEntry.COLUMN_CALENDAR_WEEK);
        testColumnsContainedInTable(db, TimetableEntry.TABLE_NAME, timetableColumnHashSet);

        final HashSet<String> lecturerColumnHashSet = new HashSet<>();
        lecturerColumnHashSet.add(LecturerEntry._ID);
        lecturerColumnHashSet.add(LecturerEntry.COLUMN_FIRST_NAMES);
        lecturerColumnHashSet.add(LecturerEntry.COLUMN_LAST_NAME);
        lecturerColumnHashSet.add(LecturerEntry.COLUMN_TITLE);
        lecturerColumnHashSet.add(LecturerEntry.COLUMN_OFFICE);
        lecturerColumnHashSet.add(LecturerEntry.COLUMN_TELEPHONE);
        lecturerColumnHashSet.add(LecturerEntry.COLUMN_EMAIL);
        testColumnsContainedInTable(db, LecturerEntry.TABLE_NAME, lecturerColumnHashSet);

        final HashSet<String> moduleColumnHashSet = new HashSet<>();
        moduleColumnHashSet.add(ModuleEntry._ID);
        moduleColumnHashSet.add(ModuleEntry.COLUMN_NUMBER);
        moduleColumnHashSet.add(ModuleEntry.COLUMN_NAME);
        moduleColumnHashSet.add(ModuleEntry.COLUMN_ECTS);
        moduleColumnHashSet.add(ModuleEntry.COLUMN_EXAM_TYPE);
        moduleColumnHashSet.add(ModuleEntry.COLUMN_HOURS);
        testColumnsContainedInTable(db, ModuleEntry.TABLE_NAME, moduleColumnHashSet);

        db.close();
    }

    private static void testColumnsContainedInTable(SQLiteDatabase db, String tableName, Set<String> columns) {
        Cursor c = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);
        assertTrue(c.moveToFirst());

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            columns.remove(columnName);
        } while (c.moveToNext());

        assertTrue(columns.isEmpty());
    }

    public void testSingleInsertions() {
        final long moduleId = insert(ModuleEntry.TABLE_NAME, TestUtilities.createModuleValues());
        final long lecturerId = insert(LecturerEntry.TABLE_NAME, TestUtilities.createLecturerValues());
        insert(TimetableEntry.TABLE_NAME, TestUtilities.createTimetableValues(moduleId, lecturerId));
    }

    public void testDoubleInsertions() {
        final long moduleId = insert(ModuleEntry.TABLE_NAME, TestUtilities.createModuleValues(), true);
        final long lecturerId = insert(LecturerEntry.TABLE_NAME, TestUtilities.createLecturerValues(), true);
        insert(TimetableEntry.TABLE_NAME, TestUtilities.createTimetableValues(moduleId, lecturerId), true);
    }

    private long insert(String tableName, ContentValues contentValues, boolean testDoubleInsertion) {
        TimetableDbHelper dbHelper = new TimetableDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long rowId = db.insert(tableName, null, contentValues);
        if (testDoubleInsertion) {
            db.insert(tableName, null, contentValues);
        }
        assertTrue(rowId != -1);
        Cursor cursor = db.query(tableName, null, null, null, null, null, null);
        assertTrue(cursor.moveToFirst());
        TestUtilities.compareCursorToContentValues(cursor, contentValues);
        assertFalse(cursor.moveToNext());

        cursor.close();
        db.close();
        return rowId;
    }

    private long insert(String tableName, ContentValues contentValues) {
        return insert(tableName, contentValues, false);
    }
}
