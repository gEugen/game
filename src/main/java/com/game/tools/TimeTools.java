package com.game.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeTools {
    public static Date getDateFromString(String dateInString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date;
        try {
            date = formatter.parse(dateInString);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return date;
    }

    public static Date getDateFromMs(long dateLimit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateLimit);
        try {
            return getDateWithoutTimeUsingFormat(calendar.getTime());

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date getDateWithoutTimeUsingFormat(Date time) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd");

        return formatter.parse(formatter.format(time));
    }
}

