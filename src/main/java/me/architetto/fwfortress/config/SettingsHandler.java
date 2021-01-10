package me.architetto.fwfortress.config;

import me.architetto.fwfortress.fortress.FortressService;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SettingsHandler {

    private static SettingsHandler instance;

    private int fortressHP;
    private int battleTimeLimit;
    private int startBattleDelay;
    private int distanceBetweenFortresses;
    private int maxGroundDistance;
    private int fortressBorderDamage;
    private int minInvaders;
    private int maxDamageForSeconds;

    private int repairPercentage;

    private long battleCooldown;
    private long repairCooldown;

    private List<String> date;
    private List<Integer> time;

    private boolean invadeAlliedFortress;

    private boolean disableInvade;

    private SettingsHandler() {
        if(instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        //inizializzare
        this.disableInvade = false;

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
        this.distanceBetweenFortresses = configManager.getInt(configManager.getConfig("Settings.yml"),"DISTANCE_BETWEEN_FORTRESSES");
        this.fortressBorderDamage = configManager.getInt(configManager.getConfig("Settings.yml"),"FORTRESS_BORDER_DAMAGE");
        this.maxGroundDistance = configManager.getInt(configManager.getConfig("Settings.yml"),"MAX_GROUND_DISTANCE");
        this.maxDamageForSeconds = configManager.getInt(configManager.getConfig("Settings.yml"),"MAX_DAMAGE_FOR_SECOND");

        this.repairPercentage = configManager.getInt(configManager.getConfig("Settings.yml"),"REPAIR_PERCENTAGE");

        this.minInvaders = configManager.getInt(configManager.getConfig("Settings.yml"),"MIN_INVADERS");

        this.battleCooldown = TimeUnit.HOURS.toMillis(configManager.getLong(configManager.getConfig("Settings.yml"),"BATTLE_COOLDOWN"));
        this.repairCooldown = TimeUnit.HOURS.toMillis(configManager.getLong(configManager.getConfig("Settings.yml"),"REPAIR_COOLDOWN"));


        this.date = (List<String>) configManager.getList(configManager.getConfig("Settings.yml"),"DATE"); //OK
        this.time = (List<Integer>) configManager.getList(configManager.getConfig("Settings.yml"),"TIME_RANGE"); //OK

        this.invadeAlliedFortress = configManager.getBoolean(configManager.getConfig("Settings.yml"),"INVADE_ALLIED_FORTRESS");

    }

    public void loadFortress() {

        ConfigManager configManager = ConfigManager.getInstance();

        ConfigurationSection configurationSection = configManager.getConfig("Fortress.yml")
                .getConfigurationSection("");

        if (configurationSection == null)
            return;

        FortressService fortressService = FortressService.getInstance();

        for (String fortressName : configurationSection.getKeys(false)) {

            fortressService.loadFortress(fortressName,
                    configManager.getStringRaw(configManager.getConfig("Fortress.yml"),fortressName + ".FIRTS_OWNER"),
                    configManager.getStringRaw(configManager.getConfig("Fortress.yml"),fortressName + ".OWNER"),
                    configManager.getLocation(configManager.getConfig("Fortress.yml"), fortressName + ".FORTRESS_POSITION"),
                    configManager.getInt(configManager.getConfig("Fortress.yml"), fortressName + ".FORTRESS_HP"),
                    configManager.getLong(configManager.getConfig("Fortress.yml"), fortressName + ".LAST_BATTLE"),
                    configManager.getLong(configManager.getConfig("Fortress.yml"), fortressName + ".LAST_REPAIR"),
                    (List<Long>) configManager.getList(configManager.getConfig("Fortress.yml"), fortressName + ".CHUNKKEYS"));

        }
    }

    public void reload() {
        this.date.clear();
        this.time.clear();

        loadSettings();

        FortressService.getInstance().clearFortressContainer();
        loadFortress();
    }

    public int getFortressHP() { return this.fortressHP; }

    public void setFortressHP(int fortressHP) { this.fortressHP = fortressHP; }

    public int getFortressBorderDamage() { return this.fortressBorderDamage; }

    public void setFortressBorderDamage(int fortressBorderDamage) { this.fortressBorderDamage = fortressBorderDamage; }

    public int getMaxDamageForSeconds() { return this.maxDamageForSeconds; }

    public void setMaxDamageForSeconds(int maxDamageForSeconds) { this.maxDamageForSeconds = maxDamageForSeconds; }

    public int getBattleTimeLimit() { return this.battleTimeLimit; }

    public void setBattleTimeLimit(int battleTimeLimit) { this.battleTimeLimit = battleTimeLimit; }

    public int getStartBattleDelay() { return this.startBattleDelay; }

    public void setStartBattleDelay(int startBattleDelay) { this.startBattleDelay = startBattleDelay; }

    public int getMinInvaders() {
        return this.minInvaders;
    }

    public void setMinInvaders(int minInvaders) { this.minInvaders = minInvaders; }

    public long getBattleCooldown() { return this.battleCooldown; }

    public void setBattleCooldown(long battleCooldown) { this.battleCooldown = battleCooldown; }

    public int getRepairPercentage() { return this.repairPercentage; }

    public void setRepairPercentage(int repairPercentage) { this.repairPercentage = repairPercentage; }

    public long getRepairCooldown() { return this.repairCooldown; }

    public void setRepairCooldown(long repairCooldown) { this.repairCooldown = repairCooldown; }

    public int getDistanceBetweenFortresses() { return this.distanceBetweenFortresses; }

    public void setDistanceBetweenFortresses(int distanceBetweenFortresses) { this.distanceBetweenFortresses = distanceBetweenFortresses; }

    public int getMaxGroundDistance() { return this.maxGroundDistance; }

    public void setMaxGroundDistance(int maxGroundDistance) { this.maxGroundDistance = maxGroundDistance; }

    public List<String> getDate() { return this.date; }

    public List<Integer> getTime() { return this.time; }

    public boolean allowInvadeAlliedFortress() { return this.invadeAlliedFortress; }

    public void setInvadeAlliedFortress(boolean invadeAlliedFortress) { this.invadeAlliedFortress = invadeAlliedFortress; }

    public boolean isInvadeDisabled() { return this.disableInvade; }

    public void setDisableInvade(boolean disableInvade) { this.disableInvade = disableInvade; }



}
