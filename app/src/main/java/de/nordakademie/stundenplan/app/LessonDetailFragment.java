package de.nordakademie.stundenplan.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import static de.nordakademie.stundenplan.app.utils.DateFormatConverter.convertToTwoDigits;

/**
 * Created by Leif Johannson on 27.11.2016.
 */

public class LessonDetailFragment extends Fragment implements Updatable {

    Cursor mCursor;
    View rootView;

    public LessonDetailFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCursor = ((LessonProvider) getActivity()).getSelectedLesson();

        rootView = inflater.inflate(R.layout.fragment_lesson_detail, container, false);
        if (mCursor != null) {
            ((TextView) rootView.findViewById(R.id.textView_lesson_title))
                    .setText(mCursor.getString(18) + " " + mCursor.getString(19));

            TextView textView = (TextView) rootView.findViewById(R.id.textView_lesson_changes);

            textView.setText(getChangeText(mCursor.getInt(8), (ImageView) rootView.findViewById(R.id.imageView_changed)));

            ((TextView) rootView.findViewById(R.id.textView_lesson_lecturer))
                    .setText(mCursor.getString(13) + " " + mCursor.getString(11) + " " + mCursor.getString(12));

            ((TextView) rootView.findViewById(R.id.textView_lesson_room))
                    .setText(mCursor.getString(3));

            ((TextView) rootView.findViewById(R.id.textView_lesson_time))
                    .setText(getTimeText(mCursor.getLong(6), mCursor.getLong(7)) + " (" +
                            mCursor.getInt(22) + " " + getString(R.string.teaching_unit) + ")");

            ((TextView) rootView.findViewById(R.id.textView_lesson_testForm))
                    .setText(getTestFormText(mCursor.getString(21)) + " (" + mCursor.getString(20) + " ECTs)");

        } else {
            throw new RuntimeException();
        }


        Button button = (Button) rootView.findViewById(R.id.button_lecturer_detail);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getActivity() instanceof LessonDetailActivity) {
                    ((LessonDetailActivity) getActivity()).setState(1);
                }


                LecturerDetailFragment fragment = new LecturerDetailFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.detail_contentFragment, fragment); //Container -> R.id.contentFragment
                transaction.commit();
            }
        });


        return rootView;
    }

    private String getTestFormText(String testForm) {
        String TEST_FORM;
        switch (testForm) {
            case "Klausur":
                TEST_FORM = getString(R.string.testform_exam);
                break;
            case "Vortrag":
                TEST_FORM = getString(R.string.testform_lecture);
                break;
            case "Hausarbeit":
                TEST_FORM = getString(R.string.testform_homework);
                break;
            case "Portfolio":
                TEST_FORM = getString(R.string.testform_portfolio);
                break;
            case "Projekt":
                TEST_FORM = getString(R.string.testform_project);
                break;
            default:
                TEST_FORM = testForm + ": " + getString(R.string.testform_invalid);
                break;
        }
        return TEST_FORM;
    }

    private String getTimeText(Long newStartTime, Long newEndTime) {
        Date startTime = new Date(newStartTime);
        Date endTime = new Date(newEndTime);
        String timeText;

        timeText = startTime.getHours() + ":" + convertToTwoDigits(Integer.toString(startTime.getMinutes()))
                + "-" + endTime.getHours() + ":" + convertToTwoDigits(Integer.toString(endTime.getMinutes()));

        return timeText;
    }

    private String getChangeText(int changeCode, ImageView changeView) {
        String HINT_TEXT = "";
        switch (changeCode) {
            case 0: //HINT_TEXT = getString(R.string.changeCode_0);
                changeView.setVisibility(View.GONE);
                break;
            case 1:
                HINT_TEXT = getString(R.string.changeCode_1);
                changeView.setImageResource(R.mipmap.ic_roomchange);
                break;
            case 2:
                HINT_TEXT = getString(R.string.changeCode_2);
                changeView.setImageResource(R.mipmap.ic_timechange);
                break;
            case 3:
                HINT_TEXT = getString(R.string.changeCode_3);
                changeView.setImageResource(R.mipmap.ic_timeandroomchange);
                break;
            case 4:
                HINT_TEXT = getString(R.string.changeCode_4);
                changeView.setImageResource(R.mipmap.ic_canceled);
                break;
            default:
                HINT_TEXT = getString(R.string.changeCode_invalid);
                changeView.setImageResource(R.mipmap.ic_changed);
                break;
        }
        return HINT_TEXT;
    }

    @Override
    public void update() {
        rootView.invalidate();
    }
}