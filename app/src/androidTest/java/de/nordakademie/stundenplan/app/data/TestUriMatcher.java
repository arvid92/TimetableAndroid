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

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import static de.nordakademie.stundenplan.app.data.TimetableContract.LecturerEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.ModuleEntry;
import static de.nordakademie.stundenplan.app.data.TimetableContract.TimetableEntry;

public class TestUriMatcher extends AndroidTestCase {
    private static final long DATE = 14190330000L;
    private static final long START_TIME = 18840000;
    private static final int MODULE_KEY = 4;
    private static final Uri TEST_TIMETABLE_DIR = TimetableEntry.CONTENT_URI;
    private static final Uri TEST_TIMETABLE_WITH_DATE_DIR = TimetableEntry.buildTimetableWithDateUri(DATE);
    private static final Uri TEST_TIMETABLE_WITH_DATE_AND_START_TIME_AND_MODULE_KEY_DIR = TimetableEntry.buildTimetableWithDateStartTimeModuleKeyUri(DATE, START_TIME, MODULE_KEY);

    private static final String FIRST_NAMES = "Hans Peter";
    private static final String LAST_NAME = "Moor";
    private static final Uri TEST_LECTURER_DIR = LecturerEntry.CONTENT_URI;
    private static final Uri TEST_LECTURER_WITH_FIRST_NAMES_AND_LAST_NAME_DIR = LecturerEntry.buildLecturerWithFirstAndLastNames(FIRST_NAMES, LAST_NAME);

    private static final String MODULE_NUMBER = "I154";
    private static final Uri TEST_MODULE_DIR = ModuleEntry.CONTENT_URI;
    private static final Uri TEST_MODULE_WITH_NUMBER_DIR = ModuleEntry.buildModuleWithNumber(MODULE_NUMBER);

    public void testUriMatcher() {
        UriMatcher matcher = TimetableProvider.buildUriMatcher();
        assertEquals(TimetableProvider.TIMETABLE, matcher.match(TEST_TIMETABLE_DIR));
        assertEquals(TimetableProvider.TIMETABLE_WITH_DATE, matcher.match(TEST_TIMETABLE_WITH_DATE_DIR));
        assertEquals(TimetableProvider.TIMETABLE_WITH_DATE_AND_START_TIME_AND_MODULE_KEY, matcher.match(TEST_TIMETABLE_WITH_DATE_AND_START_TIME_AND_MODULE_KEY_DIR));
        assertEquals(TimetableProvider.LECTURER, matcher.match(TEST_LECTURER_DIR));
        assertEquals(TimetableProvider.LECTURER_WITH_FIRST_AND_LAST_NAMES, matcher.match(TEST_LECTURER_WITH_FIRST_NAMES_AND_LAST_NAME_DIR));
        assertEquals(TimetableProvider.MODULE, matcher.match(TEST_MODULE_DIR));
        assertEquals(TimetableProvider.MODULE_WITH_NUMBER, matcher.match(TEST_MODULE_WITH_NUMBER_DIR));
    }
}
