package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleCommand extends SubCommand {
    @Override
    public String getName() {
        return "manage";
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

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        if (settingsHandler.isDisableInvade()) {
            settingsHandler.setDisableInvade(false);
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Battaglie abilitate ..."));

        } else {
            settingsHandler.setDisableInvade(true);
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Battaglie disabilitate ..."));
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
