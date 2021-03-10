package me.architetto.fwfortress;

import me.architetto.fwfortress.battle.Battle;
import me.architetto.fwfortress.battle.BattleService;
import me.architetto.fwfortress.command.CommandManager;
import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.listener.FortressCreationListener;
import me.architetto.fwfortress.listener.PlayerListener;
import me.architetto.fwfortress.listener.TownListener;

import me.architetto.fwfortress.localization.LocalizationManager;
import me.architetto.fwfortress.task.TaskService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        Bukkit.getConsoleSender().sendMessage("=====================[   FWFortress   ]======================");

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[]" + ChatColor.RESET + " Loading messages...");
        loadLocalization();

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[]" + ChatColor.RESET + " Loading settings...");
        loadSettings();

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[]" + ChatColor.RESET
                + " Loading commands and listeners...");
        loadCommands();

        loadListener();

        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[]" + ChatColor.RESET + " Loading fortresses...");
        loadFortress();

        TaskService.getInstance().schedulePositionTask();

        Bukkit.getConsoleSender().sendMessage("=============================================================");


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (!BattleService.getInstance().getCurrentBattle().isEmpty())
            BattleService.getInstance().getCurrentBattle().forEach(Battle::stopBattle);

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
