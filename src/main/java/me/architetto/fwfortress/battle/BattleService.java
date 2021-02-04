package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.config.SettingsHandler;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BattleService {

    private static BattleService battleService;

    private HashMap<String,Battle> battleContainer;

    private BattleService() {
        if(battleService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }

        this.battleContainer = new HashMap<>();
    }

    public static BattleService getInstance() {
        if(battleService == null) {
            battleService = new BattleService();
        }
        return battleService;
    }

    public void startBattle(Fortress fortress, List<UUID> enemies, Town enemyTown) {

        Battle battle = new Battle(fortress, enemies, enemyTown);

        this.battleContainer.put(fortress.getFortressName(), battle);

        battle.firstStepBattle();

    }

    public boolean isOccupied(String fortressName) {
        return battleContainer.values().stream().anyMatch(battle -> battle.getFortress().getFortressName().equals(fortressName));
    }

    public List<String> getOccupiedFortresses() {
        return battleContainer.values().stream().map(Battle::getFortress).map(Fortress::getFortressName).collect(Collectors.toList());
    }

    public void resolveBattle(Fortress fortress, String newOwner, int fortressHP) {

        if (!fortress.getCurrentOwner().equals(newOwner)) {

            fortress.setCurrentOwner(newOwner);
            fortress.setCurrentHP(SettingsHandler.getInstance().getFortressHP());

        } else {

            fortress.setCurrentHP(fortressHP);

        }

        fortress.setLastBattle(System.currentTimeMillis());

        stopBattle(fortress.getFortressName());

        this.battleContainer.remove(fortress.getFortressName());

        FortressService.getInstance().saveFortress(fortress);

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
