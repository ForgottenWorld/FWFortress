package me.architetto.fwfortress.util.localization;

import me.architetto.fwfortress.config.ConfigManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class LocalizationManager {

    private static LocalizationManager localizationManager;

    private Map<String, String> strings;

    private LocalizationManager() {
        if (localizationManager != null){
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.strings = new HashMap<>();

    }

    public static LocalizationManager getInstance() {
        if(localizationManager == null) {
            localizationManager = new LocalizationManager();
        }
        return localizationManager;
    }

    public void loadLanguageFile() {

        FileConfiguration localization = ConfigManager.getInstance().getConfig("Messages.yml");

        for (String key : localization.getConfigurationSection("strings").getKeys(false)) {
            this.strings.put(key, localization.getString("strings." + key));
        }
    }

    public void reload() {
        this.strings.clear();
        loadLanguageFile();
    }

    public String localize(String key) {
        return this.strings.containsKey(key) ? this.strings.get(key) : ChatColor.RED + "No translation present for " + key;
    }

}
