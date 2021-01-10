package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.ChatFormatter;
import me.architetto.fwfortress.util.Messages;
import me.architetto.fwfortress.util.cmd.CommandDescription;
import me.architetto.fwfortress.util.cmd.CommandName;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.RELOAD_CMD;
    }

    @Override
    public String getDescription() {
        return CommandDescription.RELOAD_CMD_DESCRIPTION;
    }

    @Override
    public String getSyntax() {
        return "/fwfortress " + CommandName.RELOAD_CMD;
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

        if (!BattleService.getInstance().getCurrentBattle().isEmpty()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage(Messages.ERR_RELOAD1));
            return;
        }

        SettingsHandler.getInstance().reload();
        sender.sendMessage(ChatFormatter.formatSuccessMessage(Messages.OK_RELOAD));

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
