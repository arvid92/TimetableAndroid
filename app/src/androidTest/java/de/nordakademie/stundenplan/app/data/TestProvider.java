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

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

public class TestProvider extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    private void deleteAllRecordsFromProvider() {
        mContext.getContentResolver().delete(TimetableEntry.CONTENT_URI, null, null);
        assertDeletion(TimetableEntry.CONTENT_URI);

        mContext.getContentResolver().delete(ModuleEntry.CONTENT_URI, null, null);
        assertDeletion(ModuleEntry.CONTENT_URI);

        mContext.getContentResolver().delete(LecturerEntry.CONTENT_URI, null, null);
        assertDeletion(LecturerEntry.CONTENT_URI);
    }

    private void assertDeletion(Uri contentUri) {
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testProviderRegistry() throws Exception {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), TimetableProvider.class.getName());
        ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
        assertEquals(providerInfo.authority, TimetableContract.CONTENT_AUTHORITY);
    }

    public void testGetType() {
        String firstNames = "Hans Peter";
        String lastName = "Wurscht";
        String moduleNumber = "I154";
        long date = 4567350000L;
        long startTime = 32980000;
        int moduleKey = 3;

        String type = mContext.getContentResolver().getType(TimetableEntry.CONTENT_URI);
        assertEquals(TimetableEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TimetableEntry.buildTimetableWithDateUri(date));
        assertEquals(TimetableEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(TimetableEntry.buildTimetableWithDateStartTimeModuleKeyUri(date, startTime, moduleKey));
        assertEquals(TimetableEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(LecturerEntry.CONTENT_URI);
        assertEquals(LecturerEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(LecturerEntry.buildLecturerWithFirstAndLastNames(firstNames, lastName));
        assertEquals(LecturerEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(ModuleEntry.CONTENT_URI);
        assertEquals(ModuleEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver().getType(ModuleEntry.buildModuleWithNumber(moduleNumber));
        assertEquals(ModuleEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testBasicQueries() {
        final long moduleId = testBasicQuery(ModuleEntry.TABLE_NAME, ModuleEntry.CONTENT_URI, TestUtilities.createModuleValues());
        final long lecturerId = testBasicQuery(LecturerEntry.TABLE_NAME, LecturerEntry.CONTENT_URI, TestUtilities.createLecturerValues());
        testBasicQuery(TimetableEntry.TABLE_NAME, TimetableEntry.CONTENT_URI, TestUtilities.createTimetableValues(moduleId, lecturerId));
    }

    private long testBasicQuery(String tableName, Uri contentUri, ContentValues contentValues) {
        final long rowId = TestUtilities.insertContentValuesDirectlyInDb(mContext, tableName, contentValues);
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        TestUtilities.validateCursor(cursor, contentValues);

        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals(cursor.getNotificationUri(), contentUri);
        }
        return rowId;
    }

    public void testUpdateModule() {
        ContentValues moduleValues = TestUtilities.createModuleValues();
        Uri moduleUri = mContext.getContentResolver().insert(ModuleEntry.CONTENT_URI, moduleValues);
        long moduleRowId = ContentUris.parseId(moduleUri);
        ContentValues updatedModuleValues = new ContentValues(moduleValues);
        updatedModuleValues.put(ModuleEntry._ID, moduleRowId);
        updatedModuleValues.put(ModuleEntry.COLUMN_NUMBER, (moduleValues.getAsString(ModuleEntry.COLUMN_NUMBER) + "a"));
        testUpdate(moduleRowId, ModuleEntry._ID, ModuleEntry.CONTENT_URI, moduleValues, updatedModuleValues);
    }

    public void testUpdateLecturer() {
        ContentValues lecturerValues = TestUtilities.createLecturerValues();
        Uri lecturerUri = mContext.getContentResolver().insert(LecturerEntry.CONTENT_URI, lecturerValues);
        long lecturerRowId = ContentUris.parseId(lecturerUri);
        ContentValues updatedLecturerValues = new ContentValues(lecturerValues);
        updatedLecturerValues.put(LecturerEntry._ID, lecturerRowId);
        updatedLecturerValues.put(LecturerEntry.COLUMN_FIRST_NAMES, (lecturerValues.getAsString(LecturerEntry.COLUMN_FIRST_NAMES) + " Jörg"));
        testUpdate(lecturerRowId, LecturerEntry._ID, LecturerEntry.CONTENT_URI, lecturerValues, updatedLecturerValues);
    }

    public void testUpdateTimetable() {
        final long moduleId = TestUtilities.insertContentValuesDirectlyInDb(mContext, ModuleEntry.TABLE_NAME, TestUtilities.createModuleValues());
        final long lecturerId = TestUtilities.insertContentValuesDirectlyInDb(mContext, LecturerEntry.TABLE_NAME, TestUtilities.createLecturerValues());
        ContentValues timetableValues = TestUtilities.createTimetableValues(moduleId, lecturerId);
        Uri timetableUri = mContext.getContentResolver().insert(TimetableEntry.CONTENT_URI, timetableValues);
        long timetableRowId = ContentUris.parseId(timetableUri);
        ContentValues updatedTimetableValues = new ContentValues(timetableValues);
        updatedTimetableValues.put(TimetableEntry._ID, timetableRowId);
        updatedTimetableValues.put(TimetableEntry.COLUMN_CHANGE_CODE, 3);
        testUpdate(timetableRowId, TimetableEntry._ID, TimetableEntry.CONTENT_URI, timetableValues, updatedTimetableValues);
    }

    private void testUpdate(long rowId, String columnId, Uri contentUri, ContentValues contentValues, ContentValues updatedValues) {
        assertTrue(rowId != -1);
        Cursor cursorWithObserver = mContext.getContentResolver().query(contentUri, null, null, null, null);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        cursorWithObserver.registerContentObserver(tco);
        int count = mContext.getContentResolver().update(contentUri, updatedValues, columnId + "= ?", new String[]{Long.toString(rowId)});
        assertEquals(count, 1);
        tco.waitForNotificationOrFail();
        cursorWithObserver.unregisterContentObserver(tco);
        cursorWithObserver.close();
        Cursor cursor = mContext.getContentResolver().query(contentUri, null, columnId + " = " + rowId, null, null);
        TestUtilities.validateCursor(cursor, updatedValues);
    }

    public void testInsertReadProvider() {
        ContentValues moduleValues = TestUtilities.createModuleValues();
        long moduleRowId = insertAndValidateValues(ModuleEntry.CONTENT_URI, moduleValues);

        ContentValues lecturerValues = TestUtilities.createLecturerValues();
        long lecturerRowId = insertAndValidateValues(LecturerEntry.CONTENT_URI, lecturerValues);

        ContentValues timetableValues = TestUtilities.createTimetableValues(moduleRowId, lecturerRowId);
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TimetableEntry.CONTENT_URI, true, tco);
        Uri timetableInsertUri = mContext.getContentResolver().insert(TimetableEntry.CONTENT_URI, timetableValues);
        assertTrue(timetableInsertUri != null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        Cursor timetableCursor = mContext.getContentResolver().query(TimetableEntry.CONTENT_URI, null, null, null, null);
        TestUtilities.validateCursor(timetableCursor, timetableValues);


        timetableCursor = mContext.getContentResolver().query(TimetableEntry.buildTimetableWithDateUri(TestUtilities.TEST_DATE), null, null, null, null);
        TestUtilities.validateCursor(timetableCursor, timetableValues);

        timetableValues.putAll(moduleValues);
        timetableCursor = mContext.getContentResolver().query(TimetableEntry.buildTimetableWithDateStartTimeModuleKeyUri(TestUtilities.TEST_DATE, TestUtilities.TEST_START_TIME, (int) moduleRowId), null, null, null, null);
        TestUtilities.validateCursor(timetableCursor, timetableValues);
    }

    private long insertAndValidateValues(Uri contentUri, ContentValues contentValues) {
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(contentUri, true, tco);
        Uri insertUri = mContext.getContentResolver().insert(contentUri, contentValues);

        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long rowId = ContentUris.parseId(insertUri);
        assertTrue(rowId != -1);

        Cursor cursor = mContext.getContentResolver().query(contentUri, null, null, null, null);
        TestUtilities.validateCursor(cursor, contentValues);
        return rowId;
    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        TestUtilities.TestContentObserver moduleObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ModuleEntry.CONTENT_URI, true, moduleObserver);

        TestUtilities.TestContentObserver lecturerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(LecturerEntry.CONTENT_URI, true, lecturerObserver);

        TestUtilities.TestContentObserver timetableObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TimetableEntry.CONTENT_URI, true, timetableObserver);

        deleteAllRecordsFromProvider();

        moduleObserver.waitForNotificationOrFail();
        lecturerObserver.waitForNotificationOrFail();
        timetableObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(moduleObserver);
        mContext.getContentResolver().unregisterContentObserver(lecturerObserver);
        mContext.getContentResolver().unregisterContentObserver(timetableObserver);
    }

}
