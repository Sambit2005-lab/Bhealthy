package com.codexnovas.bhealthy.HomeFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class SunriseSunsetCalculator {

    // Constants
    private static final double ZENITH = 90.833; // Zenith for sunrise and sunset

    public static Calendar getSunrise(Calendar date, double latitude, double longitude) {
        return getSunEvent(date, latitude, longitude, true);
    }

    public static Calendar getSunset(Calendar date, double latitude, double longitude) {
        return getSunEvent(date, latitude, longitude, false);
    }

    private static Calendar getSunEvent(Calendar date, double latitude, double longitude, boolean isSunrise) {
        double latitudeRad = Math.toRadians(latitude);
        double longitudeRad = Math.toRadians(longitude);
        double dayOfYear = date.get(Calendar.DAY_OF_YEAR);

        // Calculate the solar declination
        double declination = 23.44 * Math.sin(Math.toRadians((360 / 365) * (dayOfYear - 81)));

        // Calculate the hour angle
        double hourAngle = Math.acos(Math.cos(Math.toRadians(ZENITH)) /
                (Math.cos(latitudeRad) * Math.cos(Math.toRadians(declination))) -
                Math.tan(latitudeRad) * Math.tan(Math.toRadians(declination)));

        // Calculate the solar noon
        double solarNoon = 12 - ((longitude / 15) - 0.5);

        // Calculate the sunrise or sunset time
        double eventTime = isSunrise ? (solarNoon - hourAngle * 12 / Math.PI) : (solarNoon + hourAngle * 12 / Math.PI);

        // Set the time in Calendar object
        Calendar eventCalendar = (Calendar) date.clone();
        eventCalendar.set(Calendar.HOUR_OF_DAY, (int) eventTime);
        eventCalendar.set(Calendar.MINUTE, (int) ((eventTime - (int) eventTime) * 60));
        eventCalendar.set(Calendar.SECOND, 0);

        return eventCalendar;
    }
}
