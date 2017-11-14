package de.nordakademie.stundenplan.app.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

public class TestTimetableContract extends AndroidTestCase {

    public void testBuildTimetableUris() {
        final long date = 12345678900L;
        final long startTime = 18840000;
        final int moduleKey = 4;

        final Uri uri1 = TimetableEntry.buildTimetableWithDateUri(date);
        assertEquals(Long.toString(date), TimetableEntry.getDateSettingFromUri(uri1));

        final Uri uri2 = TimetableEntry.buildTimetableWithDateStartTimeModuleKeyUri(date, startTime, moduleKey);
        assertEquals(Long.toString(date), TimetableEntry.getDateSettingFromUri(uri2));
        assertEquals(Long.toString(startTime), TimetableEntry.getStartTimeSettingFromUri(uri2));
        assertEquals(Integer.toString(moduleKey), TimetableEntry.getModuleKeySettingFromUri(uri2));
    }

    public void testBuildModuleUris() {
        final String moduleNumber = "I154";

        final Uri uri = ModuleEntry.buildModuleWithNumber(moduleNumber);
        assertEquals(moduleNumber, ModuleEntry.getModuleNumberSettingFromUri(uri));
    }

    public void testBuildLecturerUris() {
        final String firstNames = "Hans Peter";
        final String lastName = "Moor";

        final Uri uri = LecturerEntry.buildLecturerWithFirstAndLastNames(firstNames, lastName);
        assertEquals(firstNames, LecturerEntry.getFirstNamesFromUri(uri));
        assertEquals(lastName, LecturerEntry.getLastNameFromUri(uri));
    }
}
