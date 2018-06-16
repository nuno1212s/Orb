package com.nuno1212s.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.TimeUnit;

/**
 * New time util
 */
@AllArgsConstructor
public class TimeUtil {

    String pattern;

    @Getter()
    boolean useYears, useMonths, useWeeks, useDays;

    public TimeUtil(String pattern) {
        this.pattern = pattern;

        useYears = pattern.contains("YYYY");
        useMonths = pattern.contains("mm");
        useWeeks = pattern.contains("WW");
        useDays = pattern.contains("DD");

    }

    public String toTime(long time) {
        long years = 0, months = 0, weeks = 0, days = 0, hours, minutes, seconds;

        if (useYears) {
            years = (long) Math.floor((double) TimeUnit.MILLISECONDS.toDays(time) / 365);
            time -= TimeUnit.DAYS.toMillis(years * 365);
        }

        if (useMonths) {
            months = (long) Math.floor((double) TimeUnit.MILLISECONDS.toDays(time) / 30);
            time -= TimeUnit.DAYS.toMillis(months * 30);
        }

        if (useWeeks) {
            weeks = (long) Math.floor((double) TimeUnit.MILLISECONDS.toDays(time) / 7);
            time -= TimeUnit.DAYS.toMillis(weeks * 7);
        }

        if (useDays) {
            days = TimeUnit.MILLISECONDS.toDays(time);
            time -= TimeUnit.DAYS.toMillis(days);
        }

        hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);

        minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);

        seconds = TimeUnit.MILLISECONDS.toSeconds(time);

        String b = this.pattern;

        if (useYears) {
            b = String.format(b.replace("YYYY", "%04d"), years);
        }

        if (useMonths) {
            b = String.format(b.replace("mm", "%02d"), months);
        }

        if (useWeeks) {
            b = String.format(b.replace("WW", "%02d"), weeks);
        }

        if (useDays) {
            b = String.format(b.replace("DD", "%02d"), days);
        }

        return String.format(b.replace("HH", "%02d").replace("MM", "%02d").replace("SS", "%02d"), hours, minutes, seconds);
    }

}
