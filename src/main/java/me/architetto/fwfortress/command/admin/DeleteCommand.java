package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeleteCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.DELETE_CMD;
    }

    @Override
    public String getDescription() {
        return Message.DELETE_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.DELETE_CMD + " <fortress_name>";
    }

    @Override
    public String getPermission() {
        return "fwfortress.admin";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        String fortressName = args[1];

        FortressService fortressService = FortressService.getInstance();

        Optional<Fortress> fortress = fortressService.getFortress(fortressName);

        if (!fortress.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Message.FORTRESS_DELETED_BROADCAST.broadcast(fortress.get().getFormattedName(),
                fortress.get().getFirstOwner(),
                fortress.get().getCurrentOwner());

        fortressService.removeFortress(fortress.get());

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return FortressService.getInstance().getFortressContainer().stream()
                    .map(Fortress::getFortressName).collect(Collectors.toList());
        }

        return null;
    }
}
