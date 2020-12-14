package me.architetto.fwfortress;

import me.architetto.fwfortress.command.CommandManager;
import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.config.SettingsHandler;
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

        loadSettings();

        loadCommands();

        loadListener();

        loadFortress();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void loadSettings() {
        SettingsHandler.getInstance().loadSettings();
    }

    public void loadCommands() {

        Objects.requireNonNull(getCommand("fwfortress")).setExecutor(new CommandManager());

    }

    public void loadListener() {

        getServer().getPluginManager().registerEvents(new FortressCreationListener(),this);
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);

    }

    public void loadFortress() {
        SettingsHandler.getInstance().loadFortress();
    }


}
