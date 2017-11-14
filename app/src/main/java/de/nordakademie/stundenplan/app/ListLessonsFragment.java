package de.nordakademie.stundenplan.app;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import de.nordakademie.stundenplan.app.adapters.TimetableDayAdapter;
import de.nordakademie.stundenplan.app.data.TimetableContract;

/**
 * @author Daniel Bormann (14150)
 *         <p>
 *         Fragment with inherited ListView, provides all Lessons of a specific date.
 */


public class ListLessonsFragment extends Fragment implements Updatable {

    private TimetableDayAdapter mTimetableDayAdapter;
    private long date;
    private Cursor cursor;

    public ListLessonsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cursor = getContext().getContentResolver().query(
                TimetableContract.TimetableEntry.buildTimetableWithDateUri(date), null
                , null, null, null);
        mTimetableDayAdapter =
                new TimetableDayAdapter(
                        getActivity(),
                        cursor, false);

        View rootView = inflater.inflate(R.layout.fragment_list_lessons, container, false);


        final ListView listView = (ListView) rootView.findViewById(R.id.listview_list_lessons);
        listView.setAdapter(mTimetableDayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //view.setBackgroundColor(getResources().getColor(R.color.selected_item));
                cursor.moveToPosition(position);
                ((MainActivity) getActivity()).openDetailFragment(cursor.getLong(5), cursor.getLong(6), cursor.getInt(17));
            }
        });
        return rootView;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDate() {
        return date;
    }

    @Override
    public void update() {
        mTimetableDayAdapter.notifyDataSetChanged();
    }
}

