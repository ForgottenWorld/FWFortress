package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return "reload";
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
    public void perform(Player sender, String[] args) {
        if (!sender.hasPermission("fwfortress.admin")) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_PERMISSION));
            return;
        }

        if (!BattleService.getInstance().getCurrentBattle().isEmpty()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Ci sono battaglie in corso ..."));
            return;
        }

        SettingsHandler.getInstance().reload();
        sender.sendMessage(ChatFormatter.formatSuccessMessage("Settings e fortezze ricaricati"));

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
