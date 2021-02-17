package me.architetto.fwfortress.command.user;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.StringUtil;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

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
        return "fwfortress.user";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        Optional<Fortress> fortress = FortressService.getInstance().getFortress(args[1]);

        if (!fortress.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        sender.sendMessage(StringUtil.chatHeaderFortInfo());

        Message.FORTRESS_INFO.send(sender,fortress.get().getFortressName(),fortress.get().getFirstOwner(),
                fortress.get().getCurrentOwner(),fortress.get().getCurrentHP(),fortress.get().getFormattedLocation());

        sender.sendMessage(StringUtil.chatFooter());
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
