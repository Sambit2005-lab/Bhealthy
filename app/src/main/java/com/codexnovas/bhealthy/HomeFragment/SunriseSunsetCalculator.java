package com.codexnovas.bhealthy.HomeFragment;


import java.util.Calendar;
import java.util.TimeZone;

public class SunriseSunsetCalculator {

    private static final double ZENITH = 90.833; // Zenith for sunrise and sunset

    public static Calendar getSunrise(Calendar date, double latitude, double longitude, TimeZone timeZone) {
        return getSunEvent(date, latitude, longitude, true, timeZone);
    }

    public static Calendar getSunset(Calendar date, double latitude, double longitude, TimeZone timeZone) {
        return getSunEvent(date, latitude, longitude, false, timeZone);
    }

    private static Calendar getSunEvent(Calendar date, double latitude, double longitude, boolean isSunrise, TimeZone timeZone) {
        double latitudeRad = Math.toRadians(latitude);
        double longitudeRad = Math.toRadians(longitude);
        double dayOfYear = date.get(Calendar.DAY_OF_YEAR);

        double declination = 23.44 * Math.sin(Math.toRadians((360 / 365.0) * (dayOfYear - 81)));

        double hourAngle = Math.acos(Math.cos(Math.toRadians(ZENITH)) /
                (Math.cos(latitudeRad) * Math.cos(Math.toRadians(declination))) -
                Math.tan(latitudeRad) * Math.tan(Math.toRadians(declination)));

        double solarNoon = 12 - ((longitude / 15.0) - (timeZone.getOffset(date.getTimeInMillis()) / 3600000.0));

        double eventTime = isSunrise ? (solarNoon - hourAngle * 12 / Math.PI) : (solarNoon + hourAngle * 12 / Math.PI);

        Calendar eventCalendar = (Calendar) date.clone();
        eventCalendar.setTimeZone(timeZone);
        eventCalendar.set(Calendar.HOUR_OF_DAY, (int) eventTime);
        eventCalendar.set(Calendar.MINUTE, (int) ((eventTime - (int) eventTime) * 60));
        eventCalendar.set(Calendar.SECOND, 0);

        return eventCalendar;
    }
}