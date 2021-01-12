package me.architetto.fwfortress.command.user;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.cmd.CommandDescription;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.util.localization.Message;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfoCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.INFO_CMD;
    }

    @Override
    public String getDescription() {
        return CommandDescription.INFO_CMD_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return "fwfortress.user";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        Optional<Fortress> fortressOptional = FortressService.getInstance().getFortress(args[1]);

        if (!fortressOptional.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = fortressOptional.get();

        sender.sendMessage(ChatFormatter.chatHeaderFortInfo());

        Message.FORTRESS_INFO.send(sender,fortress.getFortressName(),fortress.getFirstOwner(),
                fortress.getCurrentOwner(),fortress.getCurrentHP(),fortress.getFormattedLocation());

        sender.sendMessage(ChatFormatter.chatFooter());

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return new ArrayList<>(FortressService.getInstance().getFortressContainer().keySet());
        }

        return null;
    }
}
