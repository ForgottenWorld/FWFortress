package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.util.cmd.CommandPermission;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.TOGGLE_CMD;
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
        return CommandPermission.FORTRESS_ADMIN_PERM;
    }

    @Override
    public int getArgsRequired() {
        return 0;
    }

    @Override
    public void perform(Player sender, String[] args) {

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
