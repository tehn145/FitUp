package com.example.fitup;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    public static String format(long timestamp) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(timestamp));
    }

    public static String formatDateHeader(long timestamp) {
        Calendar now = Calendar.getInstance();
        Calendar msgTime = Calendar.getInstance();
        msgTime.setTimeInMillis(timestamp);

        now.set(Calendar.HOUR_OF_DAY, 0); now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0); now.set(Calendar.MILLISECOND, 0);

        msgTime.set(Calendar.HOUR_OF_DAY, 0); msgTime.set(Calendar.MINUTE, 0);
        msgTime.set(Calendar.SECOND, 0); msgTime.set(Calendar.MILLISECOND, 0);

        long diff = now.getTimeInMillis() - msgTime.getTimeInMillis();
        long oneDay = 24 * 60 * 60 * 1000;

        if (diff == 0) return "Today";
        if (diff == oneDay) return "Yesterday";
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(timestamp));
    }

    public static String formatFullDate(long timestamp) {
        return new SimpleDateFormat("MM/dd/yy, h:mm a", Locale.getDefault()).format(new Date(timestamp));
    }
}