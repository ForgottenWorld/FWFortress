package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.event.PreDeleteTownEvent;

import com.palmergames.bukkit.towny.event.PreNewTownEvent;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import com.palmergames.bukkit.towny.object.WorldCoord;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

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

}



