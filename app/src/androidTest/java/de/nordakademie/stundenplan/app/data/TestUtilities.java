package de.nordakademie.stundenplan.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

import de.nordakademie.stundenplan.app.utils.PollingCheck;

import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

public class TestUtilities extends AndroidTestCase {

    static long TEST_DATE = 9876540000L;
    static long TEST_START_TIME = 48960000;

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {
        assertTrue(valueCursor.moveToFirst());
        compareCursorToContentValues(valueCursor, expectedValues);
        valueCursor.close();
    }

    static void compareCursorToContentValues(Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createTimetableValues(long moduleId, long lecturerId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TimetableEntry.COLUMN_ROOM, "A001");
        contentValues.put(TimetableEntry.COLUMN_CENTURY, "I14a");
        contentValues.put(TimetableEntry.COLUMN_DATE, TEST_DATE);
        contentValues.put(TimetableEntry.COLUMN_START_TIME, TEST_START_TIME);
        contentValues.put(TimetableEntry.COLUMN_END_TIME, 86340000);
        contentValues.put(TimetableEntry.COLUMN_CHANGE_CODE, 1);
        contentValues.put(TimetableEntry.COLUMN_CALENDAR_WEEK, 47);
        contentValues.put(TimetableEntry.COLUMN_MODULE_KEY, moduleId);
        contentValues.put(TimetableEntry.COLUMN_LECTURER_KEY, lecturerId);
        return contentValues;
    }

    static ContentValues createModuleValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ModuleEntry.COLUMN_NUMBER, "I154");
        contentValues.put(ModuleEntry.COLUMN_NAME, "Logistik");
        contentValues.put(ModuleEntry.COLUMN_ECTS, 5);
        contentValues.put(ModuleEntry.COLUMN_EXAM_TYPE, "Klausur");
        contentValues.put(ModuleEntry.COLUMN_HOURS, 4);
        return contentValues;
    }

    static ContentValues createLecturerValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(LecturerEntry.COLUMN_FIRST_NAMES, "Hans Peter");
        contentValues.put(LecturerEntry.COLUMN_LAST_NAME, "Janus");
        contentValues.put(LecturerEntry.COLUMN_TITLE, "Prof. Dr. hab.");
        contentValues.put(LecturerEntry.COLUMN_OFFICE, "D107");
        contentValues.put(LecturerEntry.COLUMN_TELEPHONE, "04029678934");
        contentValues.put(LecturerEntry.COLUMN_EMAIL, "hans-peter.janus@gmx.de");
        return contentValues;
    }

    static long insertContentValuesDirectlyInDb(Context context, String tableName, ContentValues contentValues) {
        TimetableDbHelper dbHelper = new TimetableDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(tableName, null, contentValues);
        assertTrue(rowId != -1);
        return rowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
