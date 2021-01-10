package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import me.architetto.fwfortress.util.cmd.CommandDescription;
import me.architetto.fwfortress.util.cmd.CommandName;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.DELETE_CMD;
    }

    @Override
    public String getDescription() {
        return CommandDescription.DELETE_CMD_DESCRIPTION;
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

        if (!fortressService.getFortressContainer().containsKey(fortressName)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_FORTRESS_NAME2));
            return;
        }

        fortressService.removeFortress(fortressName);

        sender.sendMessage(ChatFormatter.formatSuccessMessage(String.format(Messages.FORTRESS_DELETED,fortressName)));

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return new ArrayList<>(FortressService.getInstance().getFortressContainer().keySet());
        }

        return null;
    }
}
