package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.cmd.CommandDescription;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.util.localization.Message;
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
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = FortressService.getInstance().getFortressContainer().get(fortressName);

        //Message.SUCCESS_FORTRESS_DELETE.send(sender,fortressName);
        Message.FORTRESS_DELETED_BROADCAST.broadcast(fortressName,fortress.getFirstOwner(),fortress.getCurrentOwner());

        fortressService.removeFortress(fortressName);


    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return new ArrayList<>(FortressService.getInstance().getFortressContainer().keySet());
        }

        return null;
    }
}
