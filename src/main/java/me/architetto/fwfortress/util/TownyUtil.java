package me.architetto.fwfortress.util;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import java.util.List;

public class TownyUtil {

    public static Town getTownFromTownName(String string) {
        try {
            return TownyAPI.getInstance().getDataSource().getTown(string);
        } catch (NotRegisteredException ignored) {}
        return null;
    }

    public static Town getTownFromPlayerName(String string) {
        try {
            return TownyAPI.getInstance().getDataSource().getResident(string).getTown();
        } catch (NotRegisteredException ignored) {}
        return null;
    }

    public static Resident getResidentFromPlayerName(String string) {
        try {
            return TownyAPI.getInstance().getDataSource().getResident(string);
        } catch (NotRegisteredException ignored) {}
        return null;
    }

    public static List<Resident> getTownResidentsFromTownName(String string) {
        try {
            return TownyAPI.getInstance().getDataSource().getTown(string).getResidents();
        } catch (NotRegisteredException ignored) {}
        return null;
    }

    public static void sendMessageToTown(Town town, String message) {
        town.getResidents().stream().map(Resident::getPlayer).forEach(p -> p.sendMessage(message));
    }

}
