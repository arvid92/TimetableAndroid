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
package de.nordakademie.stundenplan.app;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import de.nordakademie.stundenplan.app.data.TestDb;
import de.nordakademie.stundenplan.app.data.TestProvider;
import de.nordakademie.stundenplan.app.data.TestTimetableContract;
import de.nordakademie.stundenplan.app.data.TestUriMatcher;
import de.nordakademie.stundenplan.app.utils.TestDateFormatConverter;

@RunWith(Suite.class)
@Suite.SuiteClasses({TestDb.class, TestProvider.class, TestTimetableContract.class, TestUriMatcher.class, TestDateFormatConverter.class})
public class FullTestSuite {
}
