package de.nordakademie.stundenplan.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import de.nordakademie.stundenplan.app.R;
import de.nordakademie.stundenplan.app.utils.DateFormatConverter;

import static de.nordakademie.stundenplan.app.utils.DateFormatConverter.convertToTwoDigits;

/**
 * @author Daniel Bormann (14150)
 *
 * Provides the content of the ListView for a single week.
 */

public class TimetableWeekAdapter extends ArrayAdapter<Long> {

    public TimetableWeekAdapter(Context context, int resource, List<Long> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_days, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.list_item_days_textview);
        Date date = new Date(getItem(position));
        textView.setText(DateFormatConverter.getDayName(date, getContext()) + ", " + convertToTwoDigits(Integer.toString(date.getDate())) + "." + convertToTwoDigits(Integer.toString(date.getMonth()+1)) + "." + (date.getYear()+ 1900));
        textView.setHeight(parent.getHeight()/getCount());
        return convertView;
    }
}
