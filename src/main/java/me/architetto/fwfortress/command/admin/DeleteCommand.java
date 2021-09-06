package me.architetto.fwfortress.command.admin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class DeleteCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.DELETE_CMD;
    }

    @Override
    public String getDescription() {
        return Message.DELETE_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.DELETE_CMD + " <fortress_name>";
    }

    @Override
    public String getPermission() {
        return "fwfortress.delete";
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        String fortressName = args[1];

        FortressService fortressService = FortressService.getInstance();

        Optional<Fortress> optFortress = fortressService.getFortress(fortressName);

        if (!optFortress.isPresent()) {
            Message.ERR_FORTRESS_DOES_NOT_EXIST.send(sender);
            return;
        }

        Fortress fortress = optFortress.get();

        if (fortress.getOwner() != null) {
            Town owner;

            try {
                owner = TownyAPI.getInstance().getDataSource().getTown(fortress.getOwner());
            } catch (NotRegisteredException e) {
                Message.EXCEPTION_MESSAGE.send(sender);
                e.printStackTrace();
                return;
            }

            Message.FORTRESS_OWNED_DELETED.broadcast(fortress.getFormattedName(),
                    owner.getFormattedName());
        } else
            Message.SUCCESS_FORTRESS_DELETED.send(sender,fortress.getFormattedName());

        fortressService.removeFortress(fortress);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return FortressService.getInstance().getFortressNames();
        }

        return null;
    }
}
