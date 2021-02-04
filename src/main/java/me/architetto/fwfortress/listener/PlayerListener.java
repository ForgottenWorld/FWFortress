package me.architetto.fwfortress.listener;

import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.Optional;

public class PlayerListener implements Listener {
    BattleService battleService = BattleService.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent() && !event.getPlayer().isOp()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent() && !event.getPlayer().isOp()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent event) {
        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent() && !event.getPlayer().isOp()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent() && !event.getPlayer().isOp()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.isInvaders(event.getEntity().getUniqueId())) {
                battle.removeInvaders(event.getEntity().getUniqueId());
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.isInvaders(event.getPlayer().getUniqueId())) {
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
            if (battle.isInvaders(event.getPlayer().getUniqueId())) {
                Message.TELEPORT_DEATH_EVENT.send(event.getPlayer());
                event.getPlayer().setHealth(0);
            }
        });
    }

}
