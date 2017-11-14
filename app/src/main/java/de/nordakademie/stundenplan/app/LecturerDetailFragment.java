package de.nordakademie.stundenplan.app;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Daniel Bormann (14150)
 *         <p>
 *         The fragment for details about the lecturer.
 */

public class LecturerDetailFragment extends Fragment implements Updatable {
    Cursor mCursor;
    View rootView;

    public LecturerDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mCursor = ((LessonProvider) getActivity()).getSelectedLesson();
        rootView = inflater.inflate(R.layout.fragment_lecturer_detail, container, false);
        if (mCursor != null) {
            ((TextView) rootView.findViewById(R.id.textview_lecturer_name))
                    .setText(mCursor.getString(13) + " " + mCursor.getString(11) + " " + mCursor.getString(12));
            if (mCursor.getString(14) != null) {
                rootView.findViewById(R.id.imageView_room).setVisibility(View.VISIBLE);
                ((TextView) rootView.findViewById(R.id.textview_lecturer_office))
                        .setText(mCursor.getString(14));
            }
            if (mCursor.getString(15) != null) {
                rootView.findViewById(R.id.imageView_phone).setVisibility(View.VISIBLE);
                TextView textview = (TextView) rootView.findViewById(R.id.textview_lecturer_phone);
                textview.setText(mCursor.getString(15));
                textview.setTextColor(Color.CYAN);
                textview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mCursor.getString(15), null));
                        startActivity(intent);
                    }
                });
            }
            if (mCursor.getString(16) != null) {
                rootView.findViewById(R.id.imageView_mail).setVisibility(View.VISIBLE);
                TextView textView = (TextView) rootView.findViewById(R.id.textview_lecturer_mail);
                textView.setText(mCursor.getString(16));
                textView.setTextColor(Color.CYAN);
                textView.setOnClickListener(new MailOnClickListener());
            }
        } else {
            throw new RuntimeException();
        }
        return rootView;
    }

    @Override
    public void update() {
        rootView.invalidate();
    }

    private class MailOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View viewIn) {
            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("vnd.android.cursor.item/email");
            intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{mCursor.getString(16)});
            startActivity(Intent.createChooser(intent, getString(R.string.lecturer_mail_send_text)));
        }
    }
}
