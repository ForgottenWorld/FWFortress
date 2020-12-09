package me.architetto.fwfortress;

import me.architetto.fwfortress.command.CommandManager;
import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.listener.FortressCreationListener;
import me.architetto.fwfortress.listener.PlayerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class FWFortress extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        ConfigManager.getInstance().setPlugin(this);

        loadCommands();

        loadListener();

        //ConfigManager.getInstance().getConfig("Fortress.yml");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadCommands() {

        Objects.requireNonNull(getCommand("fwfortress")).setExecutor(new CommandManager());

    }

    public void loadListener() {

        getServer().getPluginManager().registerEvents(new FortressCreationListener(),this);
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);



    }


}
