package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.command.CommandName;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class StopCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.STOPBATTLE_CMD;
    }

    @Override
    public String getDescription() {
        return Message.STOP_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.STOPBATTLE_CMD + " [fortress_name]";
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

        Optional<Fortress> optionalFortress = FortressService.getInstance().getFortress(args[1]);

        if (!optionalFortress.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = optionalFortress.get();

        if (!BattleService.getInstance().isOccupied(fortress.getName())) {
            Message.ERR_NO_BATTLE_IS_RUNNING.send(sender,fortress.getFormattedName());
            return;
        }

        BattleService.getInstance().stopBattle(optionalFortress.get().getName());

        Message.BATTLE_STOPPED.broadcast(fortress.getFormattedName());

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2)
            return BattleService.getInstance().getOccupiedFortresses();

        return null;
    }
}
