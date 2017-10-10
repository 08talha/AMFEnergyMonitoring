package com.example.talha.amfenergymonitoring;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by talha on 19.05.2017.
 */

public class TarihFarki {
    public int getNumSaatBetween(String dateStart, String dateEnd){

        int numDays = 0;

        try{

            SimpleDateFormat formatYmd = new SimpleDateFormat("hh:mm:ss");

            Date d1 = formatYmd.parse(dateStart);
            Date d2 = formatYmd.parse(dateEnd);

            DateTime dt1 = new DateTime(d1);
            DateTime dt2 = new DateTime(d2);

            numDays = Hours.hoursBetween(dt1, dt2).getHours();

            // Days between 2013-09-01 and 2013-09-02: 1

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return numDays;
    }
    public int getNumDakkaBetween(String dateStart, String dateEnd){

        int numDays = 0;

        try{

            SimpleDateFormat formatYmd = new SimpleDateFormat("hh:mm:ss");

            Date d1 = formatYmd.parse(dateStart);
            Date d2 = formatYmd.parse(dateEnd);

            DateTime dt1 = new DateTime(d1);
            DateTime dt2 = new DateTime(d2);

            numDays = Minutes.minutesBetween(dt1, dt2).getMinutes();

            // Days between 2013-09-01 and 2013-09-02: 1

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return numDays;
    }

    public int getNumDaysBetween(String dateStart, String dateEnd){

        int numDays = 0;

        try{

            SimpleDateFormat formatYmd = new SimpleDateFormat("yyyy-MM-dd");

            Date d1 = formatYmd.parse(dateStart);
            Date d2 = formatYmd.parse(dateEnd);

            DateTime dt1 = new DateTime(d1);
            DateTime dt2 = new DateTime(d2);

            numDays = Days.daysBetween(dt1, dt2).getDays();

            // Days between 2013-09-01 and 2013-09-02: 1

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return numDays;
    }
    public int getNumMountBetween(String dateStart, String dateEnd){

        int numDays = 0;

        try{

            SimpleDateFormat formatYmd = new SimpleDateFormat("yyyy-MM-dd");

            Date d1 = formatYmd.parse(dateStart);
            Date d2 = formatYmd.parse(dateEnd);

            DateTime dt1 = new DateTime(d1);
            DateTime dt2 = new DateTime(d2);

            numDays = Months.monthsBetween(dt1, dt2).getMonths();

            // Days between 2013-09-01 and 2013-09-02: 1

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return numDays;
    }
}
