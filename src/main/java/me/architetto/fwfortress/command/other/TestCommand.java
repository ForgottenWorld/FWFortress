package me.architetto.fwfortress.command.other;

import me.architetto.fwfortress.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
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

        Chunk c = sender.getLocation().getChunk();

        int blockX = c.getX() << 4; //c.getX() * 16
        int blockZ = c.getZ() << 4; //c.getZ() * 16
        int blockY = sender.getLocation().getBlockY() + 1;

        //Gets the location of each corner
        Location g1 = c.getWorld().getBlockAt(blockX,blockY,blockZ).getLocation();
        //Bukkit.getConsoleSender().sendMessage(g1.getX() + " // " + g1.getY() + " // " + g1.getZ());
        Location g2 = c.getWorld().getBlockAt(blockX +16,blockY, blockZ +16).getLocation();
        //Bukkit.getConsoleSender().sendMessage(g2.getX() + " // " + g2.getY() + " // " + g2.getZ());


        Location b1 = c.getWorld().getBlockAt(blockX -16,blockY,blockZ -16).getLocation();
        Location b2 = c.getWorld().getBlockAt(blockX +32,blockY,blockZ +32).getLocation();

        g1.getWorld().spawnParticle(Particle.REDSTONE, g1, 2, dustOptions);
        g2.getWorld().spawnParticle(Particle.REDSTONE, g2, 2, dustOptions);

        b1.getWorld().spawnParticle(Particle.REDSTONE, b1, 2, dustOptions);
        b2.getWorld().spawnParticle(Particle.REDSTONE, b2, 2, dustOptions);



    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
