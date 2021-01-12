package me.architetto.fwfortress.command.user;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.cmd.CommandDescription;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.util.localization.Message;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InvadeCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.INVADE_CMD;
    }

    @Override
    public String getDescription() {
        return CommandDescription.INVADE_CMD_DESCRIPTION;
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

        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/London"));
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();

        String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        if (!settingsHandler.getDate().contains(dayName)
                || dateTime.getHour() < settingsHandler.getTime().get(0)
                || dateTime.getHour() > settingsHandler.getTime().get(1)) {

            Message.ERR_BATTLE_TIME_RANGE.send(sender,settingsHandler.getDate(),settingsHandler.getTime().get(0),
                    settingsHandler.getTime().get(1));
            return;
        }

        Optional<Fortress> optionalFortress = FortressService.getInstance()
                .getFortress(sender.getLocation().getChunk().getChunkKey());

        if (!optionalFortress.isPresent()) {
            Message.ERR_INVALID_INVADE_POSITION.send(sender);
            return;
        }

        Fortress fortress = optionalFortress.get();

        if (fortress.getLastBattle() != 0) {
            long remain = SettingsHandler.getInstance().getBattleCooldown() -
                    (System.currentTimeMillis() - fortress.getLastBattle());

            if (remain > 0) {

                Message.ERR_INVADE_COOLDOWN.send(sender,fortress.getFortressName(),
                        TimeUnit.MILLISECONDS.toHours(remain),
                        TimeUnit.MILLISECONDS.toMinutes(remain) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remain)),
                        TimeUnit.MILLISECONDS.toSeconds(remain) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remain)));
                return;
            }
        }

        Town senderTown;

        try{
            senderTown = TownyAPI.getInstance().getDataSource().getResident(sender.getName()).getTown();
        } catch (NotRegisteredException e) {
            Message.ERR_NOT_PART_OF_A_TOWN.send(sender);
            return;
        }

        if (fortress.getCurrentOwner().equals(senderTown.getName())) {
            Message.ERR_FORTRESS_ALREADY_OWNED.send(sender,fortress.getFortressName());
            return;
        }

        if (FortressService.getInstance().getFortressContainer().values()
                .stream().noneMatch(f -> f.getFirstOwner().equals(senderTown.getName()))) {
            Message.ERR_TONW_CAN_NOT_INVADE.send(sender);
            return;
        }

        if (!settingsHandler.allowInvadeAlliedFortress()) {
            Town fortTown;

            try{
                fortTown = TownyAPI.getInstance().getDataSource().getTown(fortress.getCurrentOwner());
            } catch (NotRegisteredException e) {
                return;
            }

            if (fortTown.isAlliedWith(senderTown)) {
                Message.ERR_INVADE_ALLIED_FORTRESS.send(sender,fortress.getFortressName());
                return;
            }
        }

        if (BattleService.getInstance().isOccupied(fortress.getFortressName())) {
            Message.ERR_FORTRESS_UNDER_INVADE.send(sender,fortress.getFortressName());
            return;
        }

        List<UUID> senderTownResidentUUID = senderTown.getResidents().stream().map(Resident::getPlayer)
                .map(Player::getUniqueId).collect(Collectors.toCollection(ArrayList::new));

        List<UUID> invadersListUUID = new ArrayList<>();


        for (Entity entity : sender.getLocation().getChunk().getEntities()) {

            if (!(entity instanceof Player))
                continue;

            Player player = ((Player) entity).getPlayer();

            if (player != null && senderTownResidentUUID.contains(player.getUniqueId()))
                invadersListUUID.add(player.getUniqueId());
        }


        if (invadersListUUID.size() < settingsHandler.getMinInvaders()) {
            Message.ERR_INSUFFICIENT_INVADERS.send(sender,settingsHandler.getMinInvaders());
            return;
        }

        BattleService.getInstance().startBattle(fortress, invadersListUUID, senderTown.getName());



    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}
