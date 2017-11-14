package de.nordakademie.stundenplan.app.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.nordakademie.stundenplan.app.R;


public class DateFormatConverter {

    private static Date getDateFromDateString(String dateString) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("MMM d, yyyy h:m:s a", Locale.ENGLISH);
        return formatter.parse(dateString);
    }

    /**
     * Konvertiert das Datum aus einem Zeit-String in Millisekunden.
     *
     * @param dateString der Zeit-String, aus dem das Datum konvertiert werden soll
     * @return das Datum in Millisekunden
     * @throws ParseException wenn der Zeit-String nicht vom Format MMM d, yyyy h:m:s a ist
     */
    public static long convertDateStringToDateInMillis(String dateString) throws ParseException {
        Date date = getDateFromDateString(dateString);
        Date dateOnly = new Date(date.getYear(), date.getMonth(), date.getDate());
        return dateOnly.getTime();
    }

    /**
     * Konvertiert Millisekunden in ein Datum vom Typ Date.
     *
     * @param millis die in ein Date zu konvertierenden Millisekunden
     * @return das Date, welches das Datum repäsentiert
     */
    public static Date convertDateInMillisToDate(long millis) {
        return new Date(millis);
    }

    /**
     * Konvertiert die Uhrzeit aus einem Zeit-String in Millisekunden.
     *
     * @param dateString der Zeit-String, aus dem die Uhrzeit konvertiert werden soll
     * @return die Uhrzeit in Millisekunden
     * @throws ParseException wenn der Zeit-String nicht vom Format MMM d, yyyy h:m:s a ist
     */
    public static long convertDateStringToTimeInMillis(String dateString) throws ParseException {
        Date date = getDateFromDateString(dateString);
        int hours = date.getHours();
        int minutes = date.getMinutes();
        return (long) (hours * 60 * 60 * 1000) + (minutes * 60 * 1000);
    }

    /**
     * Konvertiert Millisekunden in einen String vom Format hh:mm.
     *
     * @param millis die zu konvertierenden Millisekunden
     * @return der Zeit-String (z.B. 05:14)
     */
    public static String convertMillisToTimeString(long millis) {
        String minutes = Long.toString((millis / (1000 * 60)) % 60);
        String hours = Long.toString((millis / (1000 * 60)) / 60);
        String timeString = ((hours.length() < 2) ? "0" + hours : hours) + ":" + ((minutes.length() < 2) ? "0" + minutes : minutes);
        return timeString;
    }

    /**
     * Konvertiert eine einstellige Minutenzahl in eine zweistellige, sofern sie noch nicht
     * zweistellig ist. Beispiel: 5 -> 05, 10 -> 10
     *
     * @param minutes de rzu konvertierende String
     * @return der konvertierte String
     */
    public static String convertToTwoDigits(String minutes) {
        if (minutes.length() == 1) {
            return "0" + minutes;
        }
        return minutes;
    }

    public static String getDayName(Date date, Context c){
        switch (date.getDay()){
            case 0: return c.getString(R.string.week_sunday);
            case 1: return c.getString(R.string.week_monday);
            case 2: return c.getString(R.string.week_tuesday);
            case 3: return c.getString(R.string.week_wednesday);
            case 4: return c.getString(R.string.week_thursday);
            case 5: return c.getString(R.string.week_friday);
            case 6: return c.getString(R.string.week_saturday);
        }
        return "";
    }

}
