package me.architetto.fwfortress.task;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PositionTask {

    public static int schedulePlayerPositionStalker() {

        Set<UUID> uuids = new HashSet<>();

        Runnable task = () -> Bukkit
                .getServer()
                .getOnlinePlayers()
                .forEach(player -> {
                    long chunkKey = player.getLocation().getChunk().getChunkKey();
                    UUID worldUUID = player.getWorld().getUID();

                    Optional<Fortress> fortress = FortressService
                            .getInstance()
                            .getFortress(chunkKey, worldUUID);

                    if (!fortress.isPresent()) {
                        uuids.remove(player.getUniqueId());
                        return;
                    }

                    if (uuids.contains(player.getUniqueId())) return;
                    uuids.add(player.getUniqueId());

                    actionBarMessageTimeExtender(
                            player,
                            Message.FORTRESS_AREA_ACTIONBAR.asString(fortress.get().getFormattedName())
                    );
                });

        return Bukkit.getServer()
                .getScheduler()
                .scheduleSyncRepeatingTask(JavaPlugin.getPlugin(FWFortress.class), task, 30, 30);
    }

    private static void actionBarMessageTimeExtender(Player player, String message) {
        new BukkitRunnable() {
            int times = 3;
            @Override
            public void run() {
                player.sendActionBar(message);
                times -= 1;

                if (times <= 0)
                    this.cancel();
            }
        }.runTaskTimer(FWFortress.getPlugin(FWFortress.class),0,15);
    }

}
