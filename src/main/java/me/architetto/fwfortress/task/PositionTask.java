package me.architetto.fwfortress.task;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PositionTask {

    public static int schedulePlayerPositionStalker() {

        Set<UUID> uuids = new HashSet<>();

        return Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(FWFortress.getPlugin(FWFortress.class),
                () -> Bukkit.getServer().getOnlinePlayers()
                        .forEach(player -> {

                            Optional<Fortress> fortress = FortressService.getInstance()
                                    .getFortress(player.getLocation().getChunk().getChunkKey());

                            if (fortress.isPresent()) {
                                if (!uuids.contains(player.getUniqueId())) {
                                    uuids.add(player.getUniqueId());
                                    actionBarMessageTimeExtender(player,Message.FORTRESS_AREA_ALLERT
                                            .asString(fortress.get().getFormattedName()));
                                }
                            } else
                                uuids.remove(player.getUniqueId());
                        }), 30, 30);
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
