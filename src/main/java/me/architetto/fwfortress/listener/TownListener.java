package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.event.DeleteTownEvent;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TownListener implements Listener {

    @EventHandler
    public void onTownDelete(DeleteTownEvent event) {
        //todo: e se c'è una battaglia in corso sulla fortezza di questa città ? Bella domanda

        FortressService.getInstance().getFortressContainer().values().forEach(fortress -> {
            if (fortress.getFirstOwner().equals(event.getTownName())) {
                if (!fortress.getCurrentOwner().equals(event.getTownName())) {
                    TownyAPI.getInstance().getDataSource().getTowns().stream()
                            .filter(town -> town.getName().equals(fortress.getCurrentOwner()))
                            .forEach(town -> town.getResidents()
                                    .forEach(resident -> {
                                        Player player = resident.getPlayer();
                                        if (player != null && player.isOnline())
                                            player.sendMessage(ChatFormatter.formatMessage(ChatColor.AQUA + "La fortezza " +
                                                    ChatColor.YELLOW + fortress.getFortressName() +
                                                    ChatColor.AQUA + " e' caduta in rovina"));
                                    }));
                }
                FortressService.getInstance().removeFortress(fortress.getFortressName());
            }

            if (fortress.getCurrentOwner().equals(event.getTownName())) {
                if (!fortress.getCurrentOwner().equals(fortress.getFirstOwner())) {
                    TownyAPI.getInstance().getDataSource().getTowns().stream()
                            .filter(town -> town.getName().equals(fortress.getFirstOwner()))
                            .forEach(town -> town.getResidents()
                                    .forEach(resident -> {
                                        Player player = resident.getPlayer();
                                        if (player != null && player.isOnline())
                                            player.sendMessage(ChatFormatter.formatMessage(ChatColor.AQUA + "La fortezza " +
                                                    ChatColor.YELLOW + fortress.getFortressName() +
                                                    ChatColor.AQUA + " e' tornata in possesso di " +
                                                    ChatColor.YELLOW + town.getName()));
                                    }));
                    fortress.setCurrentOwner(fortress.getFirstOwner());
                } else
                    FortressService.getInstance().removeFortress(fortress.getFortressName());
            }
        });

    }
}



