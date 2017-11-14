package de.nordakademie.stundenplan.app.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import de.nordakademie.stundenplan.app.ListLessonsFragment;
import de.nordakademie.stundenplan.app.MainActivity;

/**
 * @author Daniel Bormann (14150)
 *
 * Provides the content of the ViewPager.
 */

public class SwipePagerAdapter extends FragmentStatePagerAdapter {

    Context context;

    public SwipePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        ListLessonsFragment fragment = new ListLessonsFragment();
        fragment.setDate(((MainActivity) context).getmLongList().get(i));
        return fragment;
    }

    @Override
    public int getCount() {
        return ((MainActivity) context).getmLongList().size();
    }

    @Override
    public int getItemPosition(Object object) {
        ListLessonsFragment fragment = (ListLessonsFragment) object;
        Long date = fragment.getDate();
        int position = ((MainActivity) context).getmLongList().indexOf(date);
        if (position >= 0) {
            return position;
        } else {
            return POSITION_NONE;
        }

    }
}
