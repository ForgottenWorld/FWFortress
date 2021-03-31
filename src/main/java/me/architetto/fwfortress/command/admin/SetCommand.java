package me.architetto.fwfortress.command.admin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SetCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.SET_CMD;
    }

    @Override
    public String getDescription() {
        return Message.SET_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwf " + CommandName.SET_CMD + " <fortress_name> <owner/exp/enable> <value>";
    }

    @Override
    public String getPermission() {
        return "fwfortress.set";
    }

    @Override
    public int getArgsRequired() {
        return 3;
    }

    private static final List<String> setOptions = Arrays.asList(
            "owner",
            "exp",
            "enable",
            "free"
    );

    private static final List<String> booleanOptions = Arrays.asList(
            "true",
            "false"
    );

    @Override
    public void perform(Player sender, String[] args) {

        Optional<Fortress> optFortress = FortressService.getInstance().getFortress(args[1]);

        if (!optFortress.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = optFortress.get();

        if (BattleService.getInstance().isOccupied(fortress.getName())) {
            Message.ERR_FORTRESS_OCCUPIED.send(sender,fortress.getFormattedName());
            return;
        }

        switch (args[2].toLowerCase()) {
            case "owner":
                if (args.length < 4)
                    return;

                Town newOwner;

                try {
                    newOwner = TownyAPI.getInstance().getDataSource().getTown(args[3]);
                } catch (NotRegisteredException e) {
                    //e.printStackTrace();
                    Message.ERR_INVALID_TOWN_NAME.send(sender);
                    return;
                }

                if (Objects.nonNull(fortress.getOwner())
                        && fortress.getOwner().equals(newOwner.getName()))
                    return;

                fortress.setOwner(newOwner.getName());
                FortressService.getInstance().updateFortress(fortress);
                Message.FORTRESS_CLAIM_BROADCAST.broadcast(fortress.getFormattedName(),newOwner.getFormattedName());
                break;
            case "exp":
                if (args.length < 4)
                    return;
                long expNewValue;

                try {
                    expNewValue = Long.parseLong(args[3]);
                } catch (NumberFormatException nfe) {
                    Message.ERR_INVALID_INPUT_VALUE.send(sender);
                    return;
                }

                if (fortress.getExperience() == expNewValue)
                    return;

                fortress.setExperience(expNewValue);
                FortressService.getInstance().updateFortress(fortress);
                Message.SUCCESS_MESSAGE.send(sender);
                break;
            case "enable":
                if (args.length < 4)
                    return;

                if (!args[3].equalsIgnoreCase("true")
                        && !args[3].equalsIgnoreCase("false")) {
                    Message.ERR_INVALID_INPUT_VALUE.send(sender);
                    return;
                }

                boolean b = Boolean.parseBoolean(args[3]);

                if (fortress.isEnabled() == b)
                    return;

                fortress.setEnabled(b);
                FortressService.getInstance().updateFortress(fortress);
                Message.SUCCESS_MESSAGE.send(sender);
                break;
            case "free":
                if (Objects.isNull(fortress.getOwner()))
                    return;

                Town oldOwner;

                try {
                    oldOwner = TownyAPI.getInstance().getDataSource().getTown(fortress.getOwner());
                } catch (NotRegisteredException e) {
                    e.printStackTrace();
                    Message.EXCEPTION_MESSAGE.send(sender);
                    return;
                }

                fortress.setOwner(null);
                FortressService.getInstance().updateFortress(fortress);

                Message.FORTRESS_RETURN_FREE.broadcast(fortress.getFormattedName(),oldOwner.getFormattedName());
                break;
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2)
            return FortressService.getInstance().getFortressNames();
        else if (args.length == 3)
            return setOptions;
        else if (args.length == 4) {

            if (args[2].equalsIgnoreCase("owner"))
                return TownyAPI.getInstance().getDataSource().getTowns()
                        .stream()
                        .map(Town::getName)
                        .collect(Collectors.toList());
            else if (args[2].equalsIgnoreCase("exp"))
                return null;
            else if (args[2].equalsIgnoreCase("enable"))
                return booleanOptions;

        }
        return null;
    }
}
