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
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.ChatColor;
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
        return "invade";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player sender, String[] args) {
        if (!sender.hasPermission("fwfortress.user")) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_PERMISSION));
            return;
        }

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        if (settingsHandler.isDisableInvade()) {
            sender.sendMessage(ChatFormatter.formatMessage("Le invasioni sono momentaneamente disabilitate!"));
            return;
        }

        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/London"));
        DayOfWeek dayOfWeek = dateTime.getDayOfWeek();
        String dayName = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        int time = dateTime.getHour();

        if (!settingsHandler.getDate().contains(dayName)
                || time < settingsHandler.getTime().get(0)
                || time > settingsHandler.getTime().get(1)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Le conquiste non sono attive in questo momento"));
            sender.sendMessage(ChatFormatter.formatListMessage("Giorni attivi : " + ChatColor.YELLOW + settingsHandler.getDate()));
            sender.sendMessage(ChatFormatter.formatListMessage("Orari attivi : " + ChatColor.YELLOW + "dalle "
                    + settingsHandler.getTime().get(0) + " alle " + settingsHandler.getTime().get(1)));
            return;
        }

        Optional<Fortress> optionalFortress = FortressService.getInstance().getFortress(sender.getLocation().getChunk().getChunkKey());

        if (!optionalFortress.isPresent()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Devi trovarti all'interno di una fortezza per poterla conquistare"));
            return;
        }

        Fortress fortress = optionalFortress.get();

        if (fortress.getLastBattle() != 0) {
            long remain = SettingsHandler.getInstance().getBattleCooldown() -
                    (System.currentTimeMillis() - fortress.getLastBattle());

            if (remain > 0) {

                sender.sendMessage(ChatFormatter.formatErrorMessage("La fortezza potra' essere attaccata tra : " +
                        ChatColor.YELLOW + String.format("%d ORE : %d MINUTI : %d SECONDI",
                        TimeUnit.MILLISECONDS.toHours(remain),
                        TimeUnit.MILLISECONDS.toMinutes(remain) -
                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(remain)),
                        TimeUnit.MILLISECONDS.toSeconds(remain) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(remain)))));

                return;
            }
        }

        Town senderTown;

        try{
            senderTown = TownyAPI.getInstance().getDataSource().getResident(sender.getName()).getTown();
        } catch (NotRegisteredException e) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Devi essere un cittadino per avviare una conquista !"));
            return;
        }

        if (fortress.getCurrentOwner().equals(senderTown.getName())) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Questa fortezza e' gia' sotto il controlo della tua citta' !"));
            return;
        }

        if (FortressService.getInstance().getFortressContainer().values()
                .stream().noneMatch(f -> f.getFirstOwner().equals(senderTown.getName()))) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Non puoi invadere una fortezza se la tua citta' non ne ha mai costruita una"));
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
                sender.sendMessage(ChatFormatter.formatErrorMessage("Non puoi invadere la fortezza di una citta' alleata"));
                return;
            }
        }

        if (BattleService.getInstance().isOccupied(fortress.getFortressName())) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("La fortezza e' gia' sotto attacco ..."));
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
            sender.sendMessage(ChatFormatter.formatErrorMessage("Numero di invasori minimo : "
                    + ChatColor.YELLOW + settingsHandler.getMinInvaders()));
            return;
        }

        BattleService.getInstance().startNewBattle(fortress, invadersListUUID, senderTown.getName());



    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }

}
