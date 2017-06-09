package me.joeleoli.practice.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    private static DateFormat MATCH_DATE_FORMAT = new SimpleDateFormat("mm:ss");

    private static int adjustTime(long timestamp, TimeUnit from, TimeUnit to) {
        return (int) to.convert(timestamp, from);
    }

    public static String formatElapsingNanoseconds(long timestamp) {
        timestamp = System.nanoTime() - timestamp;

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.SECOND, adjustTime(timestamp, TimeUnit.NANOSECONDS, TimeUnit.SECONDS));
        timestamp -= TimeUnit.SECONDS.toNanos(TimeUnit.NANOSECONDS.toSeconds(timestamp));

        cal.set(Calendar.MINUTE, adjustTime(timestamp, TimeUnit.NANOSECONDS, TimeUnit.MINUTES));
        timestamp -= TimeUnit.MINUTES.toNanos(TimeUnit.NANOSECONDS.toMinutes(timestamp));

        cal.set(Calendar.HOUR_OF_DAY, adjustTime(timestamp, TimeUnit.NANOSECONDS, TimeUnit.HOURS));

        return MATCH_DATE_FORMAT.format(cal.getTime());
    }

    public static String formatSeconds(int seconds) {
        int minutes = seconds / 60;

        if (minutes == 0) {
            return seconds + " seconds";
        }

        seconds %= 60;

        return minutes + " minutes and " + seconds + " seconds";
    }

}