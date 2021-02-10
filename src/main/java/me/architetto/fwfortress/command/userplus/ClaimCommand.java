package me.architetto.fwfortress.command.userplus;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.cmd.CommandName;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;


public class ClaimCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.CLAIM_CMD;
    }

    @Override
    public String getDescription() {
        return Message.CLAIM_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.CLAIM_CMD + " <fortress_name>" ;
    }

    @Override
    public String getPermission() {
        return "fwfortress.claim";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        String fortressName = String.join("_", Arrays.copyOfRange(args, 1, args.length));

        FortressCreationService.getInstance().fortressClaimMethod(sender, fortressName);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
