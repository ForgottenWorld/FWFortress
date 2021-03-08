package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.event.*;

import com.palmergames.bukkit.towny.object.WorldCoord;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.logging.Level;

public class TownListener implements Listener {

    @EventHandler
    public void onTownDelete(PreDeleteTownEvent event) {

        FortressService fortressService = FortressService.getInstance();
        fortressService.getFortressContainer()
                .stream()
                .filter(fortress -> fortress.getOwner() != null && fortress.getOwner().equals(event.getTownName()))
                .forEach(fortress -> {
                    fortress.setOwner(null);
                    fortressService.updateFortress(fortress);
                    Message.FORTRESS_RETURN_FREE.broadcast(fortress.getFormattedName(), event.getTown().getFormattedName());
                });

    }

    @EventHandler
    public void onTownClaim(TownPreClaimEvent event) {
        WorldCoord worldCoord = event.getTownBlock().getWorldCoord();
        Set<String> s = FortressCreationService.getInstance().checkFortressDistance(worldCoord);
        if (s.size() != 0) {
            event.setCancelMessage(Message.ERR_FORTRESS_DISTANCE.asString(SettingsHandler.getInstance().
                    getMinFortressDistance(),s));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onNewTown(PreNewTownEvent event) {
        WorldCoord worldCoord = WorldCoord.parseWorldCoord(event.getPlayer().getLocation());
        Set<String> s = FortressCreationService.getInstance().checkFortressDistance(worldCoord);
        if (s.size() != 0) {
            event.setCancelMessage(Message.ERR_FORTRESS_DISTANCE.asString(SettingsHandler.getInstance().
                    getMinFortressDistance(),s));
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onRenameTown(TownPreRenameEvent event) {
        //todo:
        //Cosa succede se viene modificato il nome di una town durante una battaglia ? IDK
        //Va eventualmente cancellato l'evento se la città interessata ha una fortezza sotto attacco oppure sta invadedo
        //una fortezza avversaria
        FortressService fortressService = FortressService.getInstance();
        fortressService.getFortressContainer()
                .stream()
                .filter(fortress -> fortress.getOwner() != null && fortress.getOwner().equals(event.getOldName()))
                .forEach(fortress -> {
                    fortress.setOwner(event.getNewName());
                    fortressService.updateFortress(fortress);

                    //LOG
                    Bukkit.getLogger().log(Level.INFO, ChatColor.YELLOW + "[RenameTownEvent]" + ChatColor.RESET +
                            "Changed fortress owner name from " + ChatColor.YELLOW + event.getOldName()
                            + ChatColor.RESET + " to " + ChatColor.YELLOW + event.getNewName()
                            + ChatColor.RESET);

                });
    }

    @EventHandler(priority = EventPriority.LOW,ignoreCancelled = true)
    public void onTSpawn(TownSpawnEvent event) {
        if (BattleService.getInstance().getCurrentBattle()
                .stream()
                .anyMatch(battle -> battle.isInvaders(event.getPlayer().getUniqueId()))) {
            event.setCancelMessage(Message.TOWN_SPAWN_DENY.asString());
            event.setCancelled(true);
        }

    }

}



