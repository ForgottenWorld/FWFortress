package me.architetto.fwfortress.listener;

import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.localization.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {
    BattleService battleService = BattleService.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        FortressService fortressService = FortressService.getInstance();
        for (String fortName : fortressService.getProtectedChunkKeys().keySet()) {
            for (long key : fortressService.getProtectedChunkKeys().get(fortName)) {
                if (event.getBlock().getChunk().getChunkKey() == key && !event.getPlayer().isOp()) {
                    Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        FortressService fortressService = FortressService.getInstance();
        for (String fortName : fortressService.getProtectedChunkKeys().keySet()) {
            for (long key : fortressService.getProtectedChunkKeys().get(fortName)) {
                if (event.getBlock().getChunk().getChunkKey() == key && !event.getPlayer().isOp()) {
                    Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketPlace(PlayerBucketEmptyEvent event) {
        FortressService fortressService = FortressService.getInstance();
        for (String fortName : fortressService.getProtectedChunkKeys().keySet()) {
            for (long key : fortressService.getProtectedChunkKeys().get(fortName)) {
                if (event.getBlock().getChunk().getChunkKey() == key && !event.getPlayer().isOp()) {
                    Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        FortressService fortressService = FortressService.getInstance();
        for (String fortName : fortressService.getProtectedChunkKeys().keySet()) {
            for (long key : fortressService.getProtectedChunkKeys().get(fortName)) {
                if (event.getBlock().getChunk().getChunkKey() == key && !event.getPlayer().isOp()) {
                    Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.getActiveInvaders().contains(event.getEntity().getUniqueId())) {
                battle.removeInvaders(event.getEntity().getUniqueId());
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.getActiveInvaders().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().setHealth(0);

            }
        });
    }

    @EventHandler
    public void onPlayerTelepor(PlayerTeleportEvent event) {

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)
            return;

        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.getActiveInvaders().contains(event.getPlayer().getUniqueId())) {
                Message.TELEPORT_DEATH_EVENT.send(event.getPlayer());
                event.getPlayer().setHealth(0);
            }
        });
    }

}
