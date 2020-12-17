package me.architetto.fwfortress.battle;

import me.architetto.fwfortress.config.ConfigManager;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class BattleService {

    //WIP WIP WIP WIP

    private static BattleService battleService;

    private HashMap<String,Battle> battleContainer;

    private BattleService() {
        if(battleService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        //inizializzare le variabili

        this.battleContainer = new HashMap<>();

    }

    public static BattleService getInstance() {
        if(battleService == null) {
            battleService = new BattleService();
        }
        return battleService;
    }

    public void startNewBattle(Fortress fortress, List<UUID> enemies, String enemyTown) {

        Battle battle = new Battle(fortress, enemies, enemyTown);

        this.battleContainer.put(fortress.getFortressName(), battle);

        battle.initBattle();

    }

    public boolean isOccupied(String fortressName) {
        return battleContainer.values().stream().anyMatch(battle -> battle.getFortressInBattle().getFortressName().equals(fortressName));
    }

    public void resolveBattle(Fortress fortress, String newOwner, int fortressHP) {

        ConfigManager configManager = ConfigManager.getInstance();

        if (!fortress.getCurrentOwner().equals(newOwner)) {

            fortress.setCurrentOwner(newOwner);
            fortress.setFortressHP(SettingsHandler.getInstance().getFortressHP());
            configManager.setData(configManager.getConfig("Fortress.yml"), fortress.getFortressName() + ".OWNER", newOwner);
            configManager.setData(configManager.getConfig("Fortress.yml"), fortress.getFortressName()
                    + ".FORTRESS_HP", SettingsHandler.getInstance().getFortressHP());

        } else {
            fortress.setFortressHP(fortressHP);
            configManager.setData(configManager.getConfig("Fortress.yml"), fortress.getFortressName()
                    + ".FORTRESS_HP", fortressHP);

        }

        fortress.setLastBattle(System.currentTimeMillis());
        configManager.setData(configManager.getConfig("Fortress.yml"), fortress.getFortressName()
                + ".LAST_BATTLE", fortress.getLastBattle());

        stopBattle(fortress.getFortressName());

        this.battleContainer.remove(fortress.getFortressName());

    }

    public void stopBattle(String fortressName) {
        Battle battle = this.battleContainer.get(fortressName);
        battle.stopBattle();
        this.battleContainer.remove(fortressName);
    }

    public List<Battle> getCurrentBattle() {
        return new ArrayList<>(this.battleContainer.values());
    }

    //todo

}
