package me.architetto.fwfortress.command.admin;

import me.architetto.fwfortress.command.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class EditCommand extends SubCommand {
    @Override
    public String getName() {
        return null;
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
        return null;
    }

    @Override
    public int getArgsRequired() {
        return 4;
    }

    @Override
    public void perform(Player sender, String[] args) {

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
