package me.architetto.fwfortress.util;

import me.architetto.fwfortress.config.SettingsHandler;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

    public static boolean invadeTimeCheck() {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Rome"));
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        return settingsHandler.getDate().contains(dayName)
                && dateTime.getHour() >= settingsHandler.getTime().get(0)
                && dateTime.getHour() < settingsHandler.getTime().get(1);
    }

    public static String formatSeconds(int s) {
        return String.format("%d:%d",
                TimeUnit.SECONDS.toMinutes(s),
                s - (TimeUnit.SECONDS.toMinutes(s) * 60));
    }
}
