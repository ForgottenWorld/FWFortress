package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import com.palmergames.bukkit.towny.event.PreNewTownEvent;
import com.palmergames.bukkit.towny.event.TownPreClaimEvent;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Optional;

public class TownListener implements Listener {

    @EventHandler
    public void onTownDelete(DeleteTownEvent event) {

        FortressService fortressService = FortressService.getInstance();

        new ArrayList<>(fortressService.getFortressContainer()).forEach(fortress -> {

            if (fortress.getFirstOwner().equals(event.getTownName())) {
                Message.FORTRESS_FALL_IN_RUIN1.broadcast(fortress.getFortressName());
                fortressService.removeFortress(fortress);

            } else if (fortress.getCurrentOwner().equals(event.getTownName())) {
                Message.FORTRESS_FALL_IN_RUIN2.broadcast(fortress.getFortressName(),fortress.getFirstOwner());

                fortressService.getFortressContainer()
                        .get(fortressService.getFortressContainer().indexOf(fortress))
                        .setCurrentOwner(fortress.getFirstOwner());

                fortressService.updateFortress(fortress);
            }
        });
    }

    @EventHandler
    public void onTownClaim(TownPreClaimEvent event) {
        Location location = new Location(event.getTownBlock().getWorldCoord().getBukkitWorld(),
                event.getTownBlock().getX(),
                64,
                event.getTownBlock().getZ());

        Optional<Fortress> optionalFortress = FortressService.getInstance().getFortressContainer().stream()
                .filter(fortress -> !FortressCreationService.getInstance().checkDistance(fortress.getLocation(),location)).findAny();
        optionalFortress.ifPresent(f -> {
            event.setCancelMessage(Message.ERR_TOWN_DISTANCE.asString(SettingsHandler.getInstance().getDistanceBetweenFortresses()));
            event.setCancelled(true);

        });
    }

    @EventHandler
    public void onNewTown(PreNewTownEvent event) {
        Location location = event.getPlayer().getLocation();
        Optional<Fortress> optionalFortress = FortressService.getInstance().getFortressContainer().stream()
                .filter(fortress -> !FortressCreationService.getInstance().checkDistance(fortress.getLocation(),location)).findAny();
        optionalFortress.ifPresent(f -> {
            event.setCancelMessage(Message.ERR_TOWN_DISTANCE.asString(SettingsHandler.getInstance().getDistanceBetweenFortresses()));
            event.setCancelled(true);
        });
    }

}



