package me.architetto.fwfortress.command.other;

import me.architetto.fwfortress.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

public class TestCommand extends SubCommand {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getSyntax() {
        return null;
    }

    final Particle.DustOptions dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(200, 0, 0),10);

    @Override
    public void perform(Player sender, String[] args) {

        sender.sendMessage("SYSTEM MILLISECONDS : + " + System.currentTimeMillis());

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
