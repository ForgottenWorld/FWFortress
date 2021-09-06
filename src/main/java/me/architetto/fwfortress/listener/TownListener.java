package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.event.*;

import com.palmergames.bukkit.towny.event.town.TownRuinedEvent;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

public class TownListener implements Listener {

    @EventHandler
    public void onTownDelete(PreDeleteTownEvent event) {

        FortressService.getInstance().dispossessFortressesFromTown(event.getTown());
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
    public void onTownRuined(TownRuinedEvent event) {
        if (SettingsHandler.getInstance().isLeaveIfTownRuined())
            FortressService.getInstance().dispossessFortressesFromTown(event.getTown());
    }

    @EventHandler
    public void onRenameTown(TownPreRenameEvent event) {
        FortressService.getInstance().handleTownRename(event.getOldName(), event.getNewName());
    }

    @EventHandler(priority = EventPriority.LOW,ignoreCancelled = true)
    public void onTSpawn(TownSpawnEvent event) {
        if (BattleService.getInstance().getCurrentBattle()
                .stream()
                .anyMatch(battle -> battle.isInvaders(event.getPlayer().getUniqueId()))) {
            event.setCancelMessage(Message.TELEPORT_DENY.asString());
            event.setCancelled(true);
        }

    }

}



