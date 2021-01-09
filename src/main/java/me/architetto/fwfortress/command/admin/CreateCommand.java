package me.architetto.fwfortress.command.admin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.util.cmd.CommandPermission;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.CREATE_CMD;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return "/fwfortress create <fortress_name> <fortress_firstOwner>";
    }

    @Override
    public String getPermission() {
        return CommandPermission.FORTRESS_ADMIN_PERM;
    }

    @Override
    public int getArgsRequired() {
        return 3;
    }

    @Override
    public void perform(Player sender, String[] args) {

        String fortressOwner = args[1];

        //check if town exist
        if (TownyAPI.getInstance().getDataSource().getTowns().stream().noneMatch(t -> t.getName().equals(args[1]))) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_CITY_NAME)
                    + ChatColor.YELLOW + fortressOwner);
            return;
        }

        FortressService fortressService = FortressService.getInstance();

        //check if is the town's first fortress
        if (fortressService.getFortressContainer().values().stream().anyMatch(f -> f.getFirstOwner().equals(args[1]))) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_FIRST_FORTRESS)
                    + ChatColor.YELLOW + fortressOwner);
            return;
        }

        String fortressName = String.join("_", Arrays.copyOfRange(args, 2, args.length));


        //check if this fortress's name already exist
        if (fortressService.getFortressContainer().containsKey(fortressName)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Esiste gia' una fortezza con questo nome"));
            return;
        }

        if (fortressService.isPlayerInCreationMode(sender)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Stai gia' creando una fortezza"));
            return;
        }

        sender.sendMessage(ChatFormatter.formatMessage(ChatColor.AQUA
                + "Indica la posizione del chunk conquistabile ... (CLICK DX con STICK equipaggiato)"));

        fortressService.addPlayerToFortressCreation(sender, fortressName, fortressOwner);

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
