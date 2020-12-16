package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class TownListener implements Listener {

    @EventHandler
    public void onTownDelete(DeleteTownEvent event) {

        FortressService fortressService = FortressService.getInstance();
        List<Fortress> fortresses = new ArrayList<>(fortressService.getFortressContainer().values());


        fortresses.forEach(fortress -> {
            if (fortress.getCurrentOwner().equals(event.getTownName())) {
                if (!fortress.getFirstOwner().equals(event.getTownName())) {
                    Bukkit.broadcastMessage(ChatFormatter.formatMessage(ChatColor.AQUA + "La fortezza " + ChatColor.YELLOW
                            + fortress.getFortressName() + ChatColor.AQUA + " e' tornata sotto il controllo di " +
                            ChatColor.YELLOW + fortress.getFirstOwner()));

                    fortressService.getFortressContainer().get(fortress.getFortressName())
                            .setCurrentOwner(fortress.getFirstOwner());
                    fortressService.updateFortressFile(fortress.getFortressName());
                } else {
                    Bukkit.broadcastMessage(ChatFormatter.formatMessage(ChatColor.AQUA + "La fortezza " + ChatColor.YELLOW
                            + fortress.getFortressName() + ChatColor.AQUA + " e' caduta in rovina"));
                    fortressService.removeFortress(fortress.getFortressName());
                }
            }
        });
    }
}



