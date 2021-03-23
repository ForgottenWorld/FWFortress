package me.architetto.fwfortress.config;

import me.architetto.fwfortress.fortress.FortressCreationService;
import me.architetto.fwfortress.fortress.FortressService;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SettingsHandler {

    private static SettingsHandler instance;

    private boolean fwechelon;

    private int fortressHP;
    private int battleTimeLimit;
    private int startBattleDelay;
    private int minFortressDistance;
    private int maxGroundDistance;
    private int fortressBorderDamage;
    private int minInvaders;
    private int maxDamageForSeconds;

    private long battleCountdown;

    private List<String> date;
    private List<Integer> time;

    private boolean invadeAlliedFortress;

    private boolean glowInvaders;
    private int glowPeriod;
    private int glowDuration;

    private SettingsHandler() {
        if(instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

    }

    public static SettingsHandler getInstance() {
        if(instance == null) {
            instance= new SettingsHandler();
        }
        return instance;
    }

    public void loadSettings() {

        ConfigManager configManager = ConfigManager.getInstance();

        this.fortressHP = configManager.getInt(configManager.getConfig("Settings.yml"),"FORTRESS_HP");
        this.battleTimeLimit = configManager.getInt(configManager.getConfig("Settings.yml"),"BATTLE_TIME_LIMIT");
        this.startBattleDelay = configManager.getInt(configManager.getConfig("Settings.yml"),"START_BATTLE_DELAY");
        this.minFortressDistance = configManager.getInt(configManager.getConfig("Settings.yml"),"MIN_FORTRESS_DISTANCE");
        this.fortressBorderDamage = configManager.getInt(configManager.getConfig("Settings.yml"),"FORTRESS_BORDER_DAMAGE");
        this.maxGroundDistance = configManager.getInt(configManager.getConfig("Settings.yml"),"MAX_GROUND_DISTANCE");
        this.maxDamageForSeconds = configManager.getInt(configManager.getConfig("Settings.yml"),"MAX_DAMAGE_FOR_SECOND");

        this.minInvaders = configManager.getInt(configManager.getConfig("Settings.yml"),"MIN_INVADERS");

        this.battleCountdown = TimeUnit.HOURS.toMillis(configManager.getLong(configManager.getConfig("Settings.yml"),"BATTLE_COOLDOWN"));

        this.date = (List<String>) configManager.getList(configManager.getConfig("Settings.yml"),"DATE"); //OK
        this.time = (List<Integer>) configManager.getList(configManager.getConfig("Settings.yml"),"TIME_RANGE"); //OK

        this.invadeAlliedFortress = configManager.getBoolean(configManager.getConfig("Settings.yml"),"ALLOW_INVADE_ALLIED_FORTRESS");

        this.glowInvaders = configManager.getBoolean(configManager.getConfig("Settings.yml"),"INVADERS_GLOWING_EFFECT.enable");
        this.glowPeriod = configManager.getInt(configManager.getConfig("Settings.yml"),"INVADERS_GLOWING_EFFECT.seconds_period");
        this.glowDuration = configManager.getInt(configManager.getConfig("Settings.yml"),"INVADERS_GLOWING_EFFECT.duration_tick");

    }

    public void loadFortress() {

        ConfigManager configManager = ConfigManager.getInstance();

        ConfigurationSection configurationSection = configManager.getConfig("Fortress.yml")
                .getConfigurationSection("");

        if (configurationSection == null)
            return;

        FortressCreationService fortressCreationService = FortressCreationService.getInstance();

        for (String fortressName : configurationSection.getKeys(false)) {

            fortressCreationService.loadFortress(fortressName,
                    configManager.getStringRaw(configManager.getConfig("Fortress.yml"),fortressName + ".OWNER"),
                    configManager.getLocation(configManager.getConfig("Fortress.yml"), fortressName + ".FORTRESS_POSITION"),
                    configManager.getLong(configManager.getConfig("Fortress.yml"), fortressName + ".LAST_BATTLE"),
                    configManager.getLong(configManager.getConfig("Fortress.yml"), fortressName + ".EXPERIENCE"),
                    (List<Long>) configManager.getList(configManager.getConfig("Fortress.yml"), fortressName + ".CHUNKKEYS"),
                    configManager.getBoolean(configManager.getConfig("Fortress.yml"), fortressName + ".ENABLED"));

        }
    }

    public void reload() {
        this.date.clear();
        this.time.clear();

        loadSettings();

        FortressService.getInstance().clearFortressContainer();
        loadFortress();
    }

    public boolean isFWEchelonLoaded() {
        return fwechelon;
    }

    public void setFwechelon(boolean enable) {
        fwechelon = enable;
    }

    public int getFortressHP() { return this.fortressHP; }

    public int getFortressBorderDamage() { return this.fortressBorderDamage; }

    public int getMaxDamageForSeconds() { return this.maxDamageForSeconds; }

    public int getBattleTimeLimit() { return this.battleTimeLimit; }

    public int getStartBattleDelay() { return this.startBattleDelay; }

    public int getMinInvaders() {
        return this.minInvaders;
    }

    public long getBattleCountdown() { return this.battleCountdown; }

    public int getMinFortressDistance() { return this.minFortressDistance; }

    public int getMaxGroundDistance() { return this.maxGroundDistance; }

    public List<String> getDate() { return this.date; }

    public List<Integer> getTime() { return this.time; }

    public boolean allowInvadeAlliedFortress() { return this.invadeAlliedFortress; }

    public boolean isGlowInvaders() { return this.glowInvaders; }

    public int getGlowPeriod() { return this.glowPeriod; }

    public int getGlowDuration() { return this.glowDuration; }




}
