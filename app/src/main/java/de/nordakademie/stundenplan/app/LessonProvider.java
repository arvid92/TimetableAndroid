package de.nordakademie.stundenplan.app;

import android.database.Cursor;

/**
 * @author Daniel Bormann (14150)
 *         <p>
 *         Package-Protected Interface meant for Activities.
 *         Used to provide the selected Lesson as a Cursor.
 */

interface LessonProvider {
    Cursor getSelectedLesson();
}
