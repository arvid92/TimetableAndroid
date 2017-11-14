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

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * In der TimetableContract-Klasse werden Konstanten für die verschiedenen Datenbanktabellen
 * definiert. Dazu zählen z.B. die Tabellen- und Spaltennamen. Des weiteren werden Methoden für
 * die Erzeugung von Uris und zum Extrahieren von Informationen aus Uris zur Verfügung gestellt.
 */
public class TimetableContract {

    static final String CONTENT_AUTHORITY = "de.nordakademie.stundenplan.app";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_TIMETABLE = "timetable";
    static final String PATH_LECTURER = "lecturer";
    static final String PATH_MODULE = "module";

    public static final class LecturerEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LECTURER).build();
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LECTURER;
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LECTURER;

        // Name der Datenbanktabelle
        static final String TABLE_NAME = "lecturer";

        // Spaltennamen der Datenbanktabelle
        public static final String COLUMN_FIRST_NAMES = "first_names";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OFFICE = "office";
        public static final String COLUMN_TELEPHONE = "telephone";
        public static final String COLUMN_EMAIL = "email";

        static Uri buildLecturerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        static Uri buildLecturerWithFirstAndLastNames(String firstNames, String lastName) {
            return CONTENT_URI.buildUpon().appendPath(firstNames).appendPath(lastName).build();
        }

        static String getFirstNamesFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        static String getLastNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    public static final class ModuleEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MODULE).build();
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MODULE;
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MODULE;

        // Name der Datenbanktabelle
        static final String TABLE_NAME = "module";

        // Spaltennamen der Datenbanktabelle
        public static final String COLUMN_NUMBER = "number";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_ECTS = "ects";
        public static final String COLUMN_EXAM_TYPE = "exam_type";
        public static final String COLUMN_HOURS = "hours";

        static Uri buildModuleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        static Uri buildModuleWithNumber(String moduleNumber) {
            return CONTENT_URI.buildUpon().appendPath(moduleNumber).build();
        }

        static String getModuleNumberSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class TimetableEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TIMETABLE).build();
        static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMETABLE;
        static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TIMETABLE;

        // Name der Datenbanktabelle
        static final String TABLE_NAME = "timetable";

        // Spaltennamen der Datenbanktabelle
        public static final String COLUMN_LECTURER_KEY = "lecturer_id";
        public static final String COLUMN_MODULE_KEY = "module_id";
        public static final String COLUMN_ROOM = "room";
        public static final String COLUMN_CENTURY = "century";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_CHANGE_CODE = "change_code";
        public static final String COLUMN_CALENDAR_WEEK = "calendar_week";

        static Uri buildTimetableUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildTimetableWithDateUri(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(date)).build();
        }

        public static Uri buildTimetableWithDateStartTimeModuleKeyUri(long date, long startTime, int moduleKey) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .appendPath(Long.toString(startTime))
                    .appendPath(Integer.toString(moduleKey))
                    .build();
        }

        static String getDateSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        static String getStartTimeSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        static String getModuleKeySettingFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
    }
}
