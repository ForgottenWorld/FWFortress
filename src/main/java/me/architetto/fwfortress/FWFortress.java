package me.architetto.fwfortress;

import me.architetto.fwfortress.command.CommandManager;
import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.listener.FortressCreationListener;
import me.architetto.fwfortress.listener.PlayerListener;
import me.architetto.fwfortress.listener.TownListener;

import me.architetto.fwfortress.localization.LocalizationManager;
import me.architetto.fwfortress.task.PositionTask;
import me.architetto.fwfortress.task.TaskService;
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

        loadLocalization();

        loadSettings();

        loadCommands();

        loadListener();

        loadFortress();

        TaskService.getInstance().schedulePositionTask();


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
        getServer().getPluginManager().registerEvents(new TownListener(),this);

    }

    public void loadFortress() {
        SettingsHandler.getInstance().loadFortress();
    }

    public void loadLocalization() {
        LocalizationManager.getInstance().loadLanguageFile();
    }


}
