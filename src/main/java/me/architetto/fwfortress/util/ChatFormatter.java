package me.architetto.fwfortress.util;

import org.bukkit.ChatColor;

public class ChatFormatter {

    public static String formatSuccessMessage(String message) {
        message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FW" + ChatColor.RESET  + "Fortress" + ChatColor.GRAY  +
                " ||> " + ChatColor.GREEN + message + ChatColor.RESET;
        return message;
    }

    public static String formatErrorMessage(String message) {
        message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FW" + ChatColor.RESET  + "Fortress" + ChatColor.GRAY  +
                " ||> " + ChatColor.RED + message + ChatColor.RESET;
        return message;
    }

    public static String formatAquaMessage(String message) {
        message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FW" + ChatColor.RESET  + "Fortress" + ChatColor.GRAY  +
                " ||> " + ChatColor.AQUA + message + ChatColor.RESET;
        return message;
    }

    public static String formatWhiteMessage(String message) {
        message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FW" + ChatColor.RESET  + "Fortress" + ChatColor.GRAY  +
                " ||> " + ChatColor.RESET + message;
        return message;
    }

    public static String formatYellowMessage(String message) {
        message = ChatColor.DARK_GRAY + "" + ChatColor.BOLD + "FW" + ChatColor.RESET  + "Fortress" + ChatColor.GRAY  +
                " ||> " + ChatColor.YELLOW + message + ChatColor.RESET;
        return message;
    }

}
