package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.fortress.FortressService;

import java.util.*;
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

    public void startBattle(Fortress fortress, Town enemyTown, Set<UUID> enemies, Town defendersTown) {

        Battle battle = new Battle(fortress, enemyTown, enemies, defendersTown);

        this.battleContainer.put(fortress.getName(), battle);

        battle.firstPhase();

    }

    public boolean isOccupied(String fortressName) {
        return battleContainer.values().stream().anyMatch(battle -> battle.getFortress().getName().equals(fortressName));
    }

    public List<String> getOccupiedFortresses() {
        return battleContainer.values().stream().map(Battle::getFortress).map(Fortress::getName).collect(Collectors.toList());
    }

    public void resolveBattle(Fortress fortress, String newOwner, long exp) {

        if (!fortress.getOwner().equals(newOwner))
            fortress.setOwner(newOwner);

        fortress.setExperience(fortress.getExperience() + exp);

        fortress.setLastBattle(System.currentTimeMillis());

        stopBattle(fortress.getName());

        FortressService.getInstance().updateFortress(fortress);

    }

    public void stopBattle(String fortressName) {
        Battle battle = this.battleContainer.get(fortressName);

        if (Objects.isNull(battle))
            return;

        battle.stopBattle();

        this.battleContainer.remove(fortressName);
    }

    public Set<Battle> getCurrentBattle() {
        return new HashSet<>(battleContainer.values());
    }
}
