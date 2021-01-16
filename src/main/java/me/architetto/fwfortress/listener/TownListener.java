package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.localization.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class TownListener implements Listener {

    @EventHandler
    public void onTownDelete(DeleteTownEvent event) {

        FortressService fortressService = FortressService.getInstance();

        new ArrayList<>(fortressService.getFortressContainer().values()).forEach(fortress -> {

            if (fortress.getFirstOwner().equals(event.getTownName())) {
                Message.FORTRESS_FALL_IN_RUIN1.broadcast(fortress.getFortressName());
                fortressService.removeFortress(fortress.getFortressName());

            } else if (fortress.getCurrentOwner().equals(event.getTownName())) {
                Message.FORTRESS_FALL_IN_RUIN2.broadcast(fortress.getFortressName(),fortress.getFirstOwner());
                fortressService.getFortressContainer().get(fortress.getFortressName())
                        .setCurrentOwner(fortress.getFirstOwner());
                fortressService.saveFortress(fortress);
            }
        });
    }

}



