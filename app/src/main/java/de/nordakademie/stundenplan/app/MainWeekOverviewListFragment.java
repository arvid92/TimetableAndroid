package de.nordakademie.stundenplan.app;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;

import de.nordakademie.stundenplan.app.adapters.TimetableWeekAdapter;
import de.nordakademie.stundenplan.app.utils.PreferencesManager;

/**
 * Created by Daniel Bormann on 27.11.2016.
 */

public class MainWeekOverviewListFragment extends Fragment implements Updatable {

    private TimetableWeekAdapter mTimetableWeekAdapter;
    private MainWeekOverviewSwipeFragment mSwipeFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTimetableWeekAdapter =
                new TimetableWeekAdapter(
                        getActivity(), R.layout.list_item_days,
                        ((MainActivity)getActivity()).getmLongList());
        View rootView = inflater.inflate(R.layout.fragment_main_week_overview, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_timetable_week_list);
        listView.setAdapter(mTimetableWeekAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                view.setBackgroundColor(getResources().getColor(R.color.activated_item));
                mSwipeFragment = new MainWeekOverviewSwipeFragment();
                mSwipeFragment.setSelected(position);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.main_contentFragment, mSwipeFragment);
                transaction.commit();

            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.timetablefragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            ((MainActivity) getActivity()).updateTimetable();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update() {
        ArrayList<Long> list = ((MainActivity)getActivity()).getmLongList();
        mTimetableWeekAdapter.clear();
        if (list != null) {
            for (long l : list) {
                mTimetableWeekAdapter.insert(l, mTimetableWeekAdapter.getCount());
            }
        }
        mTimetableWeekAdapter.notifyDataSetChanged();
    }
}
