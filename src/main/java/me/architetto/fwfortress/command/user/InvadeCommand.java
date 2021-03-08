package me.architetto.fwfortress.command.user;

import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.TimeUtil;
import me.architetto.fwfortress.util.TownyUtil;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.localization.Message;
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

        Fortress fort = fortress.get();

        if (!fort.isEnabled()) {
            Message.ERR_FORTRESS_DISABLED.send(sender,fort.getFormattedName());
            return;
        }

        if (fort.getLastBattle() != 0) {
            long remain = SettingsHandler.getInstance().getBattleCooldown() -
                    (System.currentTimeMillis() - fort.getLastBattle());

            if (remain > 0) {

                ZonedDateTime zonedDateTime = Instant
                        .ofEpochMilli(SettingsHandler.getInstance().getBattleCooldown() + fortress.get().getLastBattle())
                        .atZone(ZoneId.of("Europe/Paris"));
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z");

                Message.ERR_INVADE_COOLDOWN.send(sender,fort.getFormattedName(),zonedDateTime.format(dateTimeFormatter));

                return;
            }
        }

        Town invaderTown = TownyUtil.getTownFromPlayerName(sender.getName());

        if (Objects.isNull(invaderTown)) {
            Message.ERR_NOT_PART_OF_A_TOWN.send(sender);
            return;
        }

        if (Objects.isNull(fort.getOwner())) {
            fort.setOwner(invaderTown.getName());
            fort.setLastBattle(System.currentTimeMillis());
            FortressService.getInstance().updateFortress(fort);
            Message.FORTRESS_CLAIM_BROADCAST.broadcast(fort.getFormattedName(),invaderTown.getFormattedName());
            return;
        }

        if (fort.getOwner().equals(invaderTown.getName())) {
            Message.ERR_FORTRESS_ALREADY_OWNED.send(sender,fortress.get().getFormattedName());
            return;
        }

        Optional<Fortress> optInvaderFirstFortress = FortressService.getInstance().getFortressContainer()
                .stream().filter(f -> f.getOwner().equals(invaderTown.getName())).findFirst();

        if (!settingsHandler.allowInvadeAlliedFortress()) {

            Town fortressOwnerTown = TownyUtil.getTownFromTownName(fort.getOwner());

            if (Objects.nonNull(fortressOwnerTown) && fortressOwnerTown.isAlliedWith(invaderTown)) {
                Message.ERR_INVADE_ALLIED_FORTRESS.send(sender,fort.getFormattedName());
                return;
            }

        }

        if (BattleService.getInstance().isOccupied(fortress.get().getName())) {
            Message.ERR_FORTRESS_UNDER_INVADE.send(sender,fortress.get().getFormattedName());
            return;
        }

        Set<UUID> invadersUUID = getInvaders(fortress.get(), invaderTown);

        if (invadersUUID.size() < settingsHandler.getMinInvaders()) {
            Message.ERR_INSUFFICIENT_INVADERS.send(sender,settingsHandler.getMinInvaders());
            return;
        }

        BattleService.getInstance().startBattle(fortress.get(),
                invaderTown,
                invadersUUID);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

    private Set<UUID> getInvaders(Fortress fortress,Town invadersTown) {

        Set<UUID> resUUID = invadersTown.getResidents()
                .stream()
                .map(Resident::getPlayer)
                .map(Player::getUniqueId)
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
