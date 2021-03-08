package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.command.CommandName;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.TOGGLE_CMD;
    }

    @Override
    public String getDescription() {
        return Message.TOGGLE_COMMAND.asString();
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.TOGGLE_CMD;
    }

    @Override
    public String getPermission() {
        return "fwfortress.toggle";
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
            Message.TOGGLE_BATTLE_ENABLED.send(sender);

        } else {
            settingsHandler.setDisableInvade(true);
            Message.TOGGLE_BATTLE_DISABLED.send(sender);
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
