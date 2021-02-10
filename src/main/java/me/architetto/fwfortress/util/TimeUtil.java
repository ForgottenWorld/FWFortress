package me.architetto.fwfortress.util;

import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

public class TimeUtil {

    public static boolean invadeTimeCheck() {
        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/London"));
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        return settingsHandler.getDate().contains(dayName)
                && dateTime.getHour() > settingsHandler.getTime().get(0)
                && dateTime.getHour() < settingsHandler.getTime().get(1);
    }

    public static boolean buildableTimeCheck(Fortress fortress) {
        return (System.currentTimeMillis() - fortress.getCreationDate()) > SettingsHandler.getInstance().getBuildableCooldown();
    }
}
