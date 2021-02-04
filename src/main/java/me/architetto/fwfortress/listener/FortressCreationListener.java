package me.architetto.fwfortress.listener;

import me.architetto.fwfortress.fortress.FortressCreationService;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Objects;

public class FortressCreationListener implements Listener {

    @EventHandler
    public void fortressCreationListener(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        if(event.getItem() == null || event.getItem().getType() != Material.STICK)
            return;

        if(!event.getHand().equals(EquipmentSlot.HAND))
            return;

        Player player = event.getPlayer();

        if(!FortressCreationService.getInstance().isPlayerInFortressCreationMode(player)) {
            return;
        }

        Block block = event.getClickedBlock();

        if (Objects.isNull(block))
            return;

        FortressCreationService.getInstance().fortressCreationMethod(player, block.getLocation().toBlockLocation());

        event.setCancelled(true);

    }



}
