package me.architetto.fwfortress.command.admin;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateCommand extends SubCommand {
    @Override
    public String getName() {
        return "create";
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
    public void perform(Player sender, String[] args) {
        if (!sender.hasPermission("fwfortress.admin")) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_PERMISSION));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.NOT_ENOUGHT_ARGUMENTS));
            return;
        }

        String fortressOwner = args[1];

        //check if town exist
        if (TownyAPI.getInstance().getDataSource().getTowns().stream().noneMatch(t -> t.getName().equals(args[1]))) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Non essiste nessuna citta' con questo nome : ")
                    + ChatColor.YELLOW + fortressOwner);
            return;
        }

        FortressService fortressService = FortressService.getInstance();

        //check if is the town's first fortress
        if (fortressService.getFortressContainer().values().stream().anyMatch(f -> f.getFirstOwner().equals(args[1]))) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Questa citta' ha gia' costruito una fortezza")
                    + ChatColor.YELLOW + fortressOwner);
            return;
        }

        String fortressName = args[2]; //todo: il nome possono essere composti da più parole ? (MEGLIO DI NO)

        //check if this fortress's name already exist
        if (fortressService.getFortressContainer().containsKey(fortressName)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Esiste gia' una fortezza con questo nome"));
            return;
        }

        if (fortressService.isPlayerInCreationMode(sender)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Stai gia' creando una fortezza"));
            return;
        }

        int minDistance = SettingsHandler.getInstance().getDistanceBetweenFortresses();

        if (fortressService.getFortressContainer().values().stream()
                .filter(fortress -> fortress.getWorldName().equals(sender.getWorld().getName()))
                .anyMatch(fortress -> fortress.getFortressVector().distance(sender.getLocation().toVector()) < minDistance)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Sei troppo vicino ad un'altra fortezza"));

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
