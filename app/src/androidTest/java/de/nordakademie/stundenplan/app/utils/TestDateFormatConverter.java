package de.nordakademie.stundenplan.app.utils;

import android.test.AndroidTestCase;

import java.util.Date;

public class TestDateFormatConverter extends AndroidTestCase {

    private long millis1 = 0;
    private String time1 = "00:00";
    private String timeString1 = "Jan 01, 2016 00:00:00 AM";
    private Date date1 = new Date(116, 0, 1);

    private long millis2 = 18840000;
    private String time2 = "05:14";
    private String timeString2 = "Mar 10, 2016 05:14:00 AM";
    private Date date2 = new Date(116, 2, 10);

    private long millis3 = 48960000;
    private String time3 = "13:36";
    private String timeString3 = "Jun 22, 2016 1:36:00 PM";
    private Date date3 = new Date(116, 5, 22);

    private long millis4 = 86340000;
    private String time4 = "23:59";
    private String timeString4 = "Dec 31, 2017 11:59:00 PM";
    private Date date4 = new Date(117, 11, 31);

    public void testConvertMillisToTimeString() {
        assertEquals(time1, DateFormatConverter.convertMillisToTimeString(millis1));
        assertEquals(time2, DateFormatConverter.convertMillisToTimeString(millis2));
        assertEquals(time3, DateFormatConverter.convertMillisToTimeString(millis3));
        assertEquals(time4, DateFormatConverter.convertMillisToTimeString(millis4));
    }

    public void testConvertDateStringToTimeInMillis() throws Exception {
        assertEquals(millis1, DateFormatConverter.convertDateStringToTimeInMillis(timeString1));
        assertEquals(millis2, DateFormatConverter.convertDateStringToTimeInMillis(timeString2));
        assertEquals(millis3, DateFormatConverter.convertDateStringToTimeInMillis(timeString3));
        assertEquals(millis4, DateFormatConverter.convertDateStringToTimeInMillis(timeString4));
    }

    public void testConvertDateStringToDateInMillis() throws Exception {
        assertEquals(date1.getTime(), DateFormatConverter.convertDateStringToDateInMillis(timeString1));
        assertEquals(date2.getTime(), DateFormatConverter.convertDateStringToDateInMillis(timeString2));
        assertEquals(date3.getTime(), DateFormatConverter.convertDateStringToDateInMillis(timeString3));
        assertEquals(date4.getTime(), DateFormatConverter.convertDateStringToDateInMillis(timeString4));
    }

    public void testConvertDateInMillisToDate() {
        Date date11 = DateFormatConverter.convertDateInMillisToDate(date1.getTime());
        assertEquals(date1.getYear(), date11.getYear());
        assertEquals(date1.getMonth(), date11.getMonth());
        assertEquals(date1.getDate(), date11.getDate());

        Date date22 = DateFormatConverter.convertDateInMillisToDate(date2.getTime());
        assertEquals(date2.getYear(), date22.getYear());
        assertEquals(date2.getMonth(), date22.getMonth());
        assertEquals(date2.getDate(), date22.getDate());

        Date date33 = DateFormatConverter.convertDateInMillisToDate(date3.getTime());
        assertEquals(date3.getYear(), date33.getYear());
        assertEquals(date3.getMonth(), date33.getMonth());
        assertEquals(date3.getDate(), date33.getDate());

        Date date44 = DateFormatConverter.convertDateInMillisToDate(date4.getTime());
        assertEquals(date4.getYear(), date44.getYear());
        assertEquals(date4.getMonth(), date44.getMonth());
        assertEquals(date4.getDate(), date44.getDate());
    }

    public void testConvertToTwoDigits() {
        final String oneDigit = "5";
        final String twoDigits = "10";
        assertEquals("0"+oneDigit, DateFormatConverter.convertToTwoDigits(oneDigit));
        assertEquals(twoDigits, DateFormatConverter.convertToTwoDigits(twoDigits));
    }

}
