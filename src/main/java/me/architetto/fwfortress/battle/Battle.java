package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import me.architetto.fwfortress.fortress.Fortress;
import org.bukkit.boss.BossBar;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Battle {

    private BoundingBox greenArea;
    private BoundingBox blueArea;

    private List<UUID> invaders;
    private List<UUID> defenders;

    //private BossBar bossBar; bossBar o scoreBoard ? Preferisco la prima

    private int fortressHP;

    private int battleTaskID;

    public Battle(Fortress fortress, List<UUID> invaders, String invadersNation) {

        this.greenArea = fortress.getGreenBoundingBox();
        this.blueArea = fortress.getBlueBoundingBox();

        this.fortressHP = fortress.getFortressHP();

        this.invaders = invaders;
        this.defenders = getDefenders(invadersNation);

    }

    private List<UUID> getDefenders(String nationName) {
        Optional<Nation> nation = TownyAPI.getInstance().getDataSource().getNations().stream().findFirst().filter(n -> n.getName().equals(nationName));
        List<UUID> defendersList = new ArrayList<>();

        if (nation.isPresent()) {
            Nation n = nation.get();
            n.getResidents().forEach(resident -> defendersList.add(resident.getPlayer().getUniqueId()));
        }
        return defendersList;
    }

    private void startBattle() {
        //todo
    }

    public void stopBattle() {
        //todo
    }
}
