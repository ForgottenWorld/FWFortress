package me.architetto.fwfortress.command.user;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.MessageUtil;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InfoCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.INFO_CMD;
    }

    @Override
    public String getDescription() {
        return Message.INFO_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.INFO_CMD + " <fortress_name>";
    }

    @Override
    public String getPermission() {
        return "fwfortress.info";
    }

    @Override
    public int getArgsRequired() {
        return 1;
    }

    @Override
    public void perform(Player sender, String[] args) {

        if (args.length == 1) {
            SettingsHandler settingsHandler = SettingsHandler.getInstance();
            sender.sendMessage(MessageUtil.settingsInfo());
            Message.SETTINGS_INFO.send(sender,
                    settingsHandler.getDate(),
                    settingsHandler.getTime(),
                    settingsHandler.getMinInvaders(),
                    settingsHandler.getBattleTimeLimit(),
                    settingsHandler.getFortressHP(),
                    settingsHandler.getMaxDamageForSeconds(),
                    settingsHandler.getBattleCountdown(),
                    settingsHandler.allowInvadeAlliedFortress());
            sender.sendMessage(MessageUtil.chatFooter());

            return;
        }

        Optional<Fortress> optFortress = FortressService.getInstance().getFortress(args[1]);

        if (!optFortress.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = optFortress.get();

        String owner = null;
        String lastBattleDate = null;

        if (fortress.getOwner() != null) {
            try {
                owner = TownyAPI.getInstance().getDataSource().getTown(fortress.getOwner()).getFormattedName();
            } catch (NotRegisteredException e) {
                Message.EXCEPTION_MESSAGE.send(sender);
                e.printStackTrace();
                return;
            }
        }

        if (fortress.getLastBattle() != 0) {
            ZonedDateTime zonedDateTime = Instant
                    .ofEpochMilli(fortress.getLastBattle())
                    .atZone(ZoneId.of("Europe/Paris"));
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss z");
            lastBattleDate = zonedDateTime.format(dateTimeFormatter);
        }

        sender.sendMessage(MessageUtil.chatHeaderFortInfo());
        Message.FORTRESS_INFO.send(sender,fortress.getFormattedName(),owner,lastBattleDate,
                fortress.getExperience(),fortress.getFormattedLocation(),fortress.isEnabled());
        sender.sendMessage(MessageUtil.chatFooter());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return FortressService.getInstance().getFortressContainer().stream()
                    .map(Fortress::getName).collect(Collectors.toList());
        }

        return null;
    }
}
