package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.TimeUtil;
import me.architetto.fwfortress.util.TownyUtil;
import org.bukkit.GameMode;
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

        if (event.getPlayer().isOp() && event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent()) {

            Town town = TownyUtil.getTownFromPlayerName(event.getPlayer().getName());

            if (TimeUtil.buildableTimeCheck(fortress.get())
                    || town == null
                    || !town.getName().equals(fortress.get().getCurrentOwner())) {

                Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getPlayer().isOp() && event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent()) {

            Town town = TownyUtil.getTownFromPlayerName(event.getPlayer().getName());

            if (TimeUtil.buildableTimeCheck(fortress.get())
                    || town == null
                    || !town.getName().equals(fortress.get().getCurrentOwner())) {

                Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent event) {

        if (event.getPlayer().isOp() && event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent()) {

            Town town = TownyUtil.getTownFromPlayerName(event.getPlayer().getName());

            if (TimeUtil.buildableTimeCheck(fortress.get())
                    || town == null
                    || !town.getName().equals(fortress.get().getCurrentOwner())) {

                Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {

        if (event.getPlayer().isOp() && event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey());

        if (fortress.isPresent()) {

            Town town = TownyUtil.getTownFromPlayerName(event.getPlayer().getName());

            if (TimeUtil.buildableTimeCheck(fortress.get())
                    || town == null
                    || !town.getName().equals(fortress.get().getCurrentOwner())) {

                Message.ERR_BLOCK_EVENT.send(event.getPlayer());
                event.setCancelled(true);
            }
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
