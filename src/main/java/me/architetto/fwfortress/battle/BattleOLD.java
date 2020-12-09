package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.battle.util.Countdown;
import me.architetto.fwfortress.fortress.Fortress;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BattleOLD {

    //private final Fortress fortress;

    //private final BoundingBox greenArea;
    //private final BoundingBox blueArea;

    //private final String enemyTown;

    private List<UUID> defensor;
    private List<UUID> enemies;

    private int forthp;

    private int battleCountdownTaskID;

    public BattleOLD(Fortress fortress, List<UUID> enemies, String enemyTown) {

        /*
        this.fortress = fortress;

        this.enemyTown = enemyTown;

        this.enemies = enemies;

        this.greenArea = fortress.getGreenBoundingBox();

        this.blueArea = fortress.getBlueBoundingBox();

        this.forthp = 1000; //da config // si può giocare molto con gli hp di una fortezza

        Countdown countdown = new Countdown(FWFortress.plugin,1800,
                () -> {


                    try {
                        TownyAPI.getInstance().getDataSource().getTown(fortress.getCurrentOwner()).getResidents().forEach(r -> {
                            Player player = r.getPlayer();
                            player.sendMessage("La tua fortezza " + fortress.getFortName() + " e' sotto attacco!");
                        });
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }

                },
                () -> {


            //se arriva qui l'assalitore non ha avuto successo ed ha quindi perso
                    try {
                        TownyAPI.getInstance().getDataSource().getTown(this.enemyTown).getResidents().forEach(r -> {
                            Player player = r.getPlayer();
                            player.sendMessage("L'attacco alla fortezza " + fortress.getFortName() + " è stato sventato!");
                        });
                    } catch (NotRegisteredException e) {
                        e.printStackTrace();
                    }

                },
                (t) -> {

            if (enemies.size() == 0) {
                //todo: gli attaccanti sono stati sconfitti
            }

            if (getPlayersInsideGreenArea() >= 2)
                this.forthp--;

            if (forthp <= 0) {
                //todo: gli attaccanti hanno vinto
                Bukkit.getScheduler().cancelTask(this.battleCountdownTaskID);
            }

                });
        this.battleCountdownTaskID = countdown.scheduleTimer();

         */

    }

    private Integer getPlayersInsideGreenArea() {

        int amount = 0;

        for (UUID uuid : getEnemies()) {

            Player player = Bukkit.getPlayer(uuid);

            if (player == null)
                continue;

            /*
            if (!blueArea.contains(player.getLocation().getDirection())) {
                kickOutBorderPlayer(uuid);
                continue;
            }


            if (greenArea.contains(player.getLocation().getDirection())) {
                amount++;
            }

             */
        }

        return amount;

    }

    private List<UUID> getEnemies() {
        return new ArrayList<>(this.enemies);
    }

    private void kickOutBorderPlayer(UUID uuid) {
        this.enemies.remove(uuid);
    }


}
