package me.architetto.fwfortress.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager{

    private static ConfigManager single_inst = null;

    private ArrayList<FileConfiguration> customConfigs = new ArrayList<>();
    private ArrayList<String> configNames = new ArrayList<>();
    private Plugin plugin = null;

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration getConfig(String name){
        if (customConfigs.size() > 0) {
            for (FileConfiguration conf : customConfigs) {
                if (conf.getName().equalsIgnoreCase(name)) {
                    return conf;
                }
            }
        }
        return createNewCustomConfig(name);
    }

    public void reloadConfigs(){
        customConfigs.clear();
        configNames.clear();
    }

    private FileConfiguration createNewCustomConfig(String name) {
        FileConfiguration fileConfiguration;
        File configFile = new File(plugin.getDataFolder(), name);
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            plugin.saveResource(name, false);
        }

        fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(configFile);
            customConfigs.add(fileConfiguration);
            configNames.add(name);
            return fileConfiguration;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean setData(FileConfiguration conf, String path, Object data){
        conf.set(path, data);
        return saveData(conf);
    }

    public boolean saveData(FileConfiguration conf){
        try {
            conf.save(new File(plugin.getDataFolder(), configNames.get(customConfigs.indexOf(conf))));
        } catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public String getStringRaw(FileConfiguration conf, String path){
        if (!conf.contains(path))
            return null;
        return conf.getString(path);
    }

    public int getInt(FileConfiguration conf, String path){
        if (!conf.contains(path))
            return 0;
        return conf.getInt(path);
    }

    public double getDouble(FileConfiguration conf, String path){
        if (!conf.contains(path))
            return 0;
        return conf.getDouble(path);
    }

    public long getLong(FileConfiguration conf, String path) {
        if (!conf.contains(path))
            return 0;
        return conf.getLong(path);
    }

    public boolean getBoolean(FileConfiguration conf, String path){
        if (!conf.contains(path))
            return false;
        return conf.getBoolean(path);
    }

    public List<?> getList(FileConfiguration conf, String path){
        if (!conf.contains(path))
            return null;
        return conf.getList(path);
    }

    public boolean addLocation(FileConfiguration conf, Location location, String path){
        conf.set(String.format("%s.world", path), location.getWorld().getName());
        conf.set(String.format("%s.x", path), location.getX());
        conf.set(String.format("%s.y", path), location.getY());
        conf.set(String.format("%s.z", path), location.getZ());

        return saveData(conf);
    }

    public Location getLocation(FileConfiguration conf, String path) {
        if (!conf.contains(path))
            return null;

        String worldName = getStringRaw(conf, String.format("%s.world", path));

        if (worldName == null)
            return null;

        World world = Bukkit.getWorld(worldName);
        int x = getInt(conf, String.format("%s.x", path));
        int y = getInt(conf, String.format("%s.y", path));
        int z = getInt(conf, String.format("%s.z", path));

        return new Location(world, x, y, z);
    }

    private ConfigManager() {
    }

    public static ConfigManager getInstance() {
        if (single_inst == null) {
            single_inst = new ConfigManager();
        }
        return single_inst;
    }
}