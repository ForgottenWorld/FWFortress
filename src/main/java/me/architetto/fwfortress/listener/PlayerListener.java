package me.architetto.fwfortress.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

        if (event.getPlayer().hasPermission("fwfortress.build")
                || event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey(),event.getBlock().getWorld().getUID());

        if (fortress.isPresent()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getPlayer().hasPermission("fwfortress.build")
                || event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey(),event.getBlock().getWorld().getUID());

        if (fortress.isPresent()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent event) {

        if (event.getPlayer().hasPermission("fwfortress.build")
                || event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey(),event.getBlock().getWorld().getUID());

        if (fortress.isPresent()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {

        if (event.getPlayer().hasPermission("fwfortress.build")
                || event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(event.getBlock().getChunk().getChunkKey(),event.getBlock().getWorld().getUID());

        if (fortress.isPresent()) {
            Message.ERR_BLOCK_EVENT.send(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.isInvaders(event.getEntity().getUniqueId())) {
                //potrebbero abusare dormendo in un letto vicino alla fortezza e ritornando
                //velocemente alla battaglia (anche se non influenzano il calo degli hp della fortezza)
                battle.removeInvaders(event.getEntity());
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

    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onPlayerTelepor(PlayerTeleportEvent event) {

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL
                || event.getCause() == PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)
            return;

        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.isInvaders(event.getPlayer().getUniqueId())) {
                //Oppure sarebbe il caso di cancellare semplicemente l'evento ?
                Message.TELEPORT_DEATH_EVENT.send(event.getPlayer());
                event.getPlayer().setHealth(0);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        String townName;

        try {
            townName = TownyAPI.getInstance().getDataSource().getResident(event.getPlayer().getName()).getTown().getName();
        } catch (NotRegisteredException e) {
            return;
        }

        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.isTownInvolved(townName))
                battle.addPlayerToBossBar(event.getPlayer());
            });

    }

}
