package me.architetto.fwfortress.task;

import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FortressAreaTask {

    public static void checkPlayerPositiontask() {

        List<UUID> uuids = new ArrayList<>();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(FWFortress.getPlugin(FWFortress.class),
                () -> Bukkit.getServer().getOnlinePlayers()
                        .forEach(player -> {
                            Optional<Fortress> fortress = FortressService.getInstance()
                                    .getFortress(player.getLocation().getChunk().getChunkKey());
                            if (fortress.isPresent()) {
                                if (!uuids.contains(player.getUniqueId())) {
                                    uuids.add(player.getUniqueId());
                                    player.sendActionBar(Message.FORTRESS_AREA_ALLERT.asString(fortress.get().getFormattedName()));
                                }
                            } else
                                uuids.remove(player.getUniqueId());
                        }), 30, 30);

    }

}
