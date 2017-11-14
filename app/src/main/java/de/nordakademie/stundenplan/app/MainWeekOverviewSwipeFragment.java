package de.nordakademie.stundenplan.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

import de.nordakademie.stundenplan.app.adapters.SwipePagerAdapter;
import de.nordakademie.stundenplan.app.utils.DateFormatConverter;

import static de.nordakademie.stundenplan.app.utils.DateFormatConverter.convertToTwoDigits;

/**
 * Created by Daniel Bormann on 27.11.2016.
 */

public class MainWeekOverviewSwipeFragment extends Fragment implements Updatable {
    private SwipePagerAdapter mSwipePagerAdapter;
    private ViewPager mViewPager;
    private TextView mTitle;
    private int selected;

    public MainWeekOverviewSwipeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mSwipePagerAdapter =
                new SwipePagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        View rootView = inflater.inflate(R.layout.fragment_swipe_days, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.textView_list_title);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager_swipe_days);
        mViewPager.setAdapter(mSwipePagerAdapter);
        mViewPager.setCurrentItem(selected);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int i, final float v, final int i2) {
            }

            @Override
            public void onPageSelected(final int i) {
                setmTitle();
            }

            @Override
            public void onPageScrollStateChanged(final int i) {
            }
        });
        setmTitle();
        ((MainActivity) getActivity()).displayAlertOnVersionChange(getString(R.string.new_stuff_label), getString(R.string.new_stuff));
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

    private void setmTitle() {
        Date date = new Date(((MainActivity) getActivity()).getmLongList().get(mViewPager.getCurrentItem()));
        mTitle.setText(DateFormatConverter.getDayName(date, getContext()) + ", " + convertToTwoDigits(Integer.toString(date.getDate())) + "." + convertToTwoDigits(Integer.toString(date.getMonth() + 1)) + "." + (date.getYear() + 1900));
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    @Override
    public void update() {
        mSwipePagerAdapter.notifyDataSetChanged();
        setmTitle();
    }
}




