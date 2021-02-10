package me.architetto.fwfortress.command.admin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.TownyUtil;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.List;

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
        return "/fwfortress " + CommandName.CREATE_CMD + " <fortress_townOwnerName> <fortress_name>";
    }
    @Override
    public String getPermission() {
        return "fwfortress.admin";
    }
    @Override
    public int getArgsRequired() {
        return 3;
    }
    @Override
    public void perform(Player sender, String[] args) {

        String fortressOwner = args[1];

        Town senderTown = TownyUtil.getTownFromTownName(args[1]);

        if (Objects.isNull(senderTown)) {
            Message.ERR_TOWN_NAME.send(sender);
            return;
        }

        FortressService fortressService = FortressService.getInstance();

        //check if is the town's first fortress
        if (fortressService.getFortressContainer().stream().anyMatch(f -> f.getFirstOwner().equals(args[1]))) {
            Message.ERR_TOWN_ALREADY_BUILD_FORTRESS.send(sender,fortressOwner);
            return;
        }

        String fortressName = String.join("_", Arrays.copyOfRange(args, 2, args.length));

        Optional<Fortress> fortress = fortressService.getFortress(fortressName);

        //check if this fortress's name already exist
        if (fortress.isPresent()) {
            Message.ERR_FORTRESS_NAME_ALREADY_EXIST.send(sender,fortress.get().getFormattedName());
            return;
        }

        if (FortressCreationService.getInstance().isPlayerInFortressCreationMode(sender)) {
            Message.ERR_CREATION_MODE.send(sender);
            return;
        }

        Message.CREATION_MODE_MSG_1.send(sender);

        FortressCreationService.getInstance().addPlayerToFortressCreationMode(sender, fortressName, fortressOwner);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {

            List<String> list = new ArrayList<>();
            for (Town town : TownyAPI.getInstance().getDataSource().getTowns())
                list.add(town.getName());


            return list;
        }
        return null;
    }
}
