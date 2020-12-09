package me.architetto.fwfortress.listener;

import me.architetto.fwfortress.fortress.FortressService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        FortressService fortressService = FortressService.getInstance();
        for (String fortName : fortressService.getProtectedChunkKeys().keySet()) {
            for (long key : fortressService.getProtectedChunkKeys().get(fortName)) {
                //Al momento i blocchi fortezza possono essere rotti solo da un Op
                if (event.getBlock().getChunk().getChunkKey() == key && !event.getPlayer().isOp())
                    event.setCancelled(true);
            }
        }
    }

}
