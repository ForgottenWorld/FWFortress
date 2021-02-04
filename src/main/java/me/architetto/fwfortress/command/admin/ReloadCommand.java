package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.SubCommand;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.util.cmd.CommandName;
import me.architetto.fwfortress.localization.LocalizationManager;
import me.architetto.fwfortress.localization.Message;
import org.bukkit.entity.Player;

import java.util.List;

public class ReloadCommand extends SubCommand {
    @Override
    public String getName() {
        return CommandName.RELOAD_CMD;
    }

    @Override
    public String getDescription() {
        return Message.RELOAD_COMMAND.asString();
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
            Message.ERR_RELOAD.send(sender);
            return;
        }

        SettingsHandler.getInstance().reload();
        LocalizationManager.getInstance().reload();

        Message.SUCCESS_RELOAD.send(sender);

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
