package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.util.cmd.CommandPermission;
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
        return null;
    }

    @Override
    public String getSyntax() {
        return "/fwfortress delete <fortress_name>";
    }

    @Override
    public String getPermission() {
        return CommandPermission.FORTRESS_ADMIN_PERM;
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
