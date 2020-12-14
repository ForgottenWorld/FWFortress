package me.architetto.fwfortress.util;

import org.bukkit.ChatColor;

import java.util.Collections;

public class ChatFormatter {

    public static String chatHeaderNewFort() {
        return  ChatColor.YELLOW + "[*]----------------[ " +
                ChatColor.DARK_AQUA + ChatColor.BOLD + "NEW FORTRESS" +
                ChatColor.YELLOW + " ]----------------[*]";
    }

    public static String chatFooter() {
        return  ChatColor.YELLOW + String.join("", Collections.nCopies(53, "-"));
    }

    public static String formatSuccessMessage(String message) {
        message = ChatColor.GREEN + "[FW Fortress] " + ChatColor.RESET + message;
        return message;
    }

    public static String formatErrorMessage(String message) {
        message = ChatColor.RED + "[FW Fortress] " + ChatColor.RESET + message;
        return message;
    }

    public static String formatListMessage(String message) {
        message = ChatColor.GRAY + "[] " + ChatColor.RESET + message;
        return message;
    }

    public static String formatMessage(String message) {
        message = ChatColor.GOLD + "[FW Fortress] " + ChatColor.RESET + message;
        return message;
    }

}
