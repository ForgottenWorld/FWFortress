package me.architetto.fwfortress.command.user;

import com.palmergames.bukkit.towny.TownyAPI;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;
import me.architetto.fwfortress.localization.Message;
import me.architetto.fwfortress.util.MessageUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.LIST_CMD;
    }

    @Override
    public String getDescription() {
        return Message.LIST_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.LIST_CMD;
    }

    @Override
    public String getPermission() {
        return "fwfortress.list";
    }

    @Override
    public int getArgsRequired() {
        return 0;
    }

    @Override
    public void perform(Player sender, String[] args) {

        sender.sendMessage(MessageUtil.chatHeaderFortInfo());
        TownyAPI.getInstance().getDataSource().getTowns()
                .forEach(town -> {
                    List<Fortress> fortOwnedByTown = FortressService.getInstance().getFortressesOwnedByTown(town.getName());
                    sender.sendMessage("  --  " + town.getFormattedName()
                            + ChatColor.DARK_GREEN + " [#" + fortOwnedByTown.size() + "]" + ChatColor.RESET + " : "
                            + fortOwnedByTown.stream().map(Fortress::getFormattedName).collect(Collectors.toList()));
                });
        sender.sendMessage(MessageUtil.chatFooter());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
