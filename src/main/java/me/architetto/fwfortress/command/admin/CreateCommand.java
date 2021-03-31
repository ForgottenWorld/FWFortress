package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.*;

public class CreateCommand extends SubCommand {

    @Override
    public String getName() {
        return CommandName.CREATE_CMD;
    }
    @Override
    public String getDescription() {
        return Message.CREATE_COMMAND.asString();
    }
    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.CREATE_CMD + " [fortress_name]";
    }
    @Override
    public String getPermission() {
        return "fwfortress.create";
    }
    @Override
    public int getArgsRequired() {
        return 2;
    }
    @Override
    public void perform(Player sender, String[] args) {

        if (FortressCreationService.getInstance().isPlayerInFortressCreationMode(sender)) {
            Message.ERR_CREATION_MODE.send(sender);
            return;
        }

        String fortressName = String.join("_", Arrays.copyOfRange(args, 1, args.length));

        if (FortressService.getInstance().getFortress(fortressName).isPresent()) {

            Message.ERR_FORTRESS_NAME_ALREADY_EXIST.send(sender,fortressName);
            return;
        }


        Message.CREATION_MODE_MSG.send(sender);

        FortressCreationService.getInstance().addPlayerToFortressCreationMode(sender, fortressName);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
    
}
