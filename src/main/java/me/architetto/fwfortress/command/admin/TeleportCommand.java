package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TeleportCommand extends SubCommand {
    @Override
    public String getName() {
        return "tp";
    }

    @Override
    public String getDescription() {
        return Message.TELEPORT_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress tp <fortress_name>";
    }

    @Override
    public String getPermission() {
        return "fwfortress.teleport";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {
        Optional<Fortress> optFortress = FortressService.getInstance().getFortress(args[1]);

        if (!optFortress.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = optFortress.get();
        sender.teleport(fortress.getLocation().add(0,2,0));

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return FortressService.getInstance().getFortressContainer().stream()
                    .map(Fortress::getName).collect(Collectors.toList());
        }

        return null;
    }
}
