package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends SubCommand {
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/fwfortress delete <fortress_name>";
    }

    @Override
    public void perform(Player sender, String[] args) {
        if (!sender.hasPermission("fwfortress.admin")) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_PERMISSION));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.NOT_ENOUGHT_ARGUMENTS));
            return;
        }

        String fortressName = args[1];
        FortressService fortressService = FortressService.getInstance();

        if (!fortressService.getFortressContainer().containsKey(fortressName)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Nessuna fortezza con questo nome"));
            return;
        }

        fortressService.removeFortress(fortressName);

        sender.sendMessage(ChatFormatter.formatSuccessMessage("La fortezza " + fortressName + " e' stata eliminata"));

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return new ArrayList<>(FortressService.getInstance().getFortressContainer().keySet());
        }

        return null;
    }
}
