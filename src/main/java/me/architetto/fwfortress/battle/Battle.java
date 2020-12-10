package me.architetto.fwfortress.battle;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.architetto.fwfortress.FWFortress;
import me.architetto.fwfortress.battle.util.Countdown;
import me.architetto.fwfortress.fortress.Fortress;
import me.architetto.fwfortress.util.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Battle {

    //WIP WIP WIP WIP WIP WIP WIP WIP WIP

    private Fortress fortress;

    private BoundingBox greenArea;
    private BoundingBox blueArea;

    private List<UUID> invaders;
    private List<UUID> defenders;

    private String enemyTown;

    //private BossBar bossBar; bossBar o scoreBoard ? Preferisco la prima

    private int fortressHP;

    private int battleTaskID;

    public Battle(Fortress fortress, List<UUID> invaders, String enemyTown) {

        this.fortress = fortress;

        this.greenArea = fortress.getGreenBoundingBox();
        this.blueArea = fortress.getBlueBoundingBox();

        this.fortressHP = fortress.getFortressHP();

        this.invaders = invaders;
        this.defenders = getDefenders(fortress.getCurrentOwner());

        this.enemyTown = enemyTown;

    }

    private List<UUID> getDefenders(String townName) {
        Optional<Town> town = TownyAPI.getInstance().getDataSource().getTowns().stream().findFirst().filter(t -> t.getName().equals(townName));
        List<UUID> defendersList = new ArrayList<>();

        if (town.isPresent()) {
            Town t = town.get();
            t.getResidents().forEach(resident -> defendersList.add(resident.getPlayer().getUniqueId()));
        }
        return defendersList;
    }

    public List<UUID> getInvaders() {
        return new ArrayList<>(this.invaders);
    }

    private void initBattle() {
        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class),5,
                () -> {
            //todo: messaggio di avvertimento alla citta' proprietaria della fortezza
                    sendMessageToDefenders("un gruppo di soldati provenienti da " + enemyTown + "si sta' avvicinando alla fortezza " + fortress.getFortressName());
                    //todo: messaggio al gruppo di invasori
                    sendMessageToInvaders("le truppe difensive della fortezza sono state allertate ! Preparatevi alla battaglia");
                    //todo: startare gia' da qui il check della posizione degli invasori ?


                },
                () -> {
            //todo: metodo startBattle()

                    //todo: mandare messaggio alla town owner della fortezza
                    sendMessageToDefenders("Le unità nemiche stanno mettendo la fortezza a ferro e fuoco, presto corri a proteggerla!");

                    //todo: mandare messaggio al gruppo di invasori
                    sendMessageToInvaders("il dado e' tratto, nel futuro puo' esserci solo la vittoria o la morte");

                },
                (s) -> {
            //todo: altri messaggi alla città propretaria della portezza
                    if (s.getSecondsLeft() == 5)
                        sendMessageToDefenders("le unità nemiche hanno fatto breccia nelle mura difensive di " + fortress.getFortressName());


                });
    }

    private void startBattle() {
        //todo
        Countdown countdown = new Countdown(FWFortress.getPlugin(FWFortress.class),1800, //todo: durata della battaglia settabile da config
                () -> {
            //START BATTLE LOGIC


                    //todo: boosbar ?

                },
                () -> {
            //END BATLLE LOGIC
                    //todo: messaggio ai difensori
                    sendMessageToDefenders("La battaglia è terminata ! " + fortress.getFortressName() + " e' stata danneggiata ma è salva !");
                    //todo: aggiornamento degli hp della fortezza !!va anche salvato su file ovviamente !!
                    //fortress.setFortressHP(this.fortressHP);


                },
                (s) -> {
            //EVERY SECOND
                    this.fortressHP -= getInvadersInsideGreenArea();

                    if (this.fortressHP <= 0) {
                        //todo: terminare la task
                        //todo: resettare hp della fortezza

                    }

                });
        this.battleTaskID = countdown.scheduleTimer();
    }

    public void stopBattle() {
        Bukkit.getScheduler().cancelTask(this.battleTaskID);
        //todo
    }

    private void sendMessageToInvaders(String msg) {
        for (UUID uuid : new ArrayList<>(invaders)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                player.sendMessage(ChatFormatter.formatYellowMessage(msg));
        }
    }

    private void sendMessageToDefenders(String msg) {
        for (UUID uuid : new ArrayList<>(defenders)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline())
                player.sendMessage(ChatFormatter.formatYellowMessage(msg));
        }
    }

    public void removeInvaders(UUID uuid) {
        this.invaders.remove(uuid);
        //todo check che controlla se ci sono altri invasori
    }

    private void checkInvadersPosition() {

        new BukkitRunnable() {

            @Override
            public void run() {
                if (invaders.isEmpty())
                    this.cancel();

                    new ArrayList<>(invaders).forEach(uuid -> {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null && !blueArea.contains(player.getLocation().toVector()))
                            removeInvaders(uuid);
                        //todo: drop dell'inventario ?
                        //todo: messaggio al player ?
                    });
            }
        }.runTaskTimer(FWFortress.plugin,0L,10L);

    }

    private Integer getInvadersInsideGreenArea() {

        int count = 0;

        for (UUID uuid : new ArrayList<>(invaders)) {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null)
                continue;

            if (this.greenArea.contains(player.getLocation().toVector()))
                count++;

        }
        return count;

    }

}
