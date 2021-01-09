package me.architetto.fwfortress.command.user;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.util.cmd.CommandPermission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfoCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.INFO_CMD;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public String getPermission() {
        return CommandPermission.FORTRESS_USER_PERM;
    }

    @Override
    public int getArgsRequired() {
        return 2;
    }

    @Override
    public void perform(Player sender, String[] args) {

        Optional<Fortress> fortress = FortressService.getInstance().getFortress(args[1]);

        if (!fortress.isPresent()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Nessuna fortezza con questo nome"));
            return;
        }

        sender.sendMessage(ChatFormatter.chatHeaderFortInfo());
        sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "NOME FORTEZZA : " + ChatColor.YELLOW + fortress.get().getFortressName()));
        sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "PRIMO PROPRIETARIO : " + ChatColor.YELLOW + fortress.get().getFirstOwner()));
        sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "PROPRIETARIO ATTUALE : " + ChatColor.YELLOW + fortress.get().getCurrentOwner()));
        sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "HP : " + ChatColor.YELLOW + fortress.get().getCurrentHP()));
        sender.sendMessage(ChatFormatter.formatListMessage(ChatColor.AQUA + "POSIZIONE : " + fortress.get().getFormattedLocation()));
        sender.sendMessage(ChatFormatter.chatFooter());

        //todo: effetto visivo ?

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {

        if (args.length == 2) {
            return new ArrayList<>(FortressService.getInstance().getFortressContainer().keySet());
        }

        return null;
    }
}
