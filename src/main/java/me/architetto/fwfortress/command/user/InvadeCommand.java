package me.architetto.fwfortress.command.user;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.echelon.EchelonService;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.TimeUtil;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
        return "fwfortress.invade";
    }

    @Override
    public int getArgsRequired() {
        return 0;
    }

    @Override
    public void perform(Player sender, String[] args) {

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        Optional<Fortress> optFortress = FortressService.getInstance()
                .getFortress(sender.getLocation().getChunk().getChunkKey(), sender.getWorld().getUID());

        if (!optFortress.isPresent()) {
            Message.ERR_INVALID_INVADE_POSITION.send(sender);
            return;
        }

        if (!TimeUtil.invadeTimeCheck()) {
            Message.ERR_BATTLE_TIME_RANGE.send(sender,settingsHandler.getDate(),settingsHandler.getTime().get(0),
                    settingsHandler.getTime().get(1));
            return;
        }

        Fortress fortress = optFortress.get();

        if (!fortress.isEnabled()) {
            Message.ERR_FORTRESS_DISABLED.send(sender,fortress.getFormattedName());
            return;
        }

        Resident senderRes;

        try {
            senderRes = TownyAPI.getInstance().getDataSource().getResident(sender.getName());
        } catch (NotRegisteredException e) {
            Message.EXCEPTION_MESSAGE.send(sender);
            e.printStackTrace();
            return;
        }

        if (!senderRes.hasTown()) {
            Message.ERR_NOT_PART_OF_A_TOWN.send(sender);
            return;
        }

        Town invaderTown;

        try {
            invaderTown = senderRes.getTown();
        } catch (NotRegisteredException e) {
            Message.EXCEPTION_MESSAGE.send(sender);
            e.printStackTrace();
            return;
        }


        if (Objects.isNull(fortress.getOwner())) {
            fortress.setOwner(invaderTown.getName());
            fortress.setLastBattle(System.currentTimeMillis());
            FortressService.getInstance().updateFortress(fortress);
            Message.FORTRESS_CLAIM_BROADCAST.broadcast(fortress.getFormattedName(),invaderTown.getFormattedName());
            return;
        }

        Town defendersTown;

        try {
            defendersTown = TownyAPI.getInstance().getDataSource().getTown(fortress.getOwner());
        } catch (NotRegisteredException e) {
            Message.EXCEPTION_MESSAGE.send(sender);
            e.printStackTrace();
            return;
        }


        if (defendersTown.getName().equals(invaderTown.getName())) {
            Message.ERR_FORTRESS_ALREADY_OWNED.send(sender,optFortress.get().getFormattedName());
            return;
        }

        if (fortress.getLastBattle() != 0 &&
                settingsHandler.getBattleCountdown() - (System.currentTimeMillis() - fortress.getLastBattle()) > 0) {

            ZonedDateTime zonedDateTime = Instant
                    .ofEpochMilli(SettingsHandler.getInstance().getBattleCountdown() + optFortress.get().getLastBattle())
                    .atZone(ZoneId.of("Europe/Paris"));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z");

            Message.ERR_INVADE_COOLDOWN.send(sender,fortress.getFormattedName(),zonedDateTime.format(dateTimeFormatter));

            return;
        }

        if (!settingsHandler.allowInvadeAlliedFortress()
                && defendersTown.isAlliedWith(invaderTown)) {

            Message.ERR_INVADE_ALLIED_FORTRESS.send(sender,fortress.getFormattedName());
            return;

        }

        if (BattleService.getInstance().isOccupied(optFortress.get().getName())) {
            Message.ERR_FORTRESS_UNDER_INVADE.send(sender,optFortress.get().getFormattedName());
            return;
        }

        Set<UUID> invadersUUID = getInvaders(optFortress.get(), invaderTown);

        if (settingsHandler.isFWEchelonLoaded()) {
            EchelonService echelonService = EchelonService.getInstance();
            new HashSet<>(invadersUUID).stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(player -> {
                        if (echelonService.isPlayerInMutexActivity(player)) {
                            invadersUUID.remove(player.getUniqueId());
                            Message.ERR_ECHELON_ACTIVITY.send(player,echelonService.getPlayerMutexActivityName(player));
                        }
                    });
        }

        if (invadersUUID.size() < settingsHandler.getMinInvaders()) {
            Message.ERR_INSUFFICIENT_INVADERS.send(sender,settingsHandler.getMinInvaders());
            return;
        }

        BattleService.getInstance().startBattle(optFortress.get(),
                invaderTown,
                invadersUUID,
                defendersTown);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

    private Set<UUID> getInvaders(Fortress fortress,Town invadersTown) {

        Set<UUID> resUUID = invadersTown.getResidents()
                .stream()
                .map(Resident::getUUID)
                .collect(Collectors.toSet());

        World world = fortress.getLocation().getWorld();

        Set<UUID> invaders = new HashSet<>();

        fortress.getCunkKeys().forEach(key -> {

            Chunk chunk = world.getChunkAt(key);
            invaders.addAll(Arrays.stream(chunk.getEntities())
                    .filter(e -> e instanceof Player)
                    .map(Entity::getUniqueId)
                    .filter(resUUID::contains)
                    .collect(Collectors.toSet()));
        });

        return invaders;
    }

}
