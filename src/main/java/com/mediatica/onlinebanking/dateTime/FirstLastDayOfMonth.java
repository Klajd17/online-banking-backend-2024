package com.mediatica.onlinebanking.dateTime;
import java.sql.Timestamp;
import java.util.Calendar;

public class FirstLastDayOfMonth {

    public static Timestamp getFirstDayOfMonth(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setToBeginningOfDay(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static Timestamp getLastDayOfMonth(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setToEndOfDay(calendar);
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static void setToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static void setToEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }
}
