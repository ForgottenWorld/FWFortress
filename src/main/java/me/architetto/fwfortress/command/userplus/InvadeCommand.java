package me.architetto.fwfortress.command.userplus;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.TimeUtil;
import me.architetto.fwfortress.util.TownyUtil;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class InvadeCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.INVADE_CMD;
    }

    @Override
    public String getDescription() {
        return Message.INVADE_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.INVADE_CMD;
    }

    @Override
    public String getPermission() {
        return "fwfortress.user";
    }

    @Override
    public int getArgsRequired() {
        return 0;
    }

    @Override
    public void perform(Player sender, String[] args) {

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        if (settingsHandler.isInvadeDisabled()) {
            Message.ERR_BATTLE_DISABLED.send(sender);
            return;
        }

        if (!TimeUtil.invadeTimeCheck()) {
            Message.ERR_BATTLE_TIME_RANGE.send(sender,settingsHandler.getDate(),settingsHandler.getTime().get(0),
                    settingsHandler.getTime().get(1));
            return;
        }

        Optional<Fortress> fortress = FortressService.getInstance()
                .getFortress(sender.getLocation().getChunk().getChunkKey());

        if (!fortress.isPresent()) {
            Message.ERR_INVALID_INVADE_POSITION.send(sender);
            return;
        }

        if (fortress.get().getLastBattle() != 0) {
            long remain = SettingsHandler.getInstance().getBattleCooldown() -
                    (System.currentTimeMillis() - fortress.get().getLastBattle());

            if (remain > 0) {

                Message.ERR_INVADE_COOLDOWN.send(sender,fortress.get().getFortressName(),
                        TimeUnit.MILLISECONDS.toHours(remain),
                        TimeUnit.MILLISECONDS.toMinutes(remain) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remain)),
                        TimeUnit.MILLISECONDS.toSeconds(remain) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remain)));
                return;
            }
        }

        Town invaderTown = TownyUtil.getTownFromPlayerName(sender.getName());

        if (Objects.isNull(invaderTown)) {
            Message.ERR_NOT_PART_OF_A_TOWN.send(sender);
            return;
        }

        if (fortress.get().getCurrentOwner().equals(invaderTown.getName())) {
            Message.ERR_FORTRESS_ALREADY_OWNED.send(sender,fortress.get().getFormattedName());
            return;
        }

        if (FortressService.getInstance().getFortressContainer()
                .stream().noneMatch(f -> f.getFirstOwner().equals(invaderTown.getName()))) {
            Message.ERR_TONW_CAN_NOT_INVADE.send(sender);
            return;
        }

        if (!settingsHandler.allowInvadeAlliedFortress()) {

            Town fortressOwnerTown = TownyUtil.getTownFromTownName(fortress.get().getCurrentOwner());

            if (Objects.isNull(fortressOwnerTown) || fortressOwnerTown.isAlliedWith(invaderTown)) {
                Message.ERR_INVADE_ALLIED_FORTRESS.send(sender,fortress.get().getFormattedName());
                return;
            }

        }

        if (BattleService.getInstance().isOccupied(fortress.get().getFortressName())) {
            Message.ERR_FORTRESS_UNDER_INVADE.send(sender,fortress.get().getFormattedName());
            return;
        }

        List<UUID> invadersListUUID = new ArrayList<>(getInvaders(fortress.get(),invaderTown.getName()));

        if (invadersListUUID.size() < settingsHandler.getMinInvaders()) {
            Message.ERR_INSUFFICIENT_INVADERS.send(sender,settingsHandler.getMinInvaders());
            return;
        }

        BattleService.getInstance().startBattle(fortress.get(),
                invadersListUUID ,
                invaderTown);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

    private Set<UUID> getInvaders(Fortress fortress,String townName) {

        String worldName = fortress.getLocation().getWorld().getName();
        Set<UUID> list = new HashSet<>();

        fortress.getCunkKeys().forEach(key -> {
            Chunk chunk = Bukkit.getWorld(worldName).getChunkAt(key);
            Arrays.stream(chunk.getEntities()).filter(entity -> entity instanceof Player).forEach(entity -> {
                Resident resident = TownyUtil.getResidentFromPlayerName(entity.getName());
                Town town = TownyUtil.getTownFromPlayerName(entity.getName());
                if (resident != null && town != null && town.getName().equals(townName))
                    list.add(entity.getUniqueId());
            });
        });

        return list;
    }

}
