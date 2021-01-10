package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.cmd.CommandDescription;
import me.architetto.fwfortress.util.cmd.CommandName;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.TOGGLE_CMD;
    }

    @Override
    public String getDescription() {
        return CommandDescription.TOGGLE_CMD_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.TOGGLE_CMD;
    }

    @Override
    public String getPermission() {
        return "fwfortress.admin";
    }

    @Override
    public int getArgsRequired() {
        return 0;
    }

    @Override
    public void perform(Player sender, String[] args) {

        SettingsHandler settingsHandler = SettingsHandler.getInstance();

        if (settingsHandler.isInvadeDisabled()) {
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
