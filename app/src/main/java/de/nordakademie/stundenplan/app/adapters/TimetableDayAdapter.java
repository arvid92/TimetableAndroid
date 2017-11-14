package de.nordakademie.stundenplan.app.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import de.nordakademie.stundenplan.app.R;

import static de.nordakademie.stundenplan.app.utils.DateFormatConverter.*;


/**
 * @author Daniel Bormann (14150)
 *
 * Provides the content of the ListView for a single day.
 */

public class TimetableDayAdapter extends CursorAdapter {
    private int size;


    public TimetableDayAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.size = c.getCount();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_timetable, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textView = (TextView) view.findViewById(R.id.list_item_timetable_title_textview);
        textView.setText(cursor.getString(19));
        textView = (TextView) view.findViewById(R.id.list_item_timetable_duration_textview);
        Date startDate = new Date(cursor.getLong(6));
        Date endDate = new Date(cursor.getLong(7));
        String dateString = startDate.getHours() + ":" +
                convertToTwoDigits(Integer.toString(startDate.getMinutes())) + " - " +
                endDate.getHours() + ":" +
                convertToTwoDigits(Integer.toString(endDate.getMinutes()));
        textView.setText(dateString
                        );

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView_changed);
        switch(cursor.getInt(8)) {
            case 0: break;
            case 1: imageView.setImageResource(R.mipmap.ic_roomchange);
                break;
            case 2: imageView.setImageResource(R.mipmap.ic_timechange);
                break;
            case 3: imageView.setImageResource(R.mipmap.ic_timeandroomchange);
                break;
            case 4: imageView.setImageResource(R.mipmap.ic_canceled);
                break;
        }
        imageView.setVisibility(View.VISIBLE);
    }
    @Override
    public int getCount(){
        return size;
    }
}