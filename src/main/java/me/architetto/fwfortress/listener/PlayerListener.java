package me.architetto.fwfortress.listener;

import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.fortress.FortressService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener {
    BattleService battleService = BattleService.getInstance();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        FortressService fortressService = FortressService.getInstance();
        for (String fortName : fortressService.getProtectedChunkKeys().keySet()) {
            for (long key : fortressService.getProtectedChunkKeys().get(fortName)) {
                //Al momento i blocchi fortezza possono essere rotti solo da un Op
                if (event.getBlock().getChunk().getChunkKey() == key && !event.getPlayer().isOp()) {
                    event.getPlayer().sendMessage("Non puoi distruggere blocchi fortezza");
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

        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL)
            return;

        battleService.getCurrentBattle().forEach(battle -> {
            if (battle.getActiveInvaders().contains(event.getPlayer().getUniqueId())) {
                event.getPlayer().setHealth(0);
            }
        });
    }

}
